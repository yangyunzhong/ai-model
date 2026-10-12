package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelTaskBo;
import org.dromara.model.domain.vo.ModelTaskVo;
import org.dromara.model.domain.ModelTask;
import org.dromara.model.mapper.ModelTaskMapper;
import org.dromara.model.service.IModelTaskService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型任务Service业务层处理
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelTaskServiceImpl implements IModelTaskService {

    private final ModelTaskMapper baseMapper;

    /**
     * 查询模型任务
     *
     * @param id 主键
     * @return 模型任务
     */
    @Override
    public ModelTaskVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型任务列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型任务分页列表
     */
    @Override
    public TableDataInfo<ModelTaskVo> queryPageList(ModelTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelTask> lqw = buildQueryWrapper(bo);
        Page<ModelTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型任务列表
     *
     * @param bo 查询条件
     * @return 模型任务列表
     */
    @Override
    public List<ModelTaskVo> queryList(ModelTaskBo bo) {
        LambdaQueryWrapper<ModelTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelTask> buildQueryWrapper(ModelTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelTask::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelTask::getModelId, bo.getModelId());
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), ModelTask::getTaskName, bo.getTaskName());
        lqw.eq(StringUtils.isNotBlank(bo.getCronExpression()), ModelTask::getCronExpression, bo.getCronExpression());
        lqw.eq(StringUtils.isNotBlank(bo.getCronDescription()), ModelTask::getCronDescription, bo.getCronDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ModelTask::getStatus, bo.getStatus());
        lqw.eq(bo.getLastExecuteTime() != null, ModelTask::getLastExecuteTime, bo.getLastExecuteTime());
        lqw.eq(bo.getNextExecuteTime() != null, ModelTask::getNextExecuteTime, bo.getNextExecuteTime());
        return lqw;
    }

    /**
     * 新增模型任务
     *
     * @param bo 模型任务
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelTaskBo bo) {
        ModelTask add = MapstructUtils.convert(bo, ModelTask.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型任务
     *
     * @param bo 模型任务
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelTaskBo bo) {
        ModelTask update = MapstructUtils.convert(bo, ModelTask.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelTask entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型任务信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
