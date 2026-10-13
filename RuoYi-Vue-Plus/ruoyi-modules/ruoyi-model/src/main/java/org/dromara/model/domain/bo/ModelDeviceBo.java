package org.dromara.model.domain.bo;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备业务对象 metro_device（只读查询参数）
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelDeviceBo extends BaseEntity {

    /**
     * 主键（设备编号）
     */
    private String id;

    /**
     * 设备名称（模糊匹配品牌/型号规格/注册代码）
     */
    private String deviceName;

    /**
     * 设备类型，取值与model_catalog.device_type保持一致（自动扶梯/垂直电梯）
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
     * 设备品种ID，逻辑关联base_config
     */
    private String deviceVarietyCode;

    /**
     * 运行状态
     */
    private Integer runningStatus;

    /**
     * 品牌名称（模糊）
     */
    private String brand;

    /**
     * 设备注册代码（模糊）
     */
    private String registrationCode;

    /**
     * 型号规格（模糊）
     */
    private String modelSpec;

    /**
     * 设备使用地点详细描述（模糊）
     */
    private String locationDetail;

    /**
     * 备注（模糊）
     */
    private String remark;


}
