package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelResultBo;
import org.dromara.model.domain.vo.ModelResultVo;
import org.dromara.model.domain.ModelResult;
import org.dromara.model.mapper.ModelResultMapper;
import org.dromara.model.service.IModelResultService;

import java.util.List;
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
        return baseMapper.selectVoByIdWithRef(id);
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
        Page<ModelResultVo> result = baseMapper.selectVoPageWithRef(pageQuery.build(), bo);
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
        return baseMapper.selectVoListWithRef(bo);
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
