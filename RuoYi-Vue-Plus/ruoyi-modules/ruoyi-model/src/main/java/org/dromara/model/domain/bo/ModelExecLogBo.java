package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelExecLog;
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
 * 模型执行记录业务对象 model_exec_log
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelExecLog.class, reverseConvertGenerate = false)
public class ModelExecLogBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 执行编号（如EXE20260921020000001）
     */
    @NotBlank(message = "执行编号（如EXE20260921020000001）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String execNo;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @NotBlank(message = "模型ID，逻辑关联model_definition.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 模型名称（冗余，便于查询/列表展示）
     */
    private String modelName;

    /**
     * 任务ID，逻辑关联model_task.id；单次运行/比对时为空
     */
    private String taskId;

    /**
     * 触发方式：定时/单次运行/立即执行/数据比对
     */
    @NotBlank(message = "触发方式：定时/单次运行/立即执行/数据比对不能为空", groups = { AddGroup.class, EditGroup.class })
    private String triggerType;

    /**
     * 执行状态：运行中/成功/部分成功/失败/超时
     */
    @NotBlank(message = "执行状态：运行中/成功/部分成功/失败/超时不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 耗时（毫秒）
     */
    private Long costMs;

    /**
     * 涉及设备数
     */
    private Long deviceCount;

    /**
     * 成功设备数
     */
    private Long successCount;

    /**
     * 失败设备数
     */
    private Long failCount;

    /**
     * 本次执行入参快照
     */
    private String execParams;

    /**
     * 本次使用的脚本版本ID，逻辑关联model_script.id
     */
    private String scriptId;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行人ID（手动触发时记录）
     */
    private String executorId;


}
