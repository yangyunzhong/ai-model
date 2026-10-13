package org.dromara.model.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 车站信息对象 metro_station
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("metro_station")
public class MetroStation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 所属线路ID，逻辑关联metro_line.id
     */
    private String lineId;

    /**
     * 车站名称，如“罗湖站”
     */
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
