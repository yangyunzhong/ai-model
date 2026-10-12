package org.dromara.model.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.model.domain.ModelAnomalyEvent;
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
 * 异物检测事件视图对象 model_anomaly_event
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelAnomalyEvent.class)
public class ModelAnomalyEventVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 设备ID，逻辑关联model_device.id
     */
    @ExcelProperty(value = "设备ID，逻辑关联model_device.id")
    private String deviceId;

    /**
     * 设备名称（冗余）
     */
    @ExcelProperty(value = "设备名称", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余")
    private String deviceName;

    /**
     * 设备类型：自动扶梯/垂直电梯
     */
    @ExcelProperty(value = "设备类型：自动扶梯/垂直电梯")
    private String deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id（冗余）
     */
    @ExcelProperty(value = "线路ID，逻辑关联metro_line.id", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余")
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id（冗余）
     */
    @ExcelProperty(value = "车站ID，逻辑关联metro_station.id", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余")
    private String stationId;

    /**
     * 检测参数名（如电流/电压）
     */
    @ExcelProperty(value = "检测参数名", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=电流/电压")
    private String paramName;

    /**
     * 参数单位（如A/V）
     */
    @ExcelProperty(value = "参数单位", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=A/V")
    private String unit;

    /**
     * 正常值
     */
    @ExcelProperty(value = "正常值")
    private Long normalValue;

    /**
     * 异常值
     */
    @ExcelProperty(value = "异常值")
    private Long abnormalValue;

    /**
     * 偏差（百分比）
     */
    @ExcelProperty(value = "偏差", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "百=分比")
    private Long deviationPercent;

    /**
     * 监测状态：正常/关注/异常
     */
    @ExcelProperty(value = "监测状态：正常/关注/异常")
    private String monitorStatus;

    /**
     * 事件发生时间
     */
    @ExcelProperty(value = "事件发生时间")
    private Date eventTime;

    /**
     * 来源执行记录ID，逻辑关联model_exec_log.id
     */
    @ExcelProperty(value = "来源执行记录ID，逻辑关联model_exec_log.id")
    private String execId;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
