package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 线路信息对象 metro_line
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("metro_line")
public class MetroLine extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 线路名称，如“1号线”
     */
    private String name;

    /**
     * 线路业务编码，如“LINE_01”
     */
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
