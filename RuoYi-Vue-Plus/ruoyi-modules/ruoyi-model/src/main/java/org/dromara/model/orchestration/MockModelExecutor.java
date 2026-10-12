package org.dromara.model.orchestration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟执行器
 *
 * <p>兜底执行器：当模型尚未发布（无容器）或 Docker 运行时未启用时使用，
 * 按 {@code modelId + deviceId} 生成确定性的健康度结果，保证前端链路可联通、
 * 结果可稳定复现（同一输入永远得到同一输出）。</p>
 *
 * @author model-platform
 */
@Slf4j
@Order(100)
@Component
public class MockModelExecutor implements ModelExecutor {

    private static final long DIM_MIN = 60L;
    private static final long DIM_MAX = 100L;

    @Override
    public boolean supports(ModelExecContext context) {
        // 兜底执行器，始终可用
        return true;
    }

    @Override
    public ExecuteResult execute(ModelExecContext context) {
        List<ModelExecContext.DeviceInfo> devices = context.getDevices() == null
            ? List.of() : context.getDevices();
        List<ExecuteResult.DeviceResult> results = new ArrayList<>(devices.size());
        List<String> logs = new ArrayList<>();
        logs.add("【模拟执行】模型 " + context.getModelName() + " 使用内置模拟执行器");
        logs.add("【模拟执行】待评估设备数：" + devices.size());

        for (ModelExecContext.DeviceInfo device : devices) {
            String seed = context.getModelId() + "#" + device.getDeviceId();
            long dimBody = dim(seed, "body");
            long dimCondition = dim(seed, "condition");
            long dimFault = dim(seed, "fault");
            long dimMaintain = dim(seed, "maintain");
            long dimEnv = dim(seed, "env");
            long healthScore = Math.round((dimBody + dimCondition + dimFault + dimMaintain + dimEnv) / 5.0);
            results.add(ExecuteResult.DeviceResult.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .lineId(device.getLineId())
                .stationId(device.getStationId())
                .deviceType(device.getDeviceType())
                .healthScore(healthScore)
                .healthLevel(level(healthScore))
                .dimBody(dimBody)
                .dimCondition(dimCondition)
                .dimFault(dimFault)
                .dimMaintain(dimMaintain)
                .dimEnv(dimEnv)
                .build());
            logs.add("【模拟执行】设备 " + device.getDeviceName() + " => 健康分 " + healthScore);
        }
        logs.add("【模拟执行】完成");

        return ExecuteResult.builder()
            .success(true)
            .timeout(false)
            .message("模拟执行完成")
            .rawOutput(String.join("\n", logs))
            .logLines(logs)
            .devices(results)
            .build();
    }

    /**
     * 按种子生成指定维度的确定性分值
     */
    private long dim(String seed, String key) {
        int hash = Math.abs((seed + "|" + key).hashCode());
        return DIM_MIN + (hash % (DIM_MAX - DIM_MIN + 1));
    }

    /**
     * 健康等级映射
     */
    private String level(long score) {
        if (score >= 80) {
            return "healthy";
        }
        if (score >= 60) {
            return "subhealthy";
        }
        return "danger";
    }

}
