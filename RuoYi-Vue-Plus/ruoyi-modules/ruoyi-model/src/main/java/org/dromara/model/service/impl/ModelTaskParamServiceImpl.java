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
import org.dromara.model.domain.bo.ModelTaskParamBo;
import org.dromara.model.domain.vo.ModelTaskParamVo;
import org.dromara.model.domain.ModelTaskParam;
import org.dromara.model.mapper.ModelTaskParamMapper;
import org.dromara.model.service.IModelTaskParamService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型任务运行参数Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelTaskParamServiceImpl implements IModelTaskParamService {

    private final ModelTaskParamMapper baseMapper;

    /**
     * 查询模型任务运行参数
     *
     * @param id 主键
     * @return 模型任务运行参数
     */
    @Override
    public ModelTaskParamVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型任务运行参数列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型任务运行参数分页列表
     */
    @Override
    public TableDataInfo<ModelTaskParamVo> queryPageList(ModelTaskParamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelTaskParam> lqw = buildQueryWrapper(bo);
        Page<ModelTaskParamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型任务运行参数列表
     *
     * @param bo 查询条件
     * @return 模型任务运行参数列表
     */
    @Override
    public List<ModelTaskParamVo> queryList(ModelTaskParamBo bo) {
        LambdaQueryWrapper<ModelTaskParam> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelTaskParam> buildQueryWrapper(ModelTaskParamBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelTaskParam> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelTaskParam::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getTaskId()), ModelTaskParam::getTaskId, bo.getTaskId());
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelTaskParam::getModelId, bo.getModelId());
        lqw.eq(StringUtils.isNotBlank(bo.getParamKey()), ModelTaskParam::getParamKey, bo.getParamKey());
        lqw.eq(StringUtils.isNotBlank(bo.getParamValue()), ModelTaskParam::getParamValue, bo.getParamValue());
        return lqw;
    }

    /**
     * 新增模型任务运行参数
     *
     * @param bo 模型任务运行参数
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelTaskParamBo bo) {
        ModelTaskParam add = MapstructUtils.convert(bo, ModelTaskParam.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型任务运行参数
     *
     * @param bo 模型任务运行参数
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelTaskParamBo bo) {
        ModelTaskParam update = MapstructUtils.convert(bo, ModelTaskParam.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelTaskParam entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型任务运行参数信息
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
