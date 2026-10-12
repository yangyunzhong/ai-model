package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelDevice;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备台账业务对象 model_device
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelDevice.class, reverseConvertGenerate = false)
public class ModelDeviceBo extends BaseEntity {

    /**
     * 主键（设备编号，如X-01）
     */
    @NotBlank(message = "主键（设备编号，如X-01）不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceName;

    /**
     * 设备类型，取值与model_catalog.device_type保持一致
     */
    @NotBlank(message = "设备类型，取值与model_catalog.device_type保持一致不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id
     */
    @NotBlank(message = "线路ID，逻辑关联metro_line.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id
     */
    @NotBlank(message = "车站ID，逻辑关联metro_station.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String stationId;

    /**
     * 安装位置描述
     */
    private String installPosition;

    /**
     * 状态：启用/停用
     */
    @NotBlank(message = "状态：启用/停用不能为空", groups = { AddGroup.class, EditGroup.class })
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
