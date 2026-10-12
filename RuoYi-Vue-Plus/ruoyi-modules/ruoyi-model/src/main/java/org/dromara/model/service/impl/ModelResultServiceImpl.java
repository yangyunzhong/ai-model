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
import org.dromara.model.domain.bo.ModelResultBo;
import org.dromara.model.domain.vo.ModelResultVo;
import org.dromara.model.domain.ModelResult;
import org.dromara.model.mapper.ModelResultMapper;
import org.dromara.model.service.IModelResultService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型执行结果明细Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelResultServiceImpl implements IModelResultService {

    private final ModelResultMapper baseMapper;

    /**
     * 查询模型执行结果明细
     *
     * @param id 主键
     * @return 模型执行结果明细
     */
    @Override
    public ModelResultVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型执行结果明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型执行结果明细分页列表
     */
    @Override
    public TableDataInfo<ModelResultVo> queryPageList(ModelResultBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelResult> lqw = buildQueryWrapper(bo);
        Page<ModelResultVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型执行结果明细列表
     *
     * @param bo 查询条件
     * @return 模型执行结果明细列表
     */
    @Override
    public List<ModelResultVo> queryList(ModelResultBo bo) {
        LambdaQueryWrapper<ModelResult> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelResult> buildQueryWrapper(ModelResultBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelResult> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelResult::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getExecId()), ModelResult::getExecId, bo.getExecId());
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelResult::getModelId, bo.getModelId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceId()), ModelResult::getDeviceId, bo.getDeviceId());
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), ModelResult::getDeviceName, bo.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(bo.getLineId()), ModelResult::getLineId, bo.getLineId());
        lqw.eq(StringUtils.isNotBlank(bo.getStationId()), ModelResult::getStationId, bo.getStationId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), ModelResult::getDeviceType, bo.getDeviceType());
        lqw.eq(bo.getHealthScore() != null, ModelResult::getHealthScore, bo.getHealthScore());
        lqw.eq(StringUtils.isNotBlank(bo.getHealthLevel()), ModelResult::getHealthLevel, bo.getHealthLevel());
        lqw.eq(bo.getDimBody() != null, ModelResult::getDimBody, bo.getDimBody());
        lqw.eq(bo.getDimCondition() != null, ModelResult::getDimCondition, bo.getDimCondition());
        lqw.eq(bo.getDimFault() != null, ModelResult::getDimFault, bo.getDimFault());
        lqw.eq(bo.getDimMaintain() != null, ModelResult::getDimMaintain, bo.getDimMaintain());
        lqw.eq(bo.getDimEnv() != null, ModelResult::getDimEnv, bo.getDimEnv());
        return lqw;
    }

    /**
     * 新增模型执行结果明细
     *
     * @param bo 模型执行结果明细
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelResultBo bo) {
        ModelResult add = MapstructUtils.convert(bo, ModelResult.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型执行结果明细
     *
     * @param bo 模型执行结果明细
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelResultBo bo) {
        ModelResult update = MapstructUtils.convert(bo, ModelResult.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelResult entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型执行结果明细信息
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
