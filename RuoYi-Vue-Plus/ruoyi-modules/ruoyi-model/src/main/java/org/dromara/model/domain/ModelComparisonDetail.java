package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 比对详细结果对象 model_comparison_detail
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_comparison_detail")
public class ModelComparisonDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 比对记录ID，逻辑关联model_comparison_record.id
     */
    private String recordId;

    /**
     * 设备编码
     */
    private String deviceId;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
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
