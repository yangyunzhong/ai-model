package org.dromara.model.domain.vo;

import org.dromara.model.domain.MetroStation;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * 车站信息视图对象 metro_station
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MetroStation.class)
public class MetroStationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 所属线路ID，逻辑关联metro_line.id
     */
    @ExcelProperty(value = "所属线路ID")
    private String lineId;

    /**
     * 所属线路名称（联表 metro_line.name）
     */
    @ExcelProperty(value = "所属线路名称")
    private String lineName;

    /**
     * 车站名称，如“罗湖站”
     */
    @ExcelProperty(value = "车站名称")
    private String name;

    /**
     * 车站业务编码
     */
    @ExcelProperty(value = "车站业务编码")
    private String code;

    /**
     * 车站序号
     */
    @ExcelProperty(value = "车站序号")
    private Integer stationNo;

    /**
     * 车站类型
     */
    @ExcelProperty(value = "车站类型")
    private String stationType;

    /**
     * 是否换乘站:0否，1是
     */
    @ExcelProperty(value = "是否换乘站")
    private String isChange;

    /**
     * 行政区编码，关联base_config
     */
    @ExcelProperty(value = "行政区编码")
    private String districtCode;

    /**
     * 街道编码，关联base_config
     */
    @ExcelProperty(value = "街道编码")
    private String streetCode;

    /**
     * 排序权重
     */
    @ExcelProperty(value = "排序权重")
    private Long sortOrder;


}
