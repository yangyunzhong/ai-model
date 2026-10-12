package org.dromara.model.domain.bo;

import org.dromara.model.domain.MetroLine;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 线路信息业务对象 metro_line
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MetroLine.class, reverseConvertGenerate = false)
public class MetroLineBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 线路名称，如“1号线”
     */
    @NotBlank(message = "线路名称，如“1号线”不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 线路业务编码，如“LINE_01”
     */
    @NotBlank(message = "线路业务编码，如“LINE_01”不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 所属客运公司ID
     */
    private String companyId;

    /**
     * 排序权重，数字越小越靠前
     */
    private Long sortOrder;


}
