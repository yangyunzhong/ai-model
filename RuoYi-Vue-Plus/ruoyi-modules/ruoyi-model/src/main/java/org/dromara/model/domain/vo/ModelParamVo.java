package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelParam;
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
 * 模型输入输出参数视图对象 model_param
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelParam.class)
public class ModelParamVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @ExcelProperty(value = "模型ID，逻辑关联model_definition.id")
    private String modelId;

    /**
     * 参数方向：输入/输出
     */
    @ExcelProperty(value = "参数方向：输入/输出")
    private String paramDirection;

    /**
     * 参数标识（如 window_days）
     */
    @ExcelProperty(value = "参数标识", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=,w=indow_days")
    private String paramKey;

    /**
     * 参数显示名
     */
    @ExcelProperty(value = "参数显示名")
    private String paramName;

    /**
     * 参数类型：数值/文本/布尔
     */
    @ExcelProperty(value = "参数类型：数值/文本/布尔")
    private String paramType;

    /**
     * 是否必填（1是 0否）
     */
    @ExcelProperty(value = "是否必填", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=是,0=否")
    private String required;

    /**
     * 默认值
     */
    @ExcelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 参数说明
     */
    @ExcelProperty(value = "参数说明")
    private String description;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long sortOrder;


}
