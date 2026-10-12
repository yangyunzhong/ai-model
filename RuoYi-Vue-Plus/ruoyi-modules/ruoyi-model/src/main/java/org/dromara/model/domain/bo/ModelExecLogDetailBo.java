package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelExecLogDetail;
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
 * 模型执行日志明细业务对象 model_exec_log_detail
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelExecLogDetail.class, reverseConvertGenerate = false)
public class ModelExecLogDetailBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 执行记录ID，逻辑关联model_exec_log.id
     */
    @NotBlank(message = "执行记录ID，逻辑关联model_exec_log.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String execId;

    /**
     * 日志行序号（同一exec内自增）
     */
    @NotNull(message = "日志行序号（同一exec内自增）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long seq;

    /**
     * 日志时间
     */
    @NotNull(message = "日志时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date logTime;

    /**
     * 日志级别
     */
    @NotBlank(message = "日志级别不能为空", groups = { AddGroup.class, EditGroup.class })
    private String level;

    /**
     * 日志内容
     */
    @NotBlank(message = "日志内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String message;


}
