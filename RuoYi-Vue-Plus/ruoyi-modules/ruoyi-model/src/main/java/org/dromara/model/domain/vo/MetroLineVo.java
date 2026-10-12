package org.dromara.model.domain.vo;

import org.dromara.model.domain.MetroLine;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 线路信息视图对象 metro_line
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MetroLine.class)
public class MetroLineVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 线路名称，如“1号线”
     */
    @ExcelProperty(value = "线路名称，如“1号线”")
    private String name;

    /**
     * 线路业务编码，如“LINE_01”
     */
    @ExcelProperty(value = "线路业务编码，如“LINE_01”")
    private String code;

    /**
     * 所属客运公司ID
     */
    @ExcelProperty(value = "所属客运公司ID")
    private String companyId;

    /**
     * 排序权重，数字越小越靠前
     */
    @ExcelProperty(value = "排序权重，数字越小越靠前")
    private Long sortOrder;


}
