package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.satoken.utils.LoginHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.model.domain.bo.ModelComparisonRecordBo;
import org.dromara.model.domain.bo.ModelComparisonExecuteBo;
import org.dromara.model.domain.vo.ModelComparisonRecordVo;
import org.dromara.model.domain.vo.ModelComparisonExecuteVo;
import org.dromara.model.domain.vo.ModelDefinitionVo;
import org.dromara.model.domain.ModelComparisonRecord;
import org.dromara.model.domain.ModelComparisonDetail;
import org.dromara.model.domain.ModelDefinition;
import org.dromara.model.domain.ModelDevice;
import org.dromara.model.domain.ModelResult;
import org.dromara.model.mapper.ModelComparisonRecordMapper;
import org.dromara.model.mapper.ModelComparisonDetailMapper;
import org.dromara.model.mapper.ModelDefinitionMapper;
import org.dromara.model.mapper.ModelDeviceMapper;
import org.dromara.model.mapper.ModelResultMapper;
import org.dromara.model.service.IModelComparisonRecordService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final ModelComparisonDetailMapper comparisonDetailMapper;
    private final ModelDefinitionMapper definitionMapper;
    private final ModelDeviceMapper deviceMapper;
    private final ModelResultMapper resultMapper;

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

    /**
     * 执行数据比对：取各模型对同一批设备的最新历史结果，落库并返回比对矩阵
     *
     * @param bo 比对执行参数（设备品种、设备列表、模型列表）
     * @return 比对结果（模型、设备、评分明细与一致性摘要）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelComparisonExecuteVo execute(ModelComparisonExecuteBo bo) {
        // 1. 校验模型数量：至少 2 个，最多 5 个
        List<String> modelIds = bo.getModelIds() == null ? List.of() : bo.getModelIds().stream()
            .filter(StringUtils::isNotBlank).distinct().toList();
        if (modelIds.size() < 2) {
            throw new ServiceException("请至少选择 2 个模型进行比对");
        }
        if (modelIds.size() > 5) {
            throw new ServiceException("最多选择 5 个模型进行比对");
        }

        // 2. 解析参与比对的设备（取自 metro_device 外部主数据）：优先显式选择，否则按设备品种取全部设备
        List<ModelDevice> devices;
        if (bo.getDeviceIds() != null && !bo.getDeviceIds().isEmpty()) {
            List<String> selectedDeviceIds = bo.getDeviceIds().stream()
                .filter(StringUtils::isNotBlank).distinct().toList();
            devices = deviceMapper.selectList(Wrappers.<ModelDevice>lambdaQuery()
                .in(ModelDevice::getId, selectedDeviceIds)
                .orderByAsc(ModelDevice::getLineId)
                .orderByAsc(ModelDevice::getStationId)
                .orderByAsc(ModelDevice::getId));
        } else if (StringUtils.isNotBlank(bo.getDeviceType())) {
            Integer typeCode = ModelDevice.typeCodeOf(bo.getDeviceType());
            if (typeCode == null) {
                // 该品种在 metro_device 中无对应设备类型，无可比对设备
                devices = List.of();
            } else {
                devices = deviceMapper.selectList(Wrappers.<ModelDevice>lambdaQuery()
                    .eq(ModelDevice::getDeviceType, typeCode)
                    .orderByAsc(ModelDevice::getLineId)
                    .orderByAsc(ModelDevice::getStationId)
                    .orderByAsc(ModelDevice::getId));
            }
        } else {
            throw new ServiceException("请选择要参与比对的设备或设备品种");
        }
        if (devices.isEmpty()) {
            throw new ServiceException("所选范围内没有可参与比对的设备");
        }
        List<String> deviceIds = devices.stream().map(ModelDevice::getId).toList();

        // 3. 取各模型对各设备的最新一次历史结果（按创建时间倒序取首个）
        List<ModelResult> results = resultMapper.selectList(Wrappers.<ModelResult>lambdaQuery()
            .in(ModelResult::getModelId, modelIds)
            .in(ModelResult::getDeviceId, deviceIds)
            .orderByDesc(ModelResult::getCreateTime)
            .orderByDesc(ModelResult::getId));
        Map<String, ModelResult> latestMap = new LinkedHashMap<>();
        for (ModelResult result : results) {
            latestMap.putIfAbsent(result.getModelId() + "::" + result.getDeviceId(), result);
        }

        // 4. 按前端传入顺序整理参与比对的模型名称
        Map<String, String> modelNameMap = new LinkedHashMap<>();
        for (ModelDefinitionVo definition : definitionMapper.selectVoList(Wrappers.<ModelDefinition>lambdaQuery()
            .in(ModelDefinition::getId, modelIds))) {
            modelNameMap.put(definition.getId(), definition.getModelName());
        }
        List<String> resolvedModelIds = modelIds.stream().filter(modelNameMap::containsKey).toList();

        // 5. 组装逐设备逐模型明细，并统计健康等级一致性
        List<ModelComparisonExecuteVo.ComparisonCell> cells = new ArrayList<>();
        Map<String, Set<String>> deviceLevelMap = new LinkedHashMap<>();
        for (ModelDevice device : devices) {
            for (String modelId : resolvedModelIds) {
                ModelResult result = latestMap.get(modelId + "::" + device.getId());
                if (result == null) {
                    continue;
                }
                ModelComparisonExecuteVo.ComparisonCell cell = new ModelComparisonExecuteVo.ComparisonCell();
                cell.setDeviceId(device.getId());
                cell.setDeviceName(device.getDeviceName());
                cell.setModelId(modelId);
                cell.setHealthScore(result.getHealthScore());
                cell.setHealthLevel(result.getHealthLevel());
                cell.setDimBody(result.getDimBody());
                cell.setDimCondition(result.getDimCondition());
                cell.setDimFault(result.getDimFault());
                cell.setDimMaintain(result.getDimMaintain());
                cell.setDimEnv(result.getDimEnv());
                cells.add(cell);
                deviceLevelMap.computeIfAbsent(device.getId(), key -> new LinkedHashSet<>())
                    .add(result.getHealthLevel());
            }
        }

        int total = devices.size();
        int consistent = 0;
        for (Set<String> levels : deviceLevelMap.values()) {
            if (levels.size() <= 1) {
                consistent++;
            }
        }
        int divergent = total - consistent;

        // 6. 落库比对记录与明细
        Date now = new Date();
        ModelComparisonRecord record = new ModelComparisonRecord();
        record.setUserId(LoginHelper.isLogin() ? String.valueOf(LoginHelper.getUserId()) : null);
        record.setDeviceIds(String.join(",", deviceIds));
        record.setModelIds(String.join(",", resolvedModelIds));
        record.setDeviceType(bo.getDeviceType());
        record.setStatus("成功");
        record.setExecutedAt(now);
        record.setCompletedAt(now);
        Map<String, Object> summaryMap = new LinkedHashMap<>();
        summaryMap.put("total", total);
        summaryMap.put("consistent", consistent);
        summaryMap.put("divergent", divergent);
        summaryMap.put("modelCount", resolvedModelIds.size());
        record.setResultSummary(JsonUtils.toJsonString(summaryMap));
        baseMapper.insert(record);

        for (ModelComparisonExecuteVo.ComparisonCell cell : cells) {
            ModelComparisonDetail detail = new ModelComparisonDetail();
            detail.setRecordId(record.getId());
            detail.setDeviceId(cell.getDeviceId());
            detail.setModelId(cell.getModelId());
            detail.setHealthScore(cell.getHealthScore());
            detail.setHealthLevel(cell.getHealthLevel());
            Map<String, Object> dimensionScores = new LinkedHashMap<>();
            dimensionScores.put("dimBody", cell.getDimBody());
            dimensionScores.put("dimCondition", cell.getDimCondition());
            dimensionScores.put("dimFault", cell.getDimFault());
            dimensionScores.put("dimMaintain", cell.getDimMaintain());
            dimensionScores.put("dimEnv", cell.getDimEnv());
            detail.setDimensionScores(JsonUtils.toJsonString(dimensionScores));
            comparisonDetailMapper.insert(detail);
        }

        // 7. 组装返回结果
        ModelComparisonExecuteVo vo = new ModelComparisonExecuteVo();
        vo.setRecordId(record.getId());
        vo.setDeviceType(bo.getDeviceType());

        List<ModelComparisonExecuteVo.ModelBrief> modelBriefs = new ArrayList<>();
        for (String modelId : resolvedModelIds) {
            ModelComparisonExecuteVo.ModelBrief brief = new ModelComparisonExecuteVo.ModelBrief();
            brief.setModelId(modelId);
            brief.setModelName(modelNameMap.get(modelId));
            modelBriefs.add(brief);
        }
        vo.setModels(modelBriefs);

        List<ModelComparisonExecuteVo.DeviceBrief> deviceBriefs = new ArrayList<>();
        for (ModelDevice device : devices) {
            ModelComparisonExecuteVo.DeviceBrief brief = new ModelComparisonExecuteVo.DeviceBrief();
            brief.setDeviceId(device.getId());
            brief.setDeviceName(device.getDeviceName());
            brief.setLineId(device.getLineId());
            brief.setStationId(device.getStationId());
            deviceBriefs.add(brief);
        }
        vo.setDevices(deviceBriefs);
        vo.setDetails(cells);

        ModelComparisonExecuteVo.Summary summary = new ModelComparisonExecuteVo.Summary();
        summary.setTotal(total);
        summary.setConsistent(consistent);
        summary.setDivergent(divergent);
        vo.setSummary(summary);
        return vo;
    }
}
