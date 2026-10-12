package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelCatalogVo;
import org.dromara.model.domain.bo.ModelCatalogBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 模型类别Service接口
 *
 * @author Liu Cheng
 * @date 2026-09-29
 */
public interface IModelCatalogService {

    /**
     * 查询模型类别
     *
     * @param id 主键
     * @return 模型类别
     */
    ModelCatalogVo queryById(String id);

    /**
     * 分页查询模型类别列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型类别分页列表
     */
    TableDataInfo<ModelCatalogVo> queryPageList(ModelCatalogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型类别列表
     *
     * @param bo 查询条件
     * @return 模型类别列表
     */
    List<ModelCatalogVo> queryList(ModelCatalogBo bo);

    /**
     * 新增模型类别
     *
     * @param bo 模型类别
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelCatalogBo bo);

    /**
     * 修改模型类别
     *
     * @param bo 模型类别
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelCatalogBo bo);

    /**
     * 校验并批量删除模型类别信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
