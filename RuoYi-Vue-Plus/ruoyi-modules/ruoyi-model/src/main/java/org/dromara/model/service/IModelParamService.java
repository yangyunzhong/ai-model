package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelParamVo;
import org.dromara.model.domain.bo.ModelParamBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 模型输入输出参数Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IModelParamService {

    /**
     * 查询模型输入输出参数
     *
     * @param id 主键
     * @return 模型输入输出参数
     */
    ModelParamVo queryById(String id);

    /**
     * 分页查询模型输入输出参数列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型输入输出参数分页列表
     */
    TableDataInfo<ModelParamVo> queryPageList(ModelParamBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型输入输出参数列表
     *
     * @param bo 查询条件
     * @return 模型输入输出参数列表
     */
    List<ModelParamVo> queryList(ModelParamBo bo);

    /**
     * 新增模型输入输出参数
     *
     * @param bo 模型输入输出参数
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelParamBo bo);

    /**
     * 修改模型输入输出参数
     *
     * @param bo 模型输入输出参数
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelParamBo bo);

    /**
     * 校验并批量删除模型输入输出参数信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
