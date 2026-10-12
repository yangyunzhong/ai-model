package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelTaskParam;
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
 * 模型任务运行参数视图对象 model_task_param
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelTaskParam.class)
public class ModelTaskParamVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 任务ID，逻辑关联model_task.id
     */
    @ExcelProperty(value = "任务ID，逻辑关联model_task.id")
    private String taskId;

    /**
     * 模型ID，逻辑关联model_definition.id（冗余便于查询）
     */
    @ExcelProperty(value = "模型ID，逻辑关联model_definition.id", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余便于查询")
    private String modelId;

    /**
     * 参数标识，对应model_param.param_key
     */
    @ExcelProperty(value = "参数标识，对应model_param.param_key")
    private String paramKey;

    /**
     * 参数值（留空则使用model_param.default_value）
     */
    @ExcelProperty(value = "参数值", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "留=空则使用model_param.default_value")
    private String paramValue;


}
