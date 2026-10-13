package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelAnomalyEventBo;
import org.dromara.model.domain.vo.ModelAnomalyEventVo;
import org.dromara.model.domain.ModelAnomalyEvent;
import org.dromara.model.mapper.ModelAnomalyEventMapper;
import org.dromara.model.service.IModelAnomalyEventService;

import java.util.List;
import java.util.Collection;

/**
 * 异物检测事件Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelAnomalyEventServiceImpl implements IModelAnomalyEventService {

    private final ModelAnomalyEventMapper baseMapper;

    /**
     * 查询异物检测事件
     *
     * @param id 主键
     * @return 异物检测事件
     */
    @Override
    public ModelAnomalyEventVo queryById(String id){
        return baseMapper.selectVoByIdWithRef(id);
    }

    /**
     * 分页查询异物检测事件列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 异物检测事件分页列表
     */
    @Override
    public TableDataInfo<ModelAnomalyEventVo> queryPageList(ModelAnomalyEventBo bo, PageQuery pageQuery) {
        Page<ModelAnomalyEventVo> result = baseMapper.selectVoPageWithRef(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的异物检测事件列表
     *
     * @param bo 查询条件
     * @return 异物检测事件列表
     */
    @Override
    public List<ModelAnomalyEventVo> queryList(ModelAnomalyEventBo bo) {
        return baseMapper.selectVoListWithRef(bo);
    }

    /**
     * 新增异物检测事件
     *
     * @param bo 异物检测事件
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelAnomalyEventBo bo) {
        ModelAnomalyEvent add = MapstructUtils.convert(bo, ModelAnomalyEvent.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改异物检测事件
     *
     * @param bo 异物检测事件
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelAnomalyEventBo bo) {
        ModelAnomalyEvent update = MapstructUtils.convert(bo, ModelAnomalyEvent.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelAnomalyEvent entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除异物检测事件信息
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
