package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelDevice;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备视图对象 metro_device
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelDevice.class)
public class ModelDeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（设备编号）
     */
    @ExcelProperty(value = "设备编号")
    private String id;

    /**
     * 设备名称（计算生成：品牌+型号，为空时回退注册代码/设备ID）
     */
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备类型（metro_device.device_type 映射：1-自动扶梯，2-垂直电梯）
     */
    @ExcelProperty(value = "设备类型")
    private String deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id
     */
    @ExcelProperty(value = "线路ID")
    private String lineId;

    /**
     * 线路名称（联表 metro_line.name）
     */
    @ExcelProperty(value = "线路名称")
    private String lineName;

    /**
     * 车站ID，逻辑关联metro_station.id
     */
    @ExcelProperty(value = "车站ID")
    private String stationId;

    /**
     * 车站名称（联表 metro_station.name）
     */
    @ExcelProperty(value = "车站名称")
    private String stationName;

    /**
     * 设备品种ID，逻辑关联base_config
     */
    @ExcelProperty(value = "设备品种ID")
    private String deviceVarietyCode;

    /**
     * 运行状态
     */
    @ExcelProperty(value = "运行状态")
    private Integer runningStatus;

    /**
     * 品牌名称
     */
    @ExcelProperty(value = "品牌名称")
    private String brand;

    /**
     * 设备注册代码（特种设备登记号）
     */
    @ExcelProperty(value = "设备注册代码")
    private String registrationCode;

    /**
     * 型号规格
     */
    @ExcelProperty(value = "型号规格")
    private String modelSpec;

    /**
     * 设备使用地点详细描述
     */
    @ExcelProperty(value = "使用地点描述")
    private String locationDetail;

    /**
     * 设备投入使用日期
     */
    @ExcelProperty(value = "投入使用日期")
    private Date commissioningDate;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
