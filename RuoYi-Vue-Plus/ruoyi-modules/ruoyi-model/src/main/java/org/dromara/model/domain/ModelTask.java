package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 模型任务对象 model_task
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_task")
public class ModelTask extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 关联模型ID，逻辑关联model_definition.id
     */
    private String modelId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * cron表达式（如 0 2 * * *）
     */
    private String cronExpression;

    /**
     * cron描述（如“每日 02:00”）
     */
    private String cronDescription;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 调度类型：cron=周期调度，once=一次性指定时间
     */
    private String scheduleType;

    /**
     * SnailJob 作业ID
     */
    private Long jobId;
    
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
