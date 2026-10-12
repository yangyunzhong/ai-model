package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 模型定义对象 model_definition
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_definition")
public class ModelDefinition extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（支持UUID或自定义编码）
     */
    @TableId(value = "id")
    private String id;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 所属类别ID，逻辑关联model_catalog.id
     */
    private String catalogId;

    /**
     * 适用设备类型（冗余，便于查询）
     */
    private String deviceType;

    /**
     * 统计周期（天）
     */
    private Long statisticsCycleDays;

    /**
     * 状态：草稿/已发布/已归档/已下架
     */
    private String status;

    /**
     * 注册方式：本地注册/第三方注册
     */
    private String registerType;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 版本号
     */
    @Version
    private String version;

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
