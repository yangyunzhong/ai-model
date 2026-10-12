package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelCatalog;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 模型类别业务对象 model_catalog
 *
 * @author Liu Cheng
 * @date 2026-09-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelCatalog.class, reverseConvertGenerate = false)
public class ModelCatalogBo extends BaseEntity {

    /**
     * 主键（支持UUID或自定义编码）
     */
    @NotBlank(message = "主键（支持UUID或自定义编码）不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 类别名称（如：自动扶梯、垂直电梯）
     */
    @NotBlank(message = "类别名称（如：自动扶梯、垂直电梯）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String catalogName;

    /**
     * 关联设备类型（自动扶梯/垂直电梯等）
     */
    @NotBlank(message = "关联设备类型（自动扶梯/垂直电梯等）不能为空", groups = { AddGroup.class, EditGroup.class })
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
