package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 数据比对记录对象 model_comparison_record
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_comparison_record")
public class ModelComparisonRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 操作用户ID（逻辑关联用户表）
     */
    private String userId;

    /**
     * 参与比对的设备ID列表（存储设备编码数组）
     */
    private String deviceIds;

    /**
     * 参与比对的模型ID列表
     */
    private String modelIds;

    /**
     * 设备品种（过滤条件）
     */
    private String deviceType;

    /**
     * 比对状态
     */
    private String status;

    /**
     * 结果摘要（如各模型健康分布统计）
     */
    private String resultSummary;

    /**
     * 比对发起时间
     */
    private Date executedAt;

    /**
     * 完成时间
     */
    private Date completedAt;


}
