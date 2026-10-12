package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 模型输入输出参数对象 model_param
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_param")
public class ModelParam extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    private String modelId;

    /**
     * 参数方向：输入/输出
     */
    private String paramDirection;

    /**
     * 参数标识（如 window_days）
     */
    private String paramKey;

    /**
     * 参数显示名
     */
    private String paramName;

    /**
     * 参数类型：数值/文本/布尔
     */
    private String paramType;

    /**
     * 是否必填（1是 0否）
     */
    private String required;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 参数说明
     */
    private String description;

    /**
     * 排序号
     */
    private Long sortOrder;


}
