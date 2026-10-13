package org.dromara.model.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.model.domain.ModelAnomalyEvent;
import org.dromara.model.domain.bo.ModelAnomalyEventBo;
import org.dromara.model.domain.vo.ModelAnomalyEventVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

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

}
