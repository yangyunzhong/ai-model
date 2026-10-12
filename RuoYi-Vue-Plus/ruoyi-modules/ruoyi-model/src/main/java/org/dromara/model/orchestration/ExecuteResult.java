package org.dromara.model.orchestration;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型执行结果
 *
 * <p>由 {@link ModelExecutor} 返回，供 {@link ModelExecOrchestrator} 落库到
 * {@code model_result} 与 {@code model_exec_log_detail}。</p>
 *
 * @author model-platform
 */
@Data
@Builder
public class ExecuteResult {

    /**
     * 是否执行成功（脚本整体是否成功返回）
     */
    private boolean success;

    /**
     * 是否超时
     */
    private boolean timeout;

    /**
     * 概要信息（失败原因或成功提示）
     */
    private String message;

    /**
     * 脚本原始输出（用于排障留档）
     */
    private String rawOutput;

    /**
     * 过程日志行（逐行写入 model_exec_log_detail）
     */
    @Builder.Default
    private List<String> logLines = new ArrayList<>();

    /**
     * 逐设备结果（写入 model_result）
     */
    @Builder.Default
    private List<DeviceResult> devices = new ArrayList<>();

    /**
     * 构造失败结果
     */
    public static ExecuteResult failure(String message) {
        return ExecuteResult.builder()
            .success(false)
            .message(message)
            .logLines(new ArrayList<>())
            .devices(new ArrayList<>())
            .build();
    }

    /**
     * 逐设备执行结果
     */
    @Data
    @Builder
    public static class DeviceResult {

        /**
         * 设备ID
         */
        private String deviceId;

        /**
         * 设备名称
         */
        private String deviceName;

        /**
         * 线路ID
         */
        private String lineId;

        /**
         * 车站ID
         */
        private String stationId;

        /**
         * 设备类型
         */
        private String deviceType;

        /**
         * 健康总分（0-100）
         */
        private Long healthScore;

        /**
         * 健康等级 (healthy/subhealthy/danger)
         */
        private String healthLevel;

        /**
         * 维度分-本体
         */
        private Long dimBody;

        /**
         * 维度分-状态
         */
        private Long dimCondition;

        /**
         * 维度分-故障
         */
        private Long dimFault;

        /**
         * 维度分-维护
         */
        private Long dimMaintain;

        /**
         * 维度分-环境
         */
        private Long dimEnv;

        /**
         * 输出参数结果（key = 出参 paramKey，value = 脚本返回值）
         */
        @Builder.Default
        private Map<String, Object> outputs = new LinkedHashMap<>();

    }

}
