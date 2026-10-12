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
import org.dromara.model.domain.bo.ModelComparisonDetailBo;
import org.dromara.model.domain.vo.ModelComparisonDetailVo;
import org.dromara.model.domain.ModelComparisonDetail;
import org.dromara.model.mapper.ModelComparisonDetailMapper;
import org.dromara.model.service.IModelComparisonDetailService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 比对详细结果Service业务层处理
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelComparisonDetailServiceImpl implements IModelComparisonDetailService {

    private final ModelComparisonDetailMapper baseMapper;

    /**
     * 查询比对详细结果
     *
     * @param id 主键
     * @return 比对详细结果
     */
    @Override
    public ModelComparisonDetailVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询比对详细结果列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比对详细结果分页列表
     */
    @Override
    public TableDataInfo<ModelComparisonDetailVo> queryPageList(ModelComparisonDetailBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelComparisonDetail> lqw = buildQueryWrapper(bo);
        Page<ModelComparisonDetailVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的比对详细结果列表
     *
     * @param bo 查询条件
     * @return 比对详细结果列表
     */
    @Override
    public List<ModelComparisonDetailVo> queryList(ModelComparisonDetailBo bo) {
        LambdaQueryWrapper<ModelComparisonDetail> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelComparisonDetail> buildQueryWrapper(ModelComparisonDetailBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelComparisonDetail> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelComparisonDetail::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getRecordId()), ModelComparisonDetail::getRecordId, bo.getRecordId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceId()), ModelComparisonDetail::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelComparisonDetail::getModelId, bo.getModelId());
        lqw.eq(bo.getHealthScore() != null, ModelComparisonDetail::getHealthScore, bo.getHealthScore());
        lqw.eq(StringUtils.isNotBlank(bo.getHealthLevel()), ModelComparisonDetail::getHealthLevel, bo.getHealthLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getDimensionScores()), ModelComparisonDetail::getDimensionScores, bo.getDimensionScores());
        return lqw;
    }

    /**
     * 新增比对详细结果
     *
     * @param bo 比对详细结果
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelComparisonDetailBo bo) {
        ModelComparisonDetail add = MapstructUtils.convert(bo, ModelComparisonDetail.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改比对详细结果
     *
     * @param bo 比对详细结果
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelComparisonDetailBo bo) {
        ModelComparisonDetail update = MapstructUtils.convert(bo, ModelComparisonDetail.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelComparisonDetail entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除比对详细结果信息
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
