package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelTaskParam;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 模型任务运行参数业务对象 model_task_param
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelTaskParam.class, reverseConvertGenerate = false)
public class ModelTaskParamBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 任务ID，逻辑关联model_task.id
     */
    @NotBlank(message = "任务ID，逻辑关联model_task.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String taskId;

    /**
     * 模型ID，逻辑关联model_definition.id（冗余便于查询）
     */
    @NotBlank(message = "模型ID，逻辑关联model_definition.id（冗余便于查询）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 参数标识，对应model_param.param_key
     */
    @NotBlank(message = "参数标识，对应model_param.param_key不能为空", groups = { AddGroup.class, EditGroup.class })
    private String paramKey;

    /**
     * 参数值（留空则使用model_param.default_value）
     */
    private String paramValue;


}
