package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelComparisonDetailVo;
import org.dromara.model.domain.bo.ModelComparisonDetailBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 比对详细结果Service接口
 *
 * @author Lion Li
 * @date 2026-09-30
 */
public interface IModelComparisonDetailService {

    /**
     * 查询比对详细结果
     *
     * @param id 主键
     * @return 比对详细结果
     */
    ModelComparisonDetailVo queryById(String id);

    /**
     * 分页查询比对详细结果列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比对详细结果分页列表
     */
    TableDataInfo<ModelComparisonDetailVo> queryPageList(ModelComparisonDetailBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的比对详细结果列表
     *
     * @param bo 查询条件
     * @return 比对详细结果列表
     */
    List<ModelComparisonDetailVo> queryList(ModelComparisonDetailBo bo);

    /**
     * 新增比对详细结果
     *
     * @param bo 比对详细结果
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelComparisonDetailBo bo);

    /**
     * 修改比对详细结果
     *
     * @param bo 比对详细结果
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelComparisonDetailBo bo);

    /**
     * 校验并批量删除比对详细结果信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
