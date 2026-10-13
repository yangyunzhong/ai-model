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
import org.springframework.transaction.annotation.Transactional;
import org.dromara.model.domain.bo.ModelDefinitionBo;
import org.dromara.model.domain.vo.ModelDefinitionVo;
import org.dromara.model.domain.ModelDefinition;
import org.dromara.model.mapper.ModelDefinitionMapper;
import org.dromara.model.service.IModelDefinitionService;
import org.dromara.model.service.IModelRuntimeService;
import org.dromara.model.service.IModelTaskService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型定义Service业务层处理
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelDefinitionServiceImpl implements IModelDefinitionService {

    private final ModelDefinitionMapper baseMapper;

    private final IModelRuntimeService modelRuntimeService;

    private final IModelTaskService modelTaskService;

    /**
     * 查询模型定义
     *
     * @param id 主键
     * @return 模型定义
     */
    @Override
    public ModelDefinitionVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型定义列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型定义分页列表
     */
    @Override
    public TableDataInfo<ModelDefinitionVo> queryPageList(ModelDefinitionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelDefinition> lqw = buildQueryWrapper(bo);
        Page<ModelDefinitionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型定义列表
     *
     * @param bo 查询条件
     * @return 模型定义列表
     */
    @Override
    public List<ModelDefinitionVo> queryList(ModelDefinitionBo bo) {
        LambdaQueryWrapper<ModelDefinition> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelDefinition> buildQueryWrapper(ModelDefinitionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelDefinition> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelDefinition::getId);
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), ModelDefinition::getModelName, bo.getModelName());
        lqw.eq(StringUtils.isNotBlank(bo.getCatalogId()), ModelDefinition::getCatalogId, bo.getCatalogId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), ModelDefinition::getDeviceType, bo.getDeviceType());
        lqw.eq(bo.getStatisticsCycleDays() != null, ModelDefinition::getStatisticsCycleDays, bo.getStatisticsCycleDays());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ModelDefinition::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getRegisterType()), ModelDefinition::getRegisterType, bo.getRegisterType());
        lqw.eq(bo.getRegisterTime() != null, ModelDefinition::getRegisterTime, bo.getRegisterTime());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), ModelDefinition::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptPath()), ModelDefinition::getScriptPath, bo.getScriptPath());
        return lqw;
    }

    /**
     * 新增模型定义
     *
     * <p>模型与初始任务需原子创建：任务创建失败时连同模型一起回滚，避免出现无任务的半成品模型。</p>
     *
     * @param bo 模型定义
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(ModelDefinitionBo bo) {
        ModelDefinition add = MapstructUtils.convert(bo, ModelDefinition.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 注册模型后自动创建一条初始任务（仅关联模型，状态默认「暂停」）
            modelTaskService.createInitialTask(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型定义
     *
     * @param bo 模型定义
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelDefinitionBo bo) {
        ModelDefinition update = MapstructUtils.convert(bo, ModelDefinition.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelDefinition entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型定义信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        // 逐个模型清理运行时物料：容器、宿主机脚本目录、MinIO 脚本对象与 model_script 记录
        for (String id : ids) {
            modelRuntimeService.destroy(id);
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
