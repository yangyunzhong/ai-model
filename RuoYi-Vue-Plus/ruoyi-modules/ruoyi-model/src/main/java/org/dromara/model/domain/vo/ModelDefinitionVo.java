package org.dromara.model.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.model.domain.ModelDefinition;
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
 * 模型定义视图对象 model_definition
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelDefinition.class)
public class ModelDefinitionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（支持UUID或自定义编码）
     */
    @ExcelProperty(value = "主键", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "支=持UUID或自定义编码")
    private String id;

    /**
     * 模型名称
     */
    @ExcelProperty(value = "模型名称")
    private String modelName;

    /**
     * 所属类别ID，逻辑关联model_catalog.id
     */
    @ExcelProperty(value = "所属类别ID，逻辑关联model_catalog.id")
    private String catalogId;

    /**
     * 适用设备类型（冗余，便于查询）
     */
    @ExcelProperty(value = "适用设备类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余，便于查询")
    private String deviceType;

    /**
     * 统计周期（天）
     */
    @ExcelProperty(value = "统计周期", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "天=")
    private Long statisticsCycleDays;

    /**
     * 状态：草稿/已发布/已归档/已下架
     */
    @ExcelProperty(value = "状态：草稿/已发布/已归档/已下架")
    private String status;

    /**
     * 注册方式：本地注册/第三方注册
     */
    @ExcelProperty(value = "注册方式：本地注册/第三方注册")
    private String registerType;

    /**
     * 注册时间
     */
    @ExcelProperty(value = "注册时间")
    private Date registerTime;

    /**
     * 模型简介/说明
     */
    @ExcelProperty(value = "模型简介/说明")
    private String description;

    /**
     * 算法脚本存储路径
     */
    @ExcelProperty(value = "算法脚本存储路径")
    private String scriptPath;

    /**
     * 容器id
     */
    @ExcelProperty(value = "容器id")
    private String containerId;

    /**
     * 容器名称
     */
    @ExcelProperty(value = "容器名称")
    private String containerName;

    /**
     * 容器状态
     */
    @ExcelProperty(value = "容器状态")
    private String containerStatus;

    /**
     * 运行容器
     */
    @ExcelProperty(value = "运行容器")  
    private String runtimeImage;

    /**
     * 最新发布时间
     */
    @ExcelProperty(value = "最新发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastPublishTime;


}
