package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelComparisonDetail;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 比对详细结果业务对象 model_comparison_detail
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelComparisonDetail.class, reverseConvertGenerate = false)
public class ModelComparisonDetailBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 比对记录ID，逻辑关联model_comparison_record.id
     */
    @NotBlank(message = "比对记录ID，逻辑关联model_comparison_record.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String recordId;

    /**
     * 设备编码
     */
    @NotBlank(message = "设备编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceId;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @NotBlank(message = "模型ID，逻辑关联model_definition.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 健康度得分
     */
    private Long healthScore;

    /**
     * 健康等级（healthy/subhealthy/danger）
     */
    private String healthLevel;

    /**
     * 各维度得分（如本体/工况/故障等）
     */
    private String dimensionScores;


}
