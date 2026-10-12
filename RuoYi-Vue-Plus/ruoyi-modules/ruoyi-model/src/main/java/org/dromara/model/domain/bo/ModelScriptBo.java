package org.dromara.model.domain.bo;

import org.dromara.model.domain.ModelScript;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 模型算法脚本版本业务对象 model_script
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ModelScript.class, reverseConvertGenerate = false)
public class ModelScriptBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @NotBlank(message = "模型ID，逻辑关联model_definition.id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelId;

    /**
     * 存储文件名
     */
    @NotBlank(message = "存储文件名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fileName;

    /**
     * 上传时的原始文件名
     */
    @NotBlank(message = "上传时的原始文件名不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "校验状态：待校验/通过/失败不能为空", groups = { AddGroup.class, EditGroup.class })
    private String checkStatus;

    /**
     * 校验结果说明
     */
    private String checkMessage;

    /**
     * 是否当前生效版本（1是 0否）
     */
    @NotBlank(message = "是否当前生效版本（1是 0否）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String isCurrent;


}
