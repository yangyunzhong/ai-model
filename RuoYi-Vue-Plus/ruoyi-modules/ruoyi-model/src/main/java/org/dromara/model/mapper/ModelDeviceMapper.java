package org.dromara.model.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.model.domain.ModelDevice;
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 设备台账Mapper接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface ModelDeviceMapper extends BaseMapperPlus<ModelDevice, ModelDeviceVo> {

    /**
     * 联表分页查询设备台账列表（附带线路名称、车站名称）
     *
     * @param page 分页参数
     * @param bo   查询条件
     * @return 设备台账分页列表
     */
    Page<ModelDeviceVo> selectVoPageWithRef(Page<ModelDeviceVo> page, @Param("bo") ModelDeviceBo bo);

    /**
     * 联表查询设备台账列表（附带线路名称、车站名称）
     *
     * @param bo 查询条件
     * @return 设备台账列表
     */
    List<ModelDeviceVo> selectVoListWithRef(@Param("bo") ModelDeviceBo bo);

    /**
     * 联表查询设备台账详情（附带线路名称、车站名称）
     *
     * @param id 主键
     * @return 设备台账详情
     */
    ModelDeviceVo selectVoByIdWithRef(@Param("id") String id);

}
