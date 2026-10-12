package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 模型执行日志明细对象 model_exec_log_detail
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_exec_log_detail")
public class ModelExecLogDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 执行记录ID，逻辑关联model_exec_log.id
     */
    private String execId;

    /**
     * 日志行序号（同一exec内自增）
     */
    private Long seq;

    /**
     * 日志时间
     */
    private Date logTime;

    /**
     * 日志级别
     */
    private String level;

    /**
     * 日志内容
     */
    private String message;


}
