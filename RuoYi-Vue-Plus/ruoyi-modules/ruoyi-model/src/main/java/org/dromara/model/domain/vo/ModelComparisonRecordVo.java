package org.dromara.model.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.model.domain.ModelComparisonRecord;
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
 * 数据比对记录视图对象 model_comparison_record
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelComparisonRecord.class)
public class ModelComparisonRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 操作用户ID（逻辑关联用户表）
     */
    @ExcelProperty(value = "操作用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "逻=辑关联用户表")
    private String userId;

    /**
     * 参与比对的设备ID列表（存储设备编码数组）
     */
    @ExcelProperty(value = "参与比对的设备ID列表", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "存=储设备编码数组")
    private String deviceIds;

    /**
     * 参与比对的模型ID列表
     */
    @ExcelProperty(value = "参与比对的模型ID列表")
    private String modelIds;

    /**
     * 设备品种（过滤条件）
     */
    @ExcelProperty(value = "设备品种", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "过=滤条件")
    private String deviceType;

    /**
     * 比对状态
     */
    @ExcelProperty(value = "比对状态")
    private String status;

    /**
     * 结果摘要（如各模型健康分布统计）
     */
    @ExcelProperty(value = "结果摘要", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=各模型健康分布统计")
    private String resultSummary;

    /**
     * 比对发起时间
     */
    @ExcelProperty(value = "比对发起时间")
    private Date executedAt;

    /**
     * 完成时间
     */
    @ExcelProperty(value = "完成时间")
    private Date completedAt;


}
