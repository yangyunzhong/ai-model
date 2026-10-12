package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 模型任务运行参数对象 model_task_param
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_task_param")
public class ModelTaskParam extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 任务ID，逻辑关联model_task.id
     */
    private String taskId;

    /**
     * 模型ID，逻辑关联model_definition.id（冗余便于查询）
     */
    private String modelId;

    /**
     * 参数标识，对应model_param.param_key
     */
    private String paramKey;

    /**
     * 参数值（留空则使用model_param.default_value）
     */
    private String paramValue;


}
