package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelTask;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 模型任务业务对象 model_task
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelTask.class, reverseConvertGenerate = false)
public class ModelTaskBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 关联模型ID，逻辑关联model_definition.id
     */
    @NotBlank(message = "关联模型ID，逻辑关联model_definition.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String taskName;

    /**
     * cron表达式（如 0 2 * * *），周期调度必填，一次性调度留空
     */
    private String cronExpression;

    /**
     * cron描述（如“每日 02:00”）
     */
    private String cronDescription;

    /**
     * 调度类型：cron=周期调度，once=一次性指定时间
     */
    private String scheduleType;

    /**
     * 任务状态（仅新增时必填；配置编辑不改状态，为空时不会覆盖原值）
     */
    @NotBlank(message = "任务状态不能为空", groups = { AddGroup.class })
    private String status;

    /**
     * 最近一次执行时间
     */
    private Date lastExecuteTime;

    /**
     * 下次执行时间（可计算，可选）
     */
    private Date nextExecuteTime;

    /**
     * 备注
     */
    private String remark;


}
