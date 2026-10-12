package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelDevice;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备台账视图对象 model_device
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelDevice.class)
public class ModelDeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（设备编号，如X-01）
     */
    @ExcelProperty(value = "主键", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "设=备编号，如X-01")
    private String id;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备类型，取值与model_catalog.device_type保持一致
     */
    @ExcelProperty(value = "设备类型，取值与model_catalog.device_type保持一致")
    private String deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id
     */
    @ExcelProperty(value = "线路ID，逻辑关联metro_line.id")
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id
     */
    @ExcelProperty(value = "车站ID，逻辑关联metro_station.id")
    private String stationId;

    /**
     * 安装位置描述
     */
    @ExcelProperty(value = "安装位置描述")
    private String installPosition;

    /**
     * 状态：启用/停用
     */
    @ExcelProperty(value = "状态：启用/停用")
    private String status;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long sortOrder;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
