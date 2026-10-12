package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelParam;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 模型输入输出参数业务对象 model_param
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelParam.class, reverseConvertGenerate = false)
public class ModelParamBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @NotBlank(message = "模型ID，逻辑关联model_definition.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 参数方向：输入/输出
     */
    @NotBlank(message = "参数方向：输入/输出不能为空", groups = { AddGroup.class, EditGroup.class })
    private String paramDirection;

    /**
     * 参数标识（如 window_days）
     */
    @NotBlank(message = "参数标识（如 window_days）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String paramKey;

    /**
     * 参数显示名
     */
    @NotBlank(message = "参数显示名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String paramName;

    /**
     * 参数类型：数值/文本/布尔
     */
    @NotBlank(message = "参数类型：数值/文本/布尔不能为空", groups = { AddGroup.class, EditGroup.class })
    private String paramType;

    /**
     * 是否必填（1是 0否）
     */
    @NotBlank(message = "是否必填（1是 0否）不能为空", groups = { AddGroup.class, EditGroup.class })
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
