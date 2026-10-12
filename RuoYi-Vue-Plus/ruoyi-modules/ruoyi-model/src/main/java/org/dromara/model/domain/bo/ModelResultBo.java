package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelResult;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 模型执行结果明细业务对象 model_result
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelResult.class, reverseConvertGenerate = false)
public class ModelResultBo extends BaseEntity {

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
     * 模型ID，逻辑关联model_definition.id
     */
    @NotBlank(message = "模型ID，逻辑关联model_definition.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 设备ID，逻辑关联model_device.id
     */
    @NotBlank(message = "设备ID，逻辑关联model_device.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceId;

    /**
     * 设备名称（冗余）
     */
    private String deviceName;

    /**
     * 线路ID，逻辑关联metro_line.id（冗余）
     */
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id（冗余）
     */
    private String stationId;

    /**
     * 设备类型（冗余）
     */
    private String deviceType;

    /**
     * 健康总分（0-100）
     */
    private Long healthScore;

    /**
     * 健康等级 (healthy/subhealthy/danger)
     */
    private String healthLevel;

    /**
     * 维度分-本体
     */
    private Long dimBody;

    /**
     * 维度分-状态
     */
    private Long dimCondition;

    /**
     * 维度分-故障
     */
    private Long dimFault;

    /**
     * 维度分-维护
     */
    private Long dimMaintain;

    /**
     * 维度分-环境
     */
    private Long dimEnv;

    /**
     * 输出参数结果（JSON 字符串，key = 出参 paramKey）
     */
    private String outputs;


}
