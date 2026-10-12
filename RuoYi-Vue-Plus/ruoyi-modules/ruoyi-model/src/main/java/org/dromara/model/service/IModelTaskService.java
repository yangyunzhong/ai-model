package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelTaskVo;
import org.dromara.model.domain.bo.ModelTaskBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 模型任务Service接口
 *
 * @author Lion Li
 * @date 2026-09-30
 */
public interface IModelTaskService {

    /**
     * 查询模型任务
     *
     * @param id 主键
     * @return 模型任务
     */
    ModelTaskVo queryById(String id);

    /**
     * 分页查询模型任务列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型任务分页列表
     */
    TableDataInfo<ModelTaskVo> queryPageList(ModelTaskBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型任务列表
     *
     * @param bo 查询条件
     * @return 模型任务列表
     */
    List<ModelTaskVo> queryList(ModelTaskBo bo);

    /**
     * 新增模型任务
     *
     * @param bo 模型任务
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelTaskBo bo);

    /**
     * 修改模型任务
     *
     * @param bo 模型任务
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelTaskBo bo);

    /**
     * 校验并批量删除模型任务信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
