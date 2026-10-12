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
import org.dromara.model.domain.bo.ModelComparisonRecordBo;
import org.dromara.model.domain.vo.ModelComparisonRecordVo;
import org.dromara.model.domain.ModelComparisonRecord;
import org.dromara.model.mapper.ModelComparisonRecordMapper;
import org.dromara.model.service.IModelComparisonRecordService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 数据比对记录Service业务层处理
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelComparisonRecordServiceImpl implements IModelComparisonRecordService {

    private final ModelComparisonRecordMapper baseMapper;

    /**
     * 查询数据比对记录
     *
     * @param id 主键
     * @return 数据比对记录
     */
    @Override
    public ModelComparisonRecordVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询数据比对记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 数据比对记录分页列表
     */
    @Override
    public TableDataInfo<ModelComparisonRecordVo> queryPageList(ModelComparisonRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelComparisonRecord> lqw = buildQueryWrapper(bo);
        Page<ModelComparisonRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的数据比对记录列表
     *
     * @param bo 查询条件
     * @return 数据比对记录列表
     */
    @Override
    public List<ModelComparisonRecordVo> queryList(ModelComparisonRecordBo bo) {
        LambdaQueryWrapper<ModelComparisonRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelComparisonRecord> buildQueryWrapper(ModelComparisonRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelComparisonRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelComparisonRecord::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getUserId()), ModelComparisonRecord::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceIds()), ModelComparisonRecord::getDeviceIds, bo.getDeviceIds());
        lqw.eq(StringUtils.isNotBlank(bo.getModelIds()), ModelComparisonRecord::getModelIds, bo.getModelIds());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), ModelComparisonRecord::getDeviceType, bo.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ModelComparisonRecord::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getResultSummary()), ModelComparisonRecord::getResultSummary, bo.getResultSummary());
        lqw.eq(bo.getExecutedAt() != null, ModelComparisonRecord::getExecutedAt, bo.getExecutedAt());
        lqw.eq(bo.getCompletedAt() != null, ModelComparisonRecord::getCompletedAt, bo.getCompletedAt());
        return lqw;
    }

    /**
     * 新增数据比对记录
     *
     * @param bo 数据比对记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelComparisonRecordBo bo) {
        ModelComparisonRecord add = MapstructUtils.convert(bo, ModelComparisonRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改数据比对记录
     *
     * @param bo 数据比对记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelComparisonRecordBo bo) {
        ModelComparisonRecord update = MapstructUtils.convert(bo, ModelComparisonRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelComparisonRecord entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除数据比对记录信息
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
