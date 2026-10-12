package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.satoken.utils.LoginHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelExecLogBo;
import org.dromara.model.domain.bo.ModelRunOnceBo;
import org.dromara.model.domain.vo.ModelExecLogVo;
import org.dromara.model.domain.ModelDefinition;
import org.dromara.model.domain.ModelDevice;
import org.dromara.model.domain.ModelExecLog;
import org.dromara.model.domain.ModelParam;
import org.dromara.model.domain.ModelScript;
import org.dromara.model.mapper.ModelDefinitionMapper;
import org.dromara.model.mapper.ModelDeviceMapper;
import org.dromara.model.mapper.ModelExecLogMapper;
import org.dromara.model.mapper.ModelParamMapper;
import org.dromara.model.mapper.ModelScriptMapper;
import org.dromara.model.orchestration.ModelExecContext;
import org.dromara.model.orchestration.ModelExecOrchestrator;
import org.dromara.model.service.IModelExecLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型执行记录Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelExecLogServiceImpl implements IModelExecLogService {

    /**
     * 默认触发方式与设备状态
     */
    private static final String DEFAULT_TRIGGER_TYPE = "单次运行";
    private static final String DEVICE_STATUS_ENABLED = "启用";
    private static final String PARAM_DIRECTION_INPUT = "输入";
    private static final String PARAM_DIRECTION_OUTPUT = "输出";

    private final ModelExecLogMapper baseMapper;
    private final ModelDefinitionMapper definitionMapper;
    private final ModelScriptMapper scriptMapper;
    private final ModelDeviceMapper deviceMapper;
    private final ModelParamMapper paramMapper;
    private final ModelExecOrchestrator orchestrator;

    /**
     * 查询模型执行记录
     *
     * @param id 主键
     * @return 模型执行记录
     */
    @Override
    public ModelExecLogVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型执行记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型执行记录分页列表
     */
    @Override
    public TableDataInfo<ModelExecLogVo> queryPageList(ModelExecLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelExecLog> lqw = buildQueryWrapper(bo);
        Page<ModelExecLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型执行记录列表
     *
     * @param bo 查询条件
     * @return 模型执行记录列表
     */
    @Override
    public List<ModelExecLogVo> queryList(ModelExecLogBo bo) {
        LambdaQueryWrapper<ModelExecLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelExecLog> buildQueryWrapper(ModelExecLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelExecLog> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelExecLog::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getExecNo()), ModelExecLog::getExecNo, bo.getExecNo());
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelExecLog::getModelId, bo.getModelId());
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), ModelExecLog::getModelName, bo.getModelName());
        lqw.eq(StringUtils.isNotBlank(bo.getTaskId()), ModelExecLog::getTaskId, bo.getTaskId());
        lqw.eq(StringUtils.isNotBlank(bo.getTriggerType()), ModelExecLog::getTriggerType, bo.getTriggerType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ModelExecLog::getStatus, bo.getStatus());
        lqw.eq(bo.getStartTime() != null, ModelExecLog::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, ModelExecLog::getEndTime, bo.getEndTime());
        lqw.eq(bo.getCostMs() != null, ModelExecLog::getCostMs, bo.getCostMs());
        lqw.eq(bo.getDeviceCount() != null, ModelExecLog::getDeviceCount, bo.getDeviceCount());
        lqw.eq(bo.getSuccessCount() != null, ModelExecLog::getSuccessCount, bo.getSuccessCount());
        lqw.eq(bo.getFailCount() != null, ModelExecLog::getFailCount, bo.getFailCount());
        lqw.eq(StringUtils.isNotBlank(bo.getExecParams()), ModelExecLog::getExecParams, bo.getExecParams());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptId()), ModelExecLog::getScriptId, bo.getScriptId());
        lqw.eq(StringUtils.isNotBlank(bo.getErrorMsg()), ModelExecLog::getErrorMsg, bo.getErrorMsg());
        lqw.eq(StringUtils.isNotBlank(bo.getExecutorId()), ModelExecLog::getExecutorId, bo.getExecutorId());
        return lqw;
    }

    /**
     * 新增模型执行记录
     *
     * @param bo 模型执行记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelExecLogBo bo) {
        ModelExecLog add = MapstructUtils.convert(bo, ModelExecLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型执行记录
     *
     * @param bo 模型执行记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelExecLogBo bo) {
        ModelExecLog update = MapstructUtils.convert(bo, ModelExecLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelExecLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型执行记录信息
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

    @Override
    public String runOnce(ModelRunOnceBo bo) {
        ModelDefinition definition = definitionMapper.selectById(bo.getModelId());
        if (definition == null) {
            throw new ServiceException("模型不存在：" + bo.getModelId());
        }
        ModelScript script = selectCurrentScript(bo.getModelId());
        List<ModelParam> params = paramMapper.selectList(Wrappers.<ModelParam>lambdaQuery()
            .eq(ModelParam::getModelId, bo.getModelId())
            .orderByAsc(ModelParam::getSortOrder));

        ModelExecContext.ModelExecContextBuilder builder = ModelExecContext.builder()
            .modelId(definition.getId())
            .modelName(definition.getModelName())
            .triggerType(StringUtils.isBlank(bo.getTriggerType()) ? DEFAULT_TRIGGER_TYPE : bo.getTriggerType())
            .executorId(currentExecutorId())
            .params(bo.getParams())
            .inputParams(toParamMeta(params, PARAM_DIRECTION_INPUT))
            .outputParams(toParamMeta(params, PARAM_DIRECTION_OUTPUT))
            .containerId(definition.getContainerId())
            .containerScriptPath(definition.getScriptPath())
            .devices(resolveDevices(definition, bo.getDeviceIds()));
        if (script != null) {
            builder.scriptId(script.getId())
                .scriptVersion(script.getScriptVersion())
                .entryFunction(script.getEntryFunction());
        }
        return orchestrator.runOnce(builder.build());
    }

    /**
     * 取模型当前生效脚本版本
     */
    private ModelScript selectCurrentScript(String modelId) {
        List<ModelScript> scripts = scriptMapper.selectList(Wrappers.<ModelScript>lambdaQuery()
            .eq(ModelScript::getModelId, modelId)
            .eq(ModelScript::getIsCurrent, "1")
            .orderByDesc(ModelScript::getCreateTime));
        return scripts.isEmpty() ? null : scripts.get(0);
    }

    /**
     * 将模型参数按方向转换为执行上下文中的参数元数据
     */
    private List<ModelExecContext.ParamMeta> toParamMeta(List<ModelParam> params, String direction) {
        List<ModelExecContext.ParamMeta> result = new ArrayList<>();
        for (ModelParam param : params) {
            if (!direction.equals(param.getParamDirection())) {
                continue;
            }
            result.add(ModelExecContext.ParamMeta.builder()
                .paramKey(param.getParamKey())
                .paramName(param.getParamName())
                .paramType(param.getParamType())
                .defaultValue(param.getDefaultValue())
                .build());
        }
        return result;
    }

    /**
     * 解析参与本次执行的设备：优先按显式设备ID，否则取模型适用类型下的启用设备
     */
    private List<ModelExecContext.DeviceInfo> resolveDevices(ModelDefinition definition, List<String> deviceIds) {
        LambdaQueryWrapper<ModelDevice> lqw = Wrappers.<ModelDevice>lambdaQuery()
            .eq(ModelDevice::getStatus, DEVICE_STATUS_ENABLED);
        if (deviceIds != null && !deviceIds.isEmpty()) {
            lqw.in(ModelDevice::getId, deviceIds);
        } else if (StringUtils.isNotBlank(definition.getDeviceType())) {
            lqw.eq(ModelDevice::getDeviceType, definition.getDeviceType());
        }
        lqw.orderByAsc(ModelDevice::getSortOrder);
        List<ModelDevice> devices = deviceMapper.selectList(lqw);

        List<ModelExecContext.DeviceInfo> result = new ArrayList<>(devices.size());
        for (ModelDevice device : devices) {
            result.add(ModelExecContext.DeviceInfo.builder()
                .deviceId(device.getId())
                .deviceName(device.getDeviceName())
                .lineId(device.getLineId())
                .stationId(device.getStationId())
                .deviceType(device.getDeviceType())
                .build());
        }
        return result;
    }

    /**
     * 当前执行人ID（登录态下记录，非登录场景返回空）
     */
    private String currentExecutorId() {
        try {
            Long userId = LoginHelper.isLogin() ? LoginHelper.getUserId() : null;
            return userId == null ? null : String.valueOf(userId);
        } catch (Exception e) {
            log.debug("获取当前执行人失败: {}", e.getMessage());
            return null;
        }
    }
}
