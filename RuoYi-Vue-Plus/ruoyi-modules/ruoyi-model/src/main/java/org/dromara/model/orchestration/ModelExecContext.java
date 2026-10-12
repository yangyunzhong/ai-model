package org.dromara.model.orchestration;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模型执行上下文
 *
 * <p>由调用方（单次运行 / 定时任务）组装后交给 {@link ModelExecOrchestrator}，
 * 描述「用哪个容器的哪个脚本、对哪些设备、以什么参数执行」这一次执行所需全部信息。</p>
 *
 * @author model-platform
 */
@Data
@Builder
public class ModelExecContext {

    /**
     * 预生成的执行记录ID（为空时由编排器生成）
     */
    private String execLogId;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 触发方式：定时/单次运行/立即执行/数据比对
     */
    private String triggerType;

    /**
     * 任务ID（定时任务触发时），单次运行为空
     */
    private String taskId;

    /**
     * 执行人ID（手动触发时记录）
     */
    private String executorId;

    /**
     * 当前脚本版本ID（model_script.id）
     */
    private String scriptId;

    /**
     * 脚本版本号
     */
    private String scriptVersion;

    /**
     * 入口函数名，平台约定为 execute
     */
    private String entryFunction;

    /**
     * 容器ID（已发布模型对应的沙箱容器）
     */
    private String containerId;

    /**
     * 容器内脚本绝对路径
     */
    private String containerScriptPath;

    /**
     * 运行入参（对应脚本 execute(params) 的 params）
     */
    private Map<String, Object> params;

    /**
     * 模型输入参数元数据（供脚本按入参字段读取数据库表数据）
     */
    @Builder.Default
    private List<ParamMeta> inputParams = new ArrayList<>();

    /**
     * 模型输出参数元数据（脚本按出参 paramKey 返回结果）
     */
    @Builder.Default
    private List<ParamMeta> outputParams = new ArrayList<>();

    /**
     * 本次执行的设备清单
     */
    @Builder.Default
    private List<DeviceInfo> devices = new ArrayList<>();

    /**
     * 模型参数元数据（输入/输出）
     */
    @Data
    @Builder
    public static class ParamMeta implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 参数标识（模型内唯一，脚本按此 key 读写）
         */
        private String paramKey;

        /**
         * 参数显示名
         */
        private String paramName;

        /**
         * 参数类型：数值/文本/布尔
         */
        private String paramType;

        /**
         * 默认值（表中无对应字段或字段为空时回退）
         */
        private String defaultValue;

    }

    /**
     * 设备信息
     */
    @Data
    @Builder
    public static class DeviceInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

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

    }

}
