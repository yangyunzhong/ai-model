package org.dromara.model.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.model.domain.ModelExecLog;
import org.dromara.model.domain.ModelExecLogDetail;
import org.dromara.model.domain.ModelResult;
import org.dromara.model.mapper.ModelExecLogDetailMapper;
import org.dromara.model.mapper.ModelExecLogMapper;
import org.dromara.model.mapper.ModelResultMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模型执行编排器
 *
 * <p>一次执行的完整闭环：</p>
 * <ol>
 *     <li>创建 {@code model_exec_log}（状态=运行中）；</li>
 *     <li>按 {@code @Order} 选取第一个支持当前上下文的 {@link ModelExecutor}；</li>
 *     <li>执行并把逐设备结果写入 {@code model_result}；</li>
 *     <li>过程日志写入 {@code model_exec_log_detail}；</li>
 *     <li>回写 {@code model_exec_log} 的状态、耗时、成功/失败数。</li>
 * </ol>
 *
 * @author model-platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelExecOrchestrator {

    private static final DateTimeFormatter EXEC_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String STATUS_RUNNING = "运行中";
    private static final String STATUS_SUCCESS = "成功";
    private static final String STATUS_PARTIAL = "部分成功";
    private static final String STATUS_FAILED = "失败";
    private static final String STATUS_TIMEOUT = "超时";

    private final List<ModelExecutor> executors;
    private final ModelExecLogMapper execLogMapper;
    private final ModelResultMapper resultMapper;
    private final ModelExecLogDetailMapper detailMapper;

    /**
     * 执行一次模型运行并落库
     *
     * @param context 执行上下文
     * @return 执行记录ID（model_exec_log.id）
     */
    public String runOnce(ModelExecContext context) {
        ModelExecLog execLog = createRunningLog(context);
        long startMs = System.currentTimeMillis();
        int seq = appendLog(execLog.getId(), 1, "INFO",
            "开始执行模型「" + context.getModelName() + "」，触发方式：" + context.getTriggerType());

        ExecuteResult result;
        try {
            ModelExecutor executor = selectExecutor(context);
            seq = appendLog(execLog.getId(), seq + 1, "INFO",
                "选用执行器：" + executor.getClass().getSimpleName());
            result = executor.execute(context);
        } catch (Exception e) {
            log.error("模型执行异常, modelId => {}, execLogId => {}", context.getModelId(), execLog.getId(), e);
            seq = appendLog(execLog.getId(), seq + 1, "ERROR", "执行异常：" + e.getMessage());
            result = ExecuteResult.failure("执行异常：" + e.getMessage());
        }
        return finalizeExec(execLog, result, startMs, seq);
    }

    /**
     * 创建运行中的执行记录
     */
    private ModelExecLog createRunningLog(ModelExecContext context) {
        ModelExecLog execLog = new ModelExecLog();
        if (context.getExecLogId() != null) {
            execLog.setId(context.getExecLogId());
        }
        execLog.setExecNo(generateExecNo());
        execLog.setModelId(context.getModelId());
        execLog.setModelName(context.getModelName());
        execLog.setTaskId(context.getTaskId());
        execLog.setTriggerType(context.getTriggerType());
        execLog.setStatus(STATUS_RUNNING);
        execLog.setStartTime(new Date());
        execLog.setDeviceCount((long) (context.getDevices() == null ? 0 : context.getDevices().size()));
        execLog.setSuccessCount(0L);
        execLog.setFailCount(0L);
        execLog.setExecParams(JsonUtils.toJsonString(context.getParams()));
        execLog.setScriptId(context.getScriptId());
        execLog.setExecutorId(context.getExecutorId());
        execLogMapper.insert(execLog);
        log.info("创建模型执行记录, execLogId => {}, execNo => {}, modelId => {}",
            execLog.getId(), execLog.getExecNo(), context.getModelId());
        return execLog;
    }

    /**
     * 选取执行器：按 @Order 取第一个 supports 的执行器
     */
    private ModelExecutor selectExecutor(ModelExecContext context) {
        return executors.stream()
            .filter(executor -> executor.supports(context))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("没有可用的模型执行器"));
    }

    /**
     * 落库逐设备结果、追加日志并回写执行记录状态
     */
    private String finalizeExec(ModelExecLog execLog, ExecuteResult result, long startMs, int seq) {
        long successCount = 0;
        long failCount = 0;
        List<ExecuteResult.DeviceResult> deviceResults = result.getDevices() == null
            ? List.of() : result.getDevices();
        for (ExecuteResult.DeviceResult device : deviceResults) {
            ModelResult entity = new ModelResult();
            entity.setExecId(execLog.getId());
            entity.setModelId(execLog.getModelId());
            entity.setDeviceId(device.getDeviceId());
            entity.setDeviceName(device.getDeviceName());
            entity.setLineId(device.getLineId());
            entity.setStationId(device.getStationId());
            entity.setDeviceType(device.getDeviceType());
            entity.setHealthScore(device.getHealthScore());
            entity.setHealthLevel(device.getHealthLevel());
            entity.setDimBody(device.getDimBody());
            entity.setDimCondition(device.getDimCondition());
            entity.setDimFault(device.getDimFault());
            entity.setDimMaintain(device.getDimMaintain());
            entity.setDimEnv(device.getDimEnv());
            if (device.getOutputs() != null && !device.getOutputs().isEmpty()) {
                entity.setOutputs(JsonUtils.toJsonString(device.getOutputs()));
            }
            resultMapper.insert(entity);
            boolean hasOutput = device.getOutputs() != null && !device.getOutputs().isEmpty();
            if (device.getHealthScore() != null || hasOutput) {
                successCount++;
            } else {
                failCount++;
            }
        }

        if (result.getLogLines() != null) {
            for (String line : result.getLogLines()) {
                if (line == null || line.isEmpty()) {
                    continue;
                }
                seq = appendLog(execLog.getId(), seq + 1, "INFO", line);
            }
        }

        String status = resolveStatus(result, successCount, failCount);
        long costMs = System.currentTimeMillis() - startMs;
        seq = appendLog(execLog.getId(), seq + 1,
            STATUS_SUCCESS.equals(status) || STATUS_PARTIAL.equals(status) ? "INFO" : "ERROR",
            "执行结束，状态：" + status + "，设备数：" + deviceResults.size()
                + "，成功：" + successCount + "，失败：" + failCount + "，耗时：" + costMs + "ms");

        execLog.setStatus(status);
        execLog.setEndTime(new Date());
        execLog.setCostMs(costMs);
        execLog.setDeviceCount((long) deviceResults.size());
        execLog.setSuccessCount(successCount);
        execLog.setFailCount(failCount);
        if (!result.isSuccess()) {
            execLog.setErrorMsg(result.getMessage());
        }
        execLogMapper.updateById(execLog);

        log.info("模型执行完成, execLogId => {}, status => {}, costMs => {}", execLog.getId(), status, costMs);
        return execLog.getId();
    }

    /**
     * 依据执行结果判定执行状态
     */
    private String resolveStatus(ExecuteResult result, long successCount, long failCount) {
        if (result.isTimeout()) {
            return STATUS_TIMEOUT;
        }
        if (!result.isSuccess() && successCount == 0) {
            return STATUS_FAILED;
        }
        if (failCount > 0) {
            return STATUS_PARTIAL;
        }
        if (successCount == 0) {
            // 无逐设备明细但脚本成功返回
            return result.isSuccess() ? STATUS_SUCCESS : STATUS_FAILED;
        }
        return STATUS_SUCCESS;
    }

    /**
     * 追加一行执行日志明细，返回最新序号
     */
    private int appendLog(String execId, int seq, String level, String message) {
        ModelExecLogDetail detail = new ModelExecLogDetail();
        detail.setExecId(execId);
        detail.setSeq((long) seq);
        detail.setLogTime(new Date());
        detail.setLevel(level);
        detail.setMessage(message);
        detailMapper.insert(detail);
        return seq;
    }

    /**
     * 生成执行编号，如 EXE20261010153012 4321
     */
    private String generateExecNo() {
        return "EXE" + LocalDateTime.now().format(EXEC_NO_FORMATTER)
            + ThreadLocalRandom.current().nextInt(1000, 10000);
    }

}
