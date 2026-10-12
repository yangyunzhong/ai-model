package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelComparisonRecord;
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
 * 数据比对记录业务对象 model_comparison_record
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelComparisonRecord.class, reverseConvertGenerate = false)
public class ModelComparisonRecordBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 操作用户ID（逻辑关联用户表）
     */
    private String userId;

    /**
     * 参与比对的设备ID列表（存储设备编码数组）
     */
    @NotBlank(message = "参与比对的设备ID列表（存储设备编码数组）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceIds;

    /**
     * 参与比对的模型ID列表
     */
    @NotBlank(message = "参与比对的模型ID列表不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelIds;

    /**
     * 设备品种（过滤条件）
     */
    private String deviceType;

    /**
     * 比对状态
     */
    @NotBlank(message = "比对状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 结果摘要（如各模型健康分布统计）
     */
    private String resultSummary;

    /**
     * 比对发起时间
     */
    @NotNull(message = "比对发起时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date executedAt;

    /**
     * 完成时间
     */
    private Date completedAt;


}
