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
import org.dromara.model.domain.bo.ModelCatalogBo;
import org.dromara.model.domain.vo.ModelCatalogVo;
import org.dromara.model.domain.ModelCatalog;
import org.dromara.model.mapper.ModelCatalogMapper;
import org.dromara.model.service.IModelCatalogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型类别Service业务层处理
 *
 * @author Liu Cheng
 * @date 2026-09-29
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelCatalogServiceImpl implements IModelCatalogService {

    private final ModelCatalogMapper baseMapper;

    /**
     * 查询模型类别
     *
     * @param id 主键
     * @return 模型类别
     */
    @Override
    public ModelCatalogVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型类别列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型类别分页列表
     */
    @Override
    public TableDataInfo<ModelCatalogVo> queryPageList(ModelCatalogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelCatalog> lqw = buildQueryWrapper(bo);
        Page<ModelCatalogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型类别列表
     *
     * @param bo 查询条件
     * @return 模型类别列表
     */
    @Override
    public List<ModelCatalogVo> queryList(ModelCatalogBo bo) {
        LambdaQueryWrapper<ModelCatalog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelCatalog> buildQueryWrapper(ModelCatalogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelCatalog> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelCatalog::getId);
        lqw.like(StringUtils.isNotBlank(bo.getCatalogName()), ModelCatalog::getCatalogName, bo.getCatalogName());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), ModelCatalog::getDeviceType, bo.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), ModelCatalog::getDescription, bo.getDescription());
        return lqw;
    }

    /**
     * 新增模型类别
     *
     * @param bo 模型类别
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelCatalogBo bo) {
        ModelCatalog add = MapstructUtils.convert(bo, ModelCatalog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型类别
     *
     * @param bo 模型类别
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelCatalogBo bo) {
        ModelCatalog update = MapstructUtils.convert(bo, ModelCatalog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelCatalog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型类别信息
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
