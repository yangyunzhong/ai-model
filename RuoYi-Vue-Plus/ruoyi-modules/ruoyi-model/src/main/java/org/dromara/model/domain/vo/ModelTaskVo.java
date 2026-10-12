package org.dromara.model.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.model.domain.ModelTask;
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
 * 模型任务视图对象 model_task
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelTask.class)
public class ModelTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 关联模型ID，逻辑关联model_definition.id
     */
    @ExcelProperty(value = "关联模型ID，逻辑关联model_definition.id")
    private String modelId;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名称")
    private String taskName;

    /**
     * cron表达式（如 0 2 * * *）
     */
    @ExcelProperty(value = "cron表达式", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=,0=,2=,*=,*=,*=")
    private String cronExpression;

    /**
     * cron描述（如“每日 02:00”）
     */
    @ExcelProperty(value = "cron描述", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=“每日,0=2:00”")
    private String cronDescription;

    /**
     * 任务状态
     */
    @ExcelProperty(value = "任务状态")
    private String status;

    /**
     * 最近一次执行时间
     */
    @ExcelProperty(value = "最近一次执行时间")
    private Date lastExecuteTime;

    /**
     * 下次执行时间（可计算，可选）
     */
    @ExcelProperty(value = "下次执行时间", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "可=计算，可选")
    private Date nextExecuteTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
