package org.dromara.model.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据比对执行结果视图对象
 *
 * <p>返回参与比对的模型、设备清单，逐设备逐模型的健康评分明细，
 * 以及一致性统计摘要，供前端直接渲染比对矩阵。</p>
 *
 * @author model-platform
 */
@Data
public class ModelComparisonExecuteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 本次比对记录ID
     */
    private String recordId;

    /**
     * 设备品种
     */
    private String deviceType;

    /**
     * 参与比对的模型
     */
    private List<ModelBrief> models;

    /**
     * 参与比对的设备
     */
    private List<DeviceBrief> devices;

    /**
     * 逐设备逐模型的评分明细
     */
    private List<ComparisonCell> details;

    /**
     * 一致性统计摘要
     */
    private Summary summary;

    /**
     * 模型简要信息
     */
    @Data
    public static class ModelBrief implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String modelId;

        private String modelName;
    }

    /**
     * 设备简要信息
     */
    @Data
    public static class DeviceBrief implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String deviceId;

        private String deviceName;

        private String lineId;

        private String stationId;
    }

    /**
     * 单个设备在单个模型下的评分明细
     */
    @Data
    public static class ComparisonCell implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String deviceId;

        private String deviceName;

        private String modelId;

        private Long healthScore;

        private String healthLevel;

        private Long dimBody;

        private Long dimCondition;

        private Long dimFault;

        private Long dimMaintain;

        private Long dimEnv;
    }

    /**
     * 比对一致性摘要
     */
    @Data
    public static class Summary implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 参与比对的设备总数
         */
        private int total;

        /**
         * 各模型健康等级一致的设备数
         */
        private int consistent;

        /**
         * 各模型健康等级存在分歧的设备数
         */
        private int divergent;
    }
}
