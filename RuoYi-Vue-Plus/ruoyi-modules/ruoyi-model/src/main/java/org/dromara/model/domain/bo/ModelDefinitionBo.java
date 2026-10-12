package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelDefinition;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 模型定义业务对象 model_definition
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelDefinition.class, reverseConvertGenerate = false)
public class ModelDefinitionBo extends BaseEntity {

    /**
     * 主键（支持UUID或自定义编码）
     */
    @NotBlank(message = "主键（支持UUID或自定义编码）不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelName;

    /**
     * 所属类别ID，逻辑关联model_catalog.id
     */
    @NotBlank(message = "所属类别ID，逻辑关联model_catalog.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String catalogId;

    /**
     * 适用设备类型（冗余，便于查询）
     */
    @NotBlank(message = "适用设备类型（冗余，便于查询）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceType;

    /**
     * 统计周期（天）
     */
    private Long statisticsCycleDays;

    /**
     * 状态：草稿/已发布/已归档/已下架
     */
    @NotBlank(message = "状态：草稿/已发布/已归档/已下架不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 注册方式：本地注册/第三方注册
     */
    @NotBlank(message = "注册方式：本地注册/第三方注册不能为空", groups = { AddGroup.class, EditGroup.class })
    private String registerType;

    /**
     * 注册时间
     */
    @NotNull(message = "注册时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date registerTime;

    /**
     * 模型简介/说明
     */
    private String description;

    /**
     * 算法脚本存储路径
     */
    private String scriptPath;

    /**
     * 容器id
     */
    private String containerId;

    /**
     * 容器名称
     */
    private String containerName;

    /**
     * 容器状态
     */
    private String containerStatus;

    /**
     * 运行容器
     */
    private String runtimeImage;

    /**
     * 最新发布时间
     */
    private Date lastPublishTime;


}
