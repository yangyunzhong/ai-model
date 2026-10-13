package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelAnomalyEvent;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 异物检测事件业务对象 model_anomaly_event
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelAnomalyEvent.class, reverseConvertGenerate = false)
public class ModelAnomalyEventBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 设备ID，逻辑关联metro_device.id
     */
    @NotBlank(message = "设备ID，逻辑关联metro_device.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceId;

    /**
     * 设备名称（冗余）
     */
    private String deviceName;

    /**
     * 设备类型：自动扶梯/垂直电梯
     */
    @NotBlank(message = "设备类型：自动扶梯/垂直电梯不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "检测参数名（如电流/电压）不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "监测状态：正常/关注/异常不能为空", groups = { AddGroup.class, EditGroup.class })
    private String monitorStatus;

    /**
     * 事件发生时间
     */
    @NotNull(message = "事件发生时间不能为空", groups = { AddGroup.class, EditGroup.class })
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
