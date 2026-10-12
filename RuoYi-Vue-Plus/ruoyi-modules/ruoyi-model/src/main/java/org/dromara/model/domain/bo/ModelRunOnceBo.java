package org.dromara.model.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 模型单次运行业务对象
 *
 * <p>用于「单次运行」入口：指定模型、运行参数与参与设备，交由执行编排器完成
 * 一次执行并落库。</p>
 *
 * @author model-platform
 */
@Data
public class ModelRunOnceBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模型ID（必填）
     */
    @NotBlank(message = "模型ID不能为空")
    private String modelId;

    /**
     * 运行入参（对应脚本 execute(params) 的 params）
     */
    private Map<String, Object> params;

    /**
     * 参与设备ID集合（为空时按模型适用设备类型取全部启用设备）
     */
    private List<String> deviceIds;

    /**
     * 触发方式（为空时默认「单次运行」）
     */
    private String triggerType;

}
