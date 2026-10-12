package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelDefinitionVo;
import org.dromara.model.domain.bo.ModelDefinitionBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 模型定义Service接口
 *
 * @author Lion Li
 * @date 2026-09-30
 */
public interface IModelDefinitionService {

    /**
     * 查询模型定义
     *
     * @param id 主键
     * @return 模型定义
     */
    ModelDefinitionVo queryById(String id);

    /**
     * 分页查询模型定义列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型定义分页列表
     */
    TableDataInfo<ModelDefinitionVo> queryPageList(ModelDefinitionBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型定义列表
     *
     * @param bo 查询条件
     * @return 模型定义列表
     */
    List<ModelDefinitionVo> queryList(ModelDefinitionBo bo);

    /**
     * 新增模型定义
     *
     * @param bo 模型定义
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelDefinitionBo bo);

    /**
     * 修改模型定义
     *
     * @param bo 模型定义
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelDefinitionBo bo);

    /**
     * 校验并批量删除模型定义信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
