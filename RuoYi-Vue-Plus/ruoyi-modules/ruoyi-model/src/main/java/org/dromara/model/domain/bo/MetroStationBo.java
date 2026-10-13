package org.dromara.model.domain.bo;

import org.dromara.model.domain.MetroStation;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 车站信息业务对象 metro_station
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MetroStation.class, reverseConvertGenerate = false)
public class MetroStationBo extends BaseEntity {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 所属线路ID，逻辑关联metro_line.id
     */
    @NotBlank(message = "所属线路ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String lineId;

    /**
     * 车站名称，如“罗湖站”
     */
    @NotBlank(message = "车站名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 车站业务编码
     */
    private String code;

    /**
     * 车站序号
     */
    private Integer stationNo;

    /**
     * 车站类型
     */
    private String stationType;

    /**
     * 是否换乘站:0否，1是
     */
    private String isChange;

    /**
     * 行政区编码，关联base_config
     */
    private String districtCode;

    /**
     * 街道编码，关联base_config
     */
    private String streetCode;

    /**
     * 排序权重
     */
    private Long sortOrder;


}
