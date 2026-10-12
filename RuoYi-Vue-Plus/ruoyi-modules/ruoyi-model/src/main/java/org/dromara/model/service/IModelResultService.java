package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelResultVo;
import org.dromara.model.domain.bo.ModelResultBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 模型执行结果明细Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IModelResultService {

    /**
     * 查询模型执行结果明细
     *
     * @param id 主键
     * @return 模型执行结果明细
     */
    ModelResultVo queryById(String id);

    /**
     * 分页查询模型执行结果明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型执行结果明细分页列表
     */
    TableDataInfo<ModelResultVo> queryPageList(ModelResultBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型执行结果明细列表
     *
     * @param bo 查询条件
     * @return 模型执行结果明细列表
     */
    List<ModelResultVo> queryList(ModelResultBo bo);

    /**
     * 新增模型执行结果明细
     *
     * @param bo 模型执行结果明细
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelResultBo bo);

    /**
     * 修改模型执行结果明细
     *
     * @param bo 模型执行结果明细
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelResultBo bo);

    /**
     * 校验并批量删除模型执行结果明细信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
