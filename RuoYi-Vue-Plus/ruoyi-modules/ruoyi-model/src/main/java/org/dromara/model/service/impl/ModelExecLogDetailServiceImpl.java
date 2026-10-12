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
import org.dromara.model.domain.bo.ModelExecLogDetailBo;
import org.dromara.model.domain.vo.ModelExecLogDetailVo;
import org.dromara.model.domain.ModelExecLogDetail;
import org.dromara.model.mapper.ModelExecLogDetailMapper;
import org.dromara.model.service.IModelExecLogDetailService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型执行日志明细Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelExecLogDetailServiceImpl implements IModelExecLogDetailService {

    private final ModelExecLogDetailMapper baseMapper;

    /**
     * 查询模型执行日志明细
     *
     * @param id 主键
     * @return 模型执行日志明细
     */
    @Override
    public ModelExecLogDetailVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型执行日志明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型执行日志明细分页列表
     */
    @Override
    public TableDataInfo<ModelExecLogDetailVo> queryPageList(ModelExecLogDetailBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelExecLogDetail> lqw = buildQueryWrapper(bo);
        Page<ModelExecLogDetailVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型执行日志明细列表
     *
     * @param bo 查询条件
     * @return 模型执行日志明细列表
     */
    @Override
    public List<ModelExecLogDetailVo> queryList(ModelExecLogDetailBo bo) {
        LambdaQueryWrapper<ModelExecLogDetail> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelExecLogDetail> buildQueryWrapper(ModelExecLogDetailBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelExecLogDetail> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelExecLogDetail::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getExecId()), ModelExecLogDetail::getExecId, bo.getExecId());
        lqw.eq(bo.getSeq() != null, ModelExecLogDetail::getSeq, bo.getSeq());
        lqw.eq(bo.getLogTime() != null, ModelExecLogDetail::getLogTime, bo.getLogTime());
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), ModelExecLogDetail::getLevel, bo.getLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getMessage()), ModelExecLogDetail::getMessage, bo.getMessage());
        return lqw;
    }

    /**
     * 新增模型执行日志明细
     *
     * @param bo 模型执行日志明细
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelExecLogDetailBo bo) {
        ModelExecLogDetail add = MapstructUtils.convert(bo, ModelExecLogDetail.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型执行日志明细
     *
     * @param bo 模型执行日志明细
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelExecLogDetailBo bo) {
        ModelExecLogDetail update = MapstructUtils.convert(bo, ModelExecLogDetail.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelExecLogDetail entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型执行日志明细信息
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
