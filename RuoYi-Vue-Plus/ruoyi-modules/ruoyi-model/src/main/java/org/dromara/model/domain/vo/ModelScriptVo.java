package org.dromara.model.domain.vo;

import org.dromara.model.domain.ModelScript;
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
 * 模型算法脚本版本视图对象 model_script
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelScript.class)
public class ModelScriptVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @ExcelProperty(value = "模型ID，逻辑关联model_definition.id")
    private String modelId;

    /**
     * 存储文件名
     */
    @ExcelProperty(value = "存储文件名")
    private String fileName;

    /**
     * 上传时的原始文件名
     */
    @ExcelProperty(value = "上传时的原始文件名")
    private String originalName;

    /**
     * 文件后缀
     */
    @ExcelProperty(value = "文件后缀")
    private String fileSuffix;

    /**
     * 文件大小（字节）
     */
    @ExcelProperty(value = "文件大小", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "字=节")
    private Long fileSize;

    /**
     * OSS对象存储ID，逻辑关联sys_oss.oss_id
     */
    @ExcelProperty(value = "OSS对象存储ID，逻辑关联sys_oss.oss_id")
    private String ossId;

    /**
     * 脚本访问地址（未启用OSS时的兜底存储路径）
     */
    @ExcelProperty(value = "脚本访问地址", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "未=启用OSS时的兜底存储路径")
    private String scriptUrl;

    /**
     * 脚本SHA-256校验值
     */
    @ExcelProperty(value = "脚本SHA-256校验值")
    private String sha256;

    /**
     * 入口函数名，平台约定为execute
     */
    @ExcelProperty(value = "入口函数名，平台约定为execute")
    private String entryFunction;

    /**
     * 脚本版本号
     */
    @ExcelProperty(value = "脚本版本号")
    private String scriptVersion;

    /**
     * requirements.txt的OSS ID
     */
    @ExcelProperty(value = "requirements.txt的OSS ID")
    private String requirementsOssId;

    /**
     * 校验状态：待校验/通过/失败
     */
    @ExcelProperty(value = "校验状态：待校验/通过/失败")
    private String checkStatus;

    /**
     * 校验结果说明
     */
    @ExcelProperty(value = "校验结果说明")
    private String checkMessage;

    /**
     * 是否当前生效版本（1是 0否）
     */
    @ExcelProperty(value = "是否当前生效版本", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=是,0=否")
    private String isCurrent;


}
