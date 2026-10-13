package org.dromara.model.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.model.domain.MetroStation;
import org.dromara.model.domain.bo.MetroStationBo;
import org.dromara.model.domain.vo.MetroStationVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 车站信息Mapper接口
 *
 * @author Lion Li
 * @date 2026-10-13
 */
public interface MetroStationMapper extends BaseMapperPlus<MetroStation, MetroStationVo> {

    /**
     * 联表分页查询车站列表（附带线路名称）
     *
     * @param page 分页参数
     * @param bo   查询条件
     * @return 车站分页列表
     */
    Page<MetroStationVo> selectVoPageWithRef(Page<MetroStationVo> page, @Param("bo") MetroStationBo bo);

    /**
     * 联表查询车站列表（附带线路名称）
     *
     * @param bo 查询条件
     * @return 车站列表
     */
    List<MetroStationVo> selectVoListWithRef(@Param("bo") MetroStationBo bo);

    /**
     * 联表查询车站详情（附带线路名称）
     *
     * @param id 主键
     * @return 车站详情
     */
    MetroStationVo selectVoByIdWithRef(@Param("id") String id);

}
