package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelCatalog;
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
 * 模型类别视图对象 model_catalog
 *
 * @author Liu Cheng
 * @date 2026-09-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelCatalog.class)
public class ModelCatalogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（支持UUID或自定义编码）
     */
    @ExcelProperty(value = "主键", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "支=持UUID或自定义编码")
    private String id;

    /**
     * 类别名称（如：自动扶梯、垂直电梯）
     */
    @ExcelProperty(value = "类别名称", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=：自动扶梯、垂直电梯")
    private String catalogName;

    /**
     * 关联设备类型（自动扶梯/垂直电梯等）
     */
    @ExcelProperty(value = "关联设备类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "自=动扶梯/垂直电梯等")
    private String deviceType;

    /**
     * 备注说明
     */
    @ExcelProperty(value = "备注说明")
    private String description;

    /**
     * 状态（0：停用 1：正常）
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;
}
