package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelComparisonDetail;
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
 * 比对详细结果视图对象 model_comparison_detail
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelComparisonDetail.class)
public class ModelComparisonDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 比对记录ID，逻辑关联model_comparison_record.id
     */
    @ExcelProperty(value = "比对记录ID，逻辑关联model_comparison_record.id")
    private String recordId;

    /**
     * 设备编码
     */
    @ExcelProperty(value = "设备编码")
    private String deviceId;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @ExcelProperty(value = "模型ID，逻辑关联model_definition.id")
    private String modelId;

    /**
     * 健康度得分
     */
    @ExcelProperty(value = "健康度得分")
    private Long healthScore;

    /**
     * 健康等级（healthy/subhealthy/danger）
     */
    @ExcelProperty(value = "健康等级", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "h=ealthy/subhealthy/danger")
    private String healthLevel;

    /**
     * 各维度得分（如本体/工况/故障等）
     */
    @ExcelProperty(value = "各维度得分", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=本体/工况/故障等")
    private String dimensionScores;


}
