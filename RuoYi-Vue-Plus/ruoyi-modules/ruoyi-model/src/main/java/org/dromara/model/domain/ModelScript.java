package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 模型算法脚本版本对象 model_script
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_script")
public class ModelScript extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    private String modelId;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 上传时的原始文件名
     */
    private String originalName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * OSS对象存储ID，逻辑关联sys_oss.oss_id
     */
    private String ossId;

    /**
     * 脚本访问地址（未启用OSS时的兜底存储路径）
     */
    private String scriptUrl;

    /**
     * 脚本SHA-256校验值
     */
    private String sha256;

    /**
     * 入口函数名，平台约定为execute
     */
    private String entryFunction;

    /**
     * 脚本版本号
     */
    private String scriptVersion;

    /**
     * requirements.txt的OSS ID
     */
    private String requirementsOssId;

    /**
     * 校验状态：待校验/通过/失败
     */
    private String checkStatus;

    /**
     * 校验结果说明
     */
    private String checkMessage;

    /**
     * 是否当前生效版本（1是 0否）
     */
    private String isCurrent;


}
