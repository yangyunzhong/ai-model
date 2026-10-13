package org.dromara.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备主数据对象 metro_device（外部同步表，只读）
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Data
@TableName("metro_device")
public class ModelDevice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（设备编号）
     */
    @TableId(value = "id")
    private String id;

    /**
     * 设备类型：1-自动扶梯，2-垂直电梯
     */
    private Integer deviceType;

    /**
     * 线路ID，逻辑关联metro_line.id
     */
    private String lineId;

    /**
     * 车站ID，逻辑关联metro_station.id
     */
    private String stationId;

    /**
     * 设备品种ID，逻辑关联base_config
     */
    private String deviceVarietyCode;

    /**
     * 运行状态
     */
    private Integer runningStatus;

    /**
     * 品牌名称
     */
    private String brand;

    /**
     * 设备注册代码（特种设备登记号）
     */
    private String registrationCode;

    /**
     * 型号规格
     */
    private String modelSpec;

    /**
     * 设备使用地点详细描述
     */
    private String locationDetail;

    /**
     * 设备投入使用日期
     */
    private Date commissioningDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 设备名称（非表字段，计算生成：品牌+型号 → 注册代码 → 设备ID）
     */
    public String getDeviceName() {
        String brandPart = brand == null ? "" : brand.trim();
        String specPart = modelSpec == null ? "" : modelSpec.trim();
        String brandModel = (brandPart + " " + specPart).trim();
        if (!brandModel.isEmpty()) {
            return brandModel;
        }
        if (registrationCode != null && !registrationCode.isBlank()) {
            return registrationCode;
        }
        return id;
    }

    /**
     * 业务设备类型字符串（与model_catalog.device_type口径一致）转metro_device类型码
     *
     * @param deviceType 业务设备类型，如"自动扶梯"/"垂直电梯"
     * @return metro_device.device_type 类型码；无对应类型返回null
     */
    public static Integer typeCodeOf(String deviceType) {
        if (deviceType == null || deviceType.isBlank()) {
            return null;
        }
        return switch (deviceType) {
            case "自动扶梯" -> 1;
            case "垂直电梯" -> 2;
            default -> null;
        };
    }

    /**
     * metro_device类型码转业务设备类型字符串
     *
     * @param typeCode metro_device.device_type 类型码
     * @return 业务设备类型，如"自动扶梯"/"垂直电梯"；无法识别返回null
     */
    public static String typeNameOf(Integer typeCode) {
        if (typeCode == null) {
            return null;
        }
        return switch (typeCode) {
            case 1 -> "自动扶梯";
            case 2 -> "垂直电梯";
            default -> null;
        };
    }
}
