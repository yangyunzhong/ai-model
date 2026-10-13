package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 异物检测事件对象 model_anomaly_event
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_anomaly_event")
public class ModelAnomalyEvent extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 设备ID，逻辑关联metro_device.id
     */
    private String deviceId;

    /**
     * 设备名称（冗余）
     */
    private String deviceName;

    /**
     * 设备类型：自动扶梯/垂直电梯
     */
    private String deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id（冗余）
     */
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id（冗余）
     */
    private String stationId;

    /**
     * 检测参数名（如电流/电压）
     */
    private String paramName;

    /**
     * 参数单位（如A/V）
     */
    private String unit;

    /**
     * 正常值
     */
    private Long normalValue;

    /**
     * 异常值
     */
    private Long abnormalValue;

    /**
     * 偏差（百分比）
     */
    private Long deviationPercent;

    /**
     * 监测状态：正常/关注/异常
     */
    private String monitorStatus;

    /**
     * 事件发生时间
     */
    private Date eventTime;

    /**
     * 来源执行记录ID，逻辑关联model_exec_log.id
     */
    private String execId;

    /**
     * 备注
     */
    private String remark;


}
