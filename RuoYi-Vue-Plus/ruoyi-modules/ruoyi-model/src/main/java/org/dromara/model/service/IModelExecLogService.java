package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelExecLogVo;
import org.dromara.model.domain.bo.ModelExecLogBo;
import org.dromara.model.domain.bo.ModelRunOnceBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 模型执行记录Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IModelExecLogService {

    /**
     * 查询模型执行记录
     *
     * @param id 主键
     * @return 模型执行记录
     */
    ModelExecLogVo queryById(String id);

    /**
     * 分页查询模型执行记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型执行记录分页列表
     */
    TableDataInfo<ModelExecLogVo> queryPageList(ModelExecLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型执行记录列表
     *
     * @param bo 查询条件
     * @return 模型执行记录列表
     */
    List<ModelExecLogVo> queryList(ModelExecLogBo bo);

    /**
     * 新增模型执行记录
     *
     * @param bo 模型执行记录
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelExecLogBo bo);

    /**
     * 修改模型执行记录
     *
     * @param bo 模型执行记录
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelExecLogBo bo);

    /**
     * 校验并批量删除模型执行记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 单次运行模型：按当前生效脚本与目标设备执行一次，并落库执行记录与结果
     *
     * @param bo 单次运行参数
     * @return 执行记录ID（model_exec_log.id）
     */
    String runOnce(ModelRunOnceBo bo);
}
