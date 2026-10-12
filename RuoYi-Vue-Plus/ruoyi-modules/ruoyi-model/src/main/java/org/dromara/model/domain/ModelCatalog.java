package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 模型类别对象 model_catalog
 *
 * @author Liu Cheng
 * @date 2026-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_catalog")
public class ModelCatalog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（支持UUID或自定义编码）
     */
    @TableId(value = "id")
    private String id;

    /**
     * 类别名称（如：自动扶梯、垂直电梯）
     */
    private String catalogName;

    /**
     * 关联设备类型（自动扶梯/垂直电梯等）
     */
    private String deviceType;

    /**
     * 备注说明
     */
    private String description;

    /**
     * 状态（0：停用 1：正常）
     */
    private String status;

}
