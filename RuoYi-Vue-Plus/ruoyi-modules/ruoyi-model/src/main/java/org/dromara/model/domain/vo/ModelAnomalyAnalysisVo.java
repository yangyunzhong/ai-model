package org.dromara.model.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 异物检测分析聚合视图对象
 *
 * <p>为异物检测分析页提供一次性的数据装配：电流/电压趋势曲线、
 * 实时监测参数面板、异物检测规则配置与综合偏差指标。</p>
 *
 * @author ai
 * @date 2026-10-13
 */
@Data
public class ModelAnomalyAnalysisVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 趋势曲线展示的设备名称（指定设备时为该设备名，否则为范围内第一台设备）
     */
    private String deviceName;

    /**
     * 趋势曲线展示的设备ID
     */
    private String deviceId;

    /**
     * 电流/电压趋势曲线（按时间升序，最近 30 个监测点）
     */
    private List<TrendPoint> trend;

    /**
     * 电流峰值偏差百分比（相对中位水平），用于图上标注
     */
    private BigDecimal anomalyPercent;

    /**
     * 电流峰值在趋势序列中的索引
     */
    private Integer anomalyIndex;

    /**
     * 实时监测参数面板
     */
    private List<MonitorParam> monitorParams;

    /**
     * 异物检测规则配置（来自 metro_foreign_object_rule）
     */
    private List<ForeignRule> rules;

    /**
     * 趋势曲线点
     */
    @Data
    public static class TrendPoint implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 监测时间（HH:mm）
         */
        private String time;

        /**
         * 电流（A）
         */
        private BigDecimal current;
    }

    /**
     * 实时监测参数面板项
     */
    @Data
    public static class MonitorParam implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 参数名（电流/电压/振动/温度）
         */
        private String name;

        /**
         * 当前值（带单位）
         */
        private String value;

        /**
         * 正常范围（文案）
         */
        private String range;

        /**
         * 监测状态：正常/关注/异常
         */
        private String status;
    }

    /**
     * 异物检测规则配置项
     */
    @Data
    public static class ForeignRule implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 设备类型：1-扶梯，2-电梯
         */
        private Integer deviceType;

        /**
         * 电流波动阈值（百分比）
         */
        private BigDecimal currentThreshold;

        /**
         * 振动突增阈值（百分比）
         */
        private BigDecimal vibrationThreshold;

        /**
         * 电流+振动同时异常时的告警级别说明
         */
        private String alertLevel;

        /**
         * 提前预警分钟数
         */
        private Integer advanceMinutes;
    }
}
