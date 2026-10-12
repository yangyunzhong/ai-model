package org.dromara.model.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.model.domain.ModelExecLogDetail;
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
 * 模型执行日志明细视图对象 model_exec_log_detail
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelExecLogDetail.class)
public class ModelExecLogDetailVo implements Serializable {

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
     * 日志行序号（同一exec内自增）
     */
    @ExcelProperty(value = "日志行序号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "同=一exec内自增")
    private Long seq;

    /**
     * 日志时间
     */
    @ExcelProperty(value = "日志时间")
    private Date logTime;

    /**
     * 日志级别
     */
    @ExcelProperty(value = "日志级别")
    private String level;

    /**
     * 日志内容
     */
    @ExcelProperty(value = "日志内容")
    private String message;


}
