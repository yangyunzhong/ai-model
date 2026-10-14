package org.dromara.model.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.model.domain.ModelAnomalyEvent;
import org.dromara.model.domain.bo.ModelAnomalyEventBo;
import org.dromara.model.domain.vo.ModelAnomalyEventVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * 异物检测事件Mapper接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface ModelAnomalyEventMapper extends BaseMapperPlus<ModelAnomalyEvent, ModelAnomalyEventVo> {

    /**
     * 联表分页查询异物检测事件列表（附带线路名称、车站名称）
     *
     * @param page 分页参数
     * @param bo   查询条件
     * @return 异物检测事件分页列表
     */
    Page<ModelAnomalyEventVo> selectVoPageWithRef(Page<ModelAnomalyEventVo> page, @Param("bo") ModelAnomalyEventBo bo);

    /**
     * 联表查询异物检测事件列表（附带线路名称、车站名称）
     *
     * @param bo 查询条件
     * @return 异物检测事件列表
     */
    List<ModelAnomalyEventVo> selectVoListWithRef(@Param("bo") ModelAnomalyEventBo bo);

    /**
     * 联表查询异物检测事件详情（附带线路名称、车站名称）
     *
     * @param id 主键
     * @return 异物检测事件详情
     */
    ModelAnomalyEventVo selectVoByIdWithRef(@Param("id") String id);

    /**
     * 查询设备最近 N 条实时监测数据（按时间倒序，调用方自行反转为升序）
     *
     * @param deviceId 设备ID
     * @param limit    条数
     * @return 实时监测数据（monitor_time / working_current / real_vibration / real_motor_temp / real_power）
     */
    @Select("""
        SELECT monitor_time, working_current, real_vibration, real_motor_temp, real_power
        FROM metro_device_realtime
        WHERE device_id = #{deviceId}
        ORDER BY monitor_time DESC
        LIMIT #{limit}
        """)
    List<Map<String, Object>> selectRealtimeSeries(@Param("deviceId") String deviceId, @Param("limit") int limit);

    /**
     * 查询设备阈值配置
     *
     * @param deviceId 设备ID
     * @return 阈值项（item_name / item_value / item_unit）
     */
    @Select("""
        SELECT item_name, item_value, item_unit
        FROM metro_device_threshold
        WHERE fk_device = #{deviceId}
        """)
    List<Map<String, Object>> selectDeviceThresholds(@Param("deviceId") String deviceId);

    /**
     * 查询异物检测规则配置
     *
     * @return 规则列表（device_type / current_threshold / vibration_threshold / current_vibration_alert / extra_config）
     */
    @Select("""
        SELECT device_type, current_threshold, vibration_threshold, current_vibration_alert, extra_config
        FROM metro_foreign_object_rule
        ORDER BY device_type
        """)
    List<Map<String, Object>> selectForeignObjectRules();

}
