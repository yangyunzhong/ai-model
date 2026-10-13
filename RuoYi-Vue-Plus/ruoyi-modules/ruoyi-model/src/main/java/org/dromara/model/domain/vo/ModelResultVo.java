package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelResult;
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
 * 模型执行结果明细视图对象 model_result
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelResult.class)
public class ModelResultVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 执行记录ID，逻辑关联model_exec_log.id
     */
    @ExcelProperty(value = "执行记录ID，逻辑关联model_exec_log.id")
    private String execId;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @ExcelProperty(value = "模型ID，逻辑关联model_definition.id")
    private String modelId;

    /**
     * 设备ID，逻辑关联metro_device.id
     */
    @ExcelProperty(value = "设备ID，逻辑关联metro_device.id")
    private String deviceId;

    /**
     * 设备名称（冗余）
     */
    @ExcelProperty(value = "设备名称", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余")
    private String deviceName;

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
     * 线路名称（联表 metro_line.name）
     */
    @ExcelProperty(value = "线路名称")
    private String lineName;

    /**
     * 车站名称（联表 metro_station.name）
     */
    @ExcelProperty(value = "车站名称")
    private String stationName;

    /**
     * 设备类型（冗余）
     */
    @ExcelProperty(value = "设备类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余")
    private String deviceType;

    /**
     * 健康总分（0-100）
     */
    @ExcelProperty(value = "健康总分", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-100")
    private Long healthScore;

    /**
     * 健康等级 (healthy/subhealthy/danger)
     */
    @ExcelProperty(value = "健康等级 (healthy/subhealthy/danger)")
    private String healthLevel;

    /**
     * 维度分-本体
     */
    @ExcelProperty(value = "维度分-本体")
    private Long dimBody;

    /**
     * 维度分-状态
     */
    @ExcelProperty(value = "维度分-状态")
    private Long dimCondition;

    /**
     * 维度分-故障
     */
    @ExcelProperty(value = "维度分-故障")
    private Long dimFault;

    /**
     * 维度分-维护
     */
    @ExcelProperty(value = "维度分-维护")
    private Long dimMaintain;

    /**
     * 维度分-环境
     */
    @ExcelProperty(value = "维度分-环境")
    private Long dimEnv;

    /**
     * 输出参数结果（JSON 字符串，key = 出参 paramKey）
     */
    @ExcelProperty(value = "输出参数结果")
    private String outputs;


}
