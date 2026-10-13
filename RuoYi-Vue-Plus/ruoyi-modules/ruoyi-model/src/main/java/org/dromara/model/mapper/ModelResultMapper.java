package org.dromara.model.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.model.domain.ModelResult;
import org.dromara.model.domain.bo.ModelResultBo;
import org.dromara.model.domain.vo.ModelResultVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 模型执行结果明细Mapper接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface ModelResultMapper extends BaseMapperPlus<ModelResult, ModelResultVo> {

    /**
     * 联表分页查询模型执行结果明细列表（附带线路名称、车站名称）
     *
     * @param page 分页参数
     * @param bo   查询条件
     * @return 模型执行结果明细分页列表
     */
    Page<ModelResultVo> selectVoPageWithRef(Page<ModelResultVo> page, @Param("bo") ModelResultBo bo);

    /**
     * 联表查询模型执行结果明细列表（附带线路名称、车站名称）
     *
     * @param bo 查询条件
     * @return 模型执行结果明细列表
     */
    List<ModelResultVo> selectVoListWithRef(@Param("bo") ModelResultBo bo);

    /**
     * 联表查询模型执行结果明细详情（附带线路名称、车站名称）
     *
     * @param id 主键
     * @return 模型执行结果明细详情
     */
    ModelResultVo selectVoByIdWithRef(@Param("id") String id);

}
