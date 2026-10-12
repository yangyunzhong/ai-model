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
import org.dromara.model.domain.bo.ModelParamBo;
import org.dromara.model.domain.vo.ModelParamVo;
import org.dromara.model.domain.ModelParam;
import org.dromara.model.mapper.ModelParamMapper;
import org.dromara.model.service.IModelParamService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型输入输出参数Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelParamServiceImpl implements IModelParamService {

    private final ModelParamMapper baseMapper;

    /**
     * 查询模型输入输出参数
     *
     * @param id 主键
     * @return 模型输入输出参数
     */
    @Override
    public ModelParamVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型输入输出参数列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型输入输出参数分页列表
     */
    @Override
    public TableDataInfo<ModelParamVo> queryPageList(ModelParamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelParam> lqw = buildQueryWrapper(bo);
        Page<ModelParamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型输入输出参数列表
     *
     * @param bo 查询条件
     * @return 模型输入输出参数列表
     */
    @Override
    public List<ModelParamVo> queryList(ModelParamBo bo) {
        LambdaQueryWrapper<ModelParam> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelParam> buildQueryWrapper(ModelParamBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelParam> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelParam::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelParam::getModelId, bo.getModelId());
        lqw.eq(StringUtils.isNotBlank(bo.getParamDirection()), ModelParam::getParamDirection, bo.getParamDirection());
        lqw.eq(StringUtils.isNotBlank(bo.getParamKey()), ModelParam::getParamKey, bo.getParamKey());
        lqw.like(StringUtils.isNotBlank(bo.getParamName()), ModelParam::getParamName, bo.getParamName());
        lqw.eq(StringUtils.isNotBlank(bo.getParamType()), ModelParam::getParamType, bo.getParamType());
        lqw.eq(StringUtils.isNotBlank(bo.getRequired()), ModelParam::getRequired, bo.getRequired());
        lqw.eq(StringUtils.isNotBlank(bo.getDefaultValue()), ModelParam::getDefaultValue, bo.getDefaultValue());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), ModelParam::getDescription, bo.getDescription());
        lqw.eq(bo.getSortOrder() != null, ModelParam::getSortOrder, bo.getSortOrder());
        return lqw;
    }

    /**
     * 新增模型输入输出参数
     *
     * @param bo 模型输入输出参数
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelParamBo bo) {
        ModelParam add = MapstructUtils.convert(bo, ModelParam.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型输入输出参数
     *
     * @param bo 模型输入输出参数
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelParamBo bo) {
        ModelParam update = MapstructUtils.convert(bo, ModelParam.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelParam entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型输入输出参数信息
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
