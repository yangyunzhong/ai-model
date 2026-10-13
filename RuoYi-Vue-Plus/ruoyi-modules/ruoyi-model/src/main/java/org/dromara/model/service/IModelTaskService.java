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

    /**
     * 注册模型后自动创建初始任务
     *
     * <p>初始任务仅关联模型ID，任务名称、执行周期、最近执行、下次执行均为空，
     * 状态默认「暂停」，待用户配置执行周期后通过「立即执行」或「开启」启动。</p>
     *
     * @param modelId 关联模型ID
     */
    void createInitialTask(String modelId);

    /**
     * 立即执行模型任务：对该模型适用设备类型下的全部启用设备执行一次计算并落库
     *
     * @param id 任务主键
     * @return 是否执行成功
     */
    Boolean runNow(String id);

    /**
     * 切换任务启停状态，并同步 SnailJob 作业状态
     *
     * @param id 任务主键
     * @return 是否切换成功
     */
    Boolean toggleStatus(String id);

    /**
     * 一次性任务触发完成后的收尾：回写最近执行时间并将任务状态置为「暂停」
     *
     * @param taskId 任务主键
     */
    void finishOnceTask(String taskId);
}
