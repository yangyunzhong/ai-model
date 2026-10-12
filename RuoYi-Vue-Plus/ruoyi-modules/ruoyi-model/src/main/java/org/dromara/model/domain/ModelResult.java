package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 模型执行结果明细对象 model_result
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_result")
public class ModelResult extends BaseEntity {

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
     * 模型ID，逻辑关联model_definition.id
     */
    private String modelId;

    /**
     * 设备ID，逻辑关联model_device.id
     */
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
