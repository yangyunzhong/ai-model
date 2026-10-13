package org.dromara.model.domain.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据比对执行业务对象
 *
 * <p>用于「数据比对」入口：在指定设备品种下，对同一批设备使用多个模型的
 * 历史执行结果做横向比对。</p>
 *
 * @author model-platform
 */
@Data
public class ModelComparisonExecuteBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备品种（未显式选择设备时，按该品种取全部启用设备）
     */
    private String deviceType;

    /**
     * 参与比对的设备ID列表（为空时按设备品种取全部启用设备）
     */
    private List<String> deviceIds;

    /**
     * 参与比对的模型ID列表（至少 2 个，最多 5 个）
     */
    private List<String> modelIds;
}
