package org.dromara.model.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.model.runtime.ModelContainerManager;
import org.dromara.model.runtime.RuntimeProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Docker 沙箱执行器
 *
 * <p>当模型已发布（存在可用容器）且运行时开关打开时生效。执行方式为在常驻容器内
 * 调用 {@code python <runnerPath> <scriptPath> <entryFunction> <base64(params)>}，
 * runner.py 会把结果以单行 {@code __RESULT__{json}} 输出到 stdout，其余 stdout 视作日志。</p>
 *
 * <p>结果 JSON 约定：</p>
 * <pre>
 * {
 *   "success": true,
 *   "message": "ok",
 *   "devices": [
 *     {"deviceId":"X-01","healthScore":88,"healthLevel":"healthy",
 *      "dimBody":90,"dimCondition":86,"dimFault":88,"dimMaintain":85,"dimEnv":91}
 *   ]
 * }
 * </pre>
 *
 * @author model-platform
 */
@Slf4j
@Order(10)
@Component
@RequiredArgsConstructor
public class DockerModelExecutor implements ModelExecutor {

    private static final String RESULT_PREFIX = "__RESULT__";
    private static final int MAX_MESSAGE_LENGTH = 2000;

    private final RuntimeProperties properties;
    private final ModelContainerManager containerManager;

    @Override
    public boolean supports(ModelExecContext context) {
        return Boolean.TRUE.equals(properties.getEnabled())
            && StringUtils.isNotBlank(context.getContainerId())
            && StringUtils.isNotBlank(context.getContainerScriptPath())
            && containerManager.exists(context.getContainerId());
    }

    @Override
    public ExecuteResult execute(ModelExecContext context) {
        String entryFunction = StringUtils.isBlank(context.getEntryFunction())
            ? "execute" : context.getEntryFunction();
        String base64Params = Base64.getEncoder().encodeToString(
            JsonUtils.toJsonString(context.getParams() == null ? Map.of() : context.getParams())
                .getBytes(StandardCharsets.UTF_8));
        String base64Meta = Base64.getEncoder().encodeToString(
            JsonUtils.toJsonString(buildParamMeta(context)).getBytes(StandardCharsets.UTF_8));
        String[] cmd = {
            "python", properties.getRunnerPath(),
            context.getContainerScriptPath(), entryFunction, base64Params, base64Meta
        };

        log.info("容器内执行脚本, containerId => {}, cmd => {}", context.getContainerId(), String.join(" ", cmd));
        ModelContainerManager.ExecResult execResult = containerManager.exec(
            context.getContainerId(), cmd, properties.getExecTimeoutSeconds());

        List<String> logs = new ArrayList<>();
        logs.add("【沙箱执行】执行命令：" + String.join(" ", cmd));
        List<String> stdoutLines = splitLines(execResult.stdout());
        for (String line : stdoutLines) {
            if (!line.startsWith(RESULT_PREFIX)) {
                logs.add(line);
            }
        }

        if (execResult.timeout()) {
            logs.add("【沙箱执行】脚本执行超时");
            return ExecuteResult.builder()
                .success(false)
                .timeout(true)
                .message("脚本执行超时（超过 " + properties.getExecTimeoutSeconds() + " 秒）")
                .rawOutput(execResult.stdout())
                .logLines(logs)
                .devices(new ArrayList<>())
                .build();
        }

        String payload = extractResultLine(stdoutLines);
        if (payload == null) {
            String reason = "脚本未输出约定结果（缺少 " + RESULT_PREFIX + " 行），退出码=" + execResult.exitCode();
            if (StringUtils.isNotBlank(execResult.stderr())) {
                reason = reason + "；stderr=" + truncate(execResult.stderr());
            }
            logs.add("【沙箱执行】" + reason);
            return ExecuteResult.builder()
                .success(false)
                .timeout(false)
                .message(reason)
                .rawOutput(execResult.stdout())
                .logLines(logs)
                .devices(new ArrayList<>())
                .build();
        }

        return parsePayload(payload, context, logs);
    }

    /**
     * 解析 runner.py 输出的结果 JSON
     */
    private ExecuteResult parsePayload(String payload, ModelExecContext context, List<String> logs) {
        Map<String, Object> dict;
        try {
            dict = JsonUtils.parseMap(payload);
        } catch (Exception e) {
            logs.add("【沙箱执行】结果 JSON 解析失败：" + e.getMessage());
            return ExecuteResult.builder()
                .success(false)
                .message("脚本结果 JSON 解析失败：" + truncate(e.getMessage()))
                .rawOutput(payload)
                .logLines(logs)
                .devices(new ArrayList<>())
                .build();
        }
        if (dict == null) {
            logs.add("【沙箱执行】结果为空");
            return ExecuteResult.builder()
                .success(false)
                .message("脚本结果为空")
                .rawOutput(payload)
                .logLines(logs)
                .devices(new ArrayList<>())
                .build();
        }

        boolean success = asBoolean(dict.get("success"));
        String message = asString(dict.get("message"));
        if (StringUtils.isBlank(message)) {
            message = success ? "执行成功" : "执行失败";
        }
        List<ExecuteResult.DeviceResult> deviceResults = new ArrayList<>();

        Object devicesObj = dict.get("devices");
        if (devicesObj instanceof List<?> deviceList) {
            Map<String, ModelExecContext.DeviceInfo> deviceMap = indexDevices(context);
            for (Object item : deviceList) {
                if (!(item instanceof Map<?, ?> raw)) {
                    continue;
                }
                Map<String, Object> deviceData = new HashMap<>();
                raw.forEach((k, v) -> deviceData.put(String.valueOf(k), v));
                String deviceId = asString(deviceData.get("deviceId"));
                ModelExecContext.DeviceInfo info = deviceId == null ? null : deviceMap.get(deviceId);
                Long healthScore = asLong(deviceData.get("healthScore"));
                deviceResults.add(ExecuteResult.DeviceResult.builder()
                    .deviceId(deviceId)
                    .deviceName(info != null ? info.getDeviceName() : asString(deviceData.get("deviceName")))
                    .lineId(info != null ? info.getLineId() : asString(deviceData.get("lineId")))
                    .stationId(info != null ? info.getStationId() : asString(deviceData.get("stationId")))
                    .deviceType(info != null ? info.getDeviceType() : asString(deviceData.get("deviceType")))
                    .healthScore(healthScore)
                    .healthLevel(asString(deviceData.get("healthLevel")))
                    .dimBody(asLong(deviceData.get("dimBody")))
                    .dimCondition(asLong(deviceData.get("dimCondition")))
                    .dimFault(asLong(deviceData.get("dimFault")))
                    .dimMaintain(asLong(deviceData.get("dimMaintain")))
                    .dimEnv(asLong(deviceData.get("dimEnv")))
                    .outputs(extractOutputs(deviceData, context))
                    .build());
            }
        }
        if (deviceResults.isEmpty()) {
            logs.add("【沙箱执行】结果未包含 devices 明细");
        }
        logs.add("【沙箱执行】" + message);

        return ExecuteResult.builder()
            .success(success)
            .timeout(false)
            .message(message)
            .rawOutput(payload)
            .logLines(logs)
            .devices(deviceResults)
            .build();
    }

    private Map<String, ModelExecContext.DeviceInfo> indexDevices(ModelExecContext context) {
        Map<String, ModelExecContext.DeviceInfo> map = new HashMap<>();
        if (context.getDevices() == null) {
            return map;
        }
        for (ModelExecContext.DeviceInfo device : context.getDevices()) {
            if (device.getDeviceId() != null) {
                map.put(device.getDeviceId(), device);
            }
        }
        return map;
    }

    /**
     * 组装传给脚本的参数元数据（输入/输出参数），供 runner.py 写入环境变量、
     * 由 platform_db 按输入参数 paramKey 读取表字段并按输出参数 paramKey 返回结果。
     */
    private Map<String, Object> buildParamMeta(ModelExecContext context) {
        Map<String, Object> meta = new HashMap<>(4);
        meta.put("inputParams", toMetaList(context.getInputParams()));
        meta.put("outputParams", toMetaList(context.getOutputParams()));
        return meta;
    }

    private List<Map<String, Object>> toMetaList(List<ModelExecContext.ParamMeta> params) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (params == null) {
            return list;
        }
        for (ModelExecContext.ParamMeta param : params) {
            Map<String, Object> item = new LinkedHashMap<>(4);
            item.put("paramKey", param.getParamKey());
            item.put("paramName", param.getParamName());
            item.put("paramType", param.getParamType());
            item.put("defaultValue", param.getDefaultValue());
            list.add(item);
        }
        return list;
    }

    /**
     * 按模型输出参数 paramKey 从设备结果中提取输出数据
     */
    private Map<String, Object> extractOutputs(Map<String, Object> deviceData, ModelExecContext context) {
        Map<String, Object> outputs = new LinkedHashMap<>();
        List<ModelExecContext.ParamMeta> outputParams = context.getOutputParams();
        if (outputParams == null || outputParams.isEmpty()) {
            return outputs;
        }
        for (ModelExecContext.ParamMeta param : outputParams) {
            String key = param.getParamKey();
            if (StringUtils.isNotBlank(key) && deviceData.containsKey(key)) {
                outputs.put(key, deviceData.get(key));
            }
        }
        return outputs;
    }

    /**
     * 提取 __RESULT__ 行中的 JSON 内容（取最后一条，避免日志中误出现）
     */
    private String extractResultLine(List<String> lines) {
        String payload = null;
        for (String line : lines) {
            if (line.startsWith(RESULT_PREFIX)) {
                payload = line.substring(RESULT_PREFIX.length()).trim();
            }
        }
        return payload;
    }

    /**
     * 按行切分（兼容 \r\n）
     */
    private List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        for (String line : text.split("\\r?\\n")) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value != null && Boolean.parseBoolean(String.valueOf(value));
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Math.round(Double.parseDouble(String.valueOf(value)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String truncate(String text) {
        if (text == null) {
            return null;
        }
        return text.length() <= MAX_MESSAGE_LENGTH ? text : text.substring(0, MAX_MESSAGE_LENGTH);
    }

}
