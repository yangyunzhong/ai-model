package org.dromara.model.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.core.util.JsonUtil;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.model.domain.bo.ModelRunOnceBo;
import org.dromara.model.service.IModelExecLogService;
import org.dromara.model.service.IModelTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 模型任务定时执行器
 *
 * <p>由 SnailJob 按模型任务配置的 cron 表达式触发，对模型适用设备类型下的全部启用设备
 * 执行一次计算并落库，随后回写任务最近执行时间。</p>
 *
 * <p>作业名称 {@code modelTaskExecutor} 需与 {@code ModelTaskServiceImpl} 中创建作业时使用的
 * 执行器名称保持一致。</p>
 *
 * @author model-platform
 */
@Slf4j
@Component
@RequiredArgsConstructor
@JobExecutor(name = "modelTaskExecutor")
public class ModelTaskJobExecutor {

    /**
     * 定时触发方式
     */
    private static final String TRIGGER_TYPE_SCHEDULED = "定时";

    private final IModelExecLogService execLogService;

    private final IModelTaskService taskService;

    /**
     * SnailJob 定时执行入口
     *
     * @param jobArgs 作业参数，包含 modelId 与 taskId
     * @return 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        Map<String, Object> params = resolveParams(jobArgs);
        String modelId = asString(params.get("modelId"));
        String taskId = asString(params.get("taskId"));
        SnailJobLog.LOCAL.info("模型任务定时执行开始, modelId => {}, taskId => {}", modelId, taskId);
        SnailJobLog.REMOTE.info("模型任务定时执行开始, modelId => {}, taskId => {}", modelId, taskId);

        if (StringUtils.isBlank(modelId)) {
            return ExecuteResult.failure("模型任务缺少 modelId 参数");
        }

        try {
            // 不传 deviceIds：由 resolveDevices 按模型适用设备类型取全部启用设备
            ModelRunOnceBo bo = new ModelRunOnceBo();
            bo.setModelId(modelId);
            bo.setTaskId(taskId);
            bo.setTriggerType(TRIGGER_TYPE_SCHEDULED);
            String execLogId = execLogService.runOnce(bo);

            if (StringUtils.isNotBlank(taskId)) {
                // 回写最近执行时间；一次性任务（POINT_IN_TIME 触发）执行完成自动暂停
                taskService.finishOnceTask(taskId);
            }

            String msg = "模型任务定时执行完成, execLogId => " + execLogId;
            SnailJobLog.LOCAL.info(msg);
            SnailJobLog.REMOTE.info(msg);
            return ExecuteResult.success(msg);
        } catch (Exception e) {
            log.error("模型任务定时执行失败, modelId => {}, taskId => {}", modelId, taskId, e);
            SnailJobLog.REMOTE.error("模型任务定时执行失败: {}", e.getMessage());
            return ExecuteResult.failure("模型任务执行失败：" + e.getMessage());
        }
    }

    /**
     * 解析作业参数，兼容 Map 与 JSON 字符串两种形态
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveParams(JobArgs jobArgs) {
        Object raw = jobArgs.getJobParams();
        if (raw instanceof Map) {
            return (Map<String, Object>) raw;
        }
        if (raw instanceof String str && StringUtils.isNotBlank(str)) {
            try {
                return JsonUtil.parseHashMap(str);
            } catch (Exception e) {
                log.warn("解析模型任务作业参数失败: {}", str);
            }
        }
        return Map.of();
    }

    /**
     * 参数值转字符串
     */
    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
