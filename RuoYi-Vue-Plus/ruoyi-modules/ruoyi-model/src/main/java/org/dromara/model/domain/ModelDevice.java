package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备台账对象 model_device
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_device")
public class ModelDevice extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（设备编号，如X-01）
     */
    @TableId(value = "id")
    private String id;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型，取值与model_catalog.device_type保持一致
     */
    private String deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id
     */
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id
     */
    private String stationId;

    /**
     * 安装位置描述
     */
    private String installPosition;

    /**
     * 状态：启用/停用
     */
    private String status;

    /**
     * 排序号
     */
    private Long sortOrder;

    /**
     * 备注
     */
    private String remark;


}
