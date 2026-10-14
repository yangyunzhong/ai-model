package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.enums.FormatsType;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.ModelDevice;
import org.dromara.model.domain.bo.ModelAnomalyEventBo;
import org.dromara.model.domain.vo.ModelAnomalyAnalysisVo;
import org.dromara.model.domain.vo.ModelAnomalyEventVo;
import org.dromara.model.domain.ModelAnomalyEvent;
import org.dromara.model.mapper.ModelAnomalyEventMapper;
import org.dromara.model.mapper.ModelDeviceMapper;
import org.dromara.model.service.IModelAnomalyEventService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final ModelDeviceMapper deviceMapper;

    /** 趋势曲线监测点数量 */
    private static final int TREND_POINT_COUNT = 30;

    /** 温度正常范围下限系数（相对上限） */
    private static final BigDecimal TEMP_LOW_FACTOR = new BigDecimal("0.45");

    /** 关注状态阈值系数（达到上限的 90% 转为关注） */
    private static final BigDecimal WARN_FACTOR = new BigDecimal("0.9");

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

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

    /**
     * 异物检测分析聚合查询
     *
     * <p>装配内容：目标设备电流/电压趋势曲线（最近 30 个监测点）、实时监测参数面板
     * （电流/电压/振动/温度，状态按阈值判定）、异物检测规则配置、电流峰值偏差指标。</p>
     *
     * @param bo 过滤条件（设备ID/设备类型/线路/车站）
     * @return 分析结果
     */
    @Override
    public ModelAnomalyAnalysisVo analysis(ModelAnomalyEventBo bo) {
        ModelAnomalyAnalysisVo vo = new ModelAnomalyAnalysisVo();

        // 1. 解析设备范围并确定趋势展示设备
        List<ModelDevice> devices = resolveDevices(bo);
        ModelDevice target = null;
        if (StringUtils.isNotBlank(bo.getDeviceId())) {
            target = devices.stream()
                .filter(device -> bo.getDeviceId().equals(device.getId()))
                .findFirst().orElse(null);
        } else if (!devices.isEmpty()) {
            target = devices.get(0);
        }

        // 2. 趋势曲线：目标设备最近 30 个监测点（电流真实值，电压按 380V 基准合成）
        List<ModelAnomalyAnalysisVo.TrendPoint> trend = new ArrayList<>();
        if (target != null) {
            vo.setDeviceName(target.getDeviceName());
            vo.setDeviceId(target.getId());
            List<Map<String, Object>> series = baseMapper.selectRealtimeSeries(target.getId(), TREND_POINT_COUNT);
            Collections.reverse(series);
            BigDecimal baseCurrent = null;
            int peakIndex = -1;
            BigDecimal peakCurrent = BigDecimal.ZERO;
            for (int i = 0; i < series.size(); i++) {
                Map<String, Object> row = series.get(i);
                ModelAnomalyAnalysisVo.TrendPoint point = new ModelAnomalyAnalysisVo.TrendPoint();
                Object monitorTime = row.get("monitor_time");
                point.setTime(monitorTime instanceof Date date
                    ? DateUtils.parseDateToStr(FormatsType.HH_MM_SS, date) : String.valueOf(monitorTime));
                BigDecimal current = toBigDecimal(row.get("working_current"));
                point.setCurrent(current);
                trend.add(point);
                if (current != null) {
                    if (baseCurrent == null) {
                        baseCurrent = current;
                    }
                    if (current.compareTo(peakCurrent) > 0) {
                        peakCurrent = current;
                        peakIndex = i;
                    }
                }
            }
            vo.setTrend(trend);
            List<ModelAnomalyAnalysisVo.ForeignRule> rules = buildForeignRules();
            if (baseCurrent != null && baseCurrent.compareTo(BigDecimal.ZERO) > 0) {
                vo.setAnomalyPercent(peakCurrent.subtract(baseCurrent)
                    .multiply(HUNDRED).divide(baseCurrent, 1, RoundingMode.HALF_UP));
            } else {
                vo.setAnomalyPercent(BigDecimal.ZERO);
            }
            // 只有电流瞬时波动超过设备类型规则的阈值（如扶梯 >20%）才视为异常，否则不标注异常点
            if (!isAnomalyExceeded(vo.getAnomalyPercent(), rules, target.getDeviceType())) {
                vo.setAnomalyIndex(-1);
                vo.setAnomalyPercent(BigDecimal.ZERO);
            } else {
                vo.setAnomalyIndex(peakIndex);
            }
            vo.setMonitorParams(buildMonitorParams(target, series, rules));
        } else {
            vo.setTrend(trend);
            vo.setAnomalyIndex(-1);
            vo.setAnomalyPercent(BigDecimal.ZERO);
            vo.setMonitorParams(new ArrayList<>());
        }

        // 3. 异物检测规则配置
        vo.setRules(buildForeignRules());

        return vo;
    }

    /**
     * 判断电流峰值波动是否超过设备类型对应的异物规则阈值（percent 为百分比，如 25 表示 25%）
     */
    private boolean isAnomalyExceeded(BigDecimal percent, List<ModelAnomalyAnalysisVo.ForeignRule> rules, Integer deviceType) {
        if (percent == null || percent.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        for (ModelAnomalyAnalysisVo.ForeignRule rule : rules) {
            if (deviceType != null && deviceType.equals(rule.getDeviceType())) {
                // 该类型未配置电流阈值时不限制
                return rule.getCurrentThreshold() == null || percent.compareTo(rule.getCurrentThreshold()) > 0;
            }
        }
        return true;
    }

    /**
     * 按过滤条件解析设备范围
     */
    private List<ModelDevice> resolveDevices(ModelAnomalyEventBo bo) {
        LambdaQueryWrapper<ModelDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceId()), ModelDevice::getId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getLineId()), ModelDevice::getLineId, bo.getLineId());
        lqw.eq(StringUtils.isNotBlank(bo.getStationId()), ModelDevice::getStationId, bo.getStationId());
        if (StringUtils.isNotBlank(bo.getDeviceType())) {
            Integer typeCode = ModelDevice.typeCodeOf(bo.getDeviceType());
            lqw.eq(typeCode != null, ModelDevice::getDeviceType, typeCode);
        }
        lqw.orderByAsc(ModelDevice::getId);
        return deviceMapper.selectList(lqw);
    }

    /**
     * 构建实时监测参数面板：电流 / 振动 / 温度
     *
     * <p>电流不设绝对值正常范围，按异物规则监测瞬时波动（相对序列基线的波动百分比）；
     * 振动、温度仍按绝对值上限判定。</p>
     */
    private List<ModelAnomalyAnalysisVo.MonitorParam> buildMonitorParams(ModelDevice device,
        List<Map<String, Object>> series, List<ModelAnomalyAnalysisVo.ForeignRule> rules) {
        Map<String, BigDecimal> thresholds = new HashMap<>();
        for (Map<String, Object> row : baseMapper.selectDeviceThresholds(device.getId())) {
            BigDecimal value = toBigDecimal(row.get("item_value"));
            if (value != null && row.get("item_name") != null) {
                thresholds.put(String.valueOf(row.get("item_name")), value);
            }
        }
        boolean elevator = Integer.valueOf(2).equals(device.getDeviceType());
        BigDecimal vibrationLimit = thresholds.getOrDefault("振动值上限", elevator ? BigDecimal.valueOf(2.2) : BigDecimal.valueOf(3.0));
        BigDecimal tempLimit = thresholds.getOrDefault("电机温度上限", elevator ? BigDecimal.valueOf(75) : BigDecimal.valueOf(80));
        BigDecimal tempLow = tempLimit.multiply(TEMP_LOW_FACTOR).setScale(0, RoundingMode.HALF_UP);

        Map<String, Object> latest = series == null || series.isEmpty() ? null : series.get(series.size() - 1);
        BigDecimal current = latest == null ? null : toBigDecimal(latest.get("working_current"));
        BigDecimal vibration = latest == null ? null : toBigDecimal(latest.get("real_vibration"));
        BigDecimal temp = latest == null ? null : toBigDecimal(latest.get("real_motor_temp"));

        // 电流瞬时波动：最新值相对序列首点（基线）的波动百分比
        BigDecimal baseCurrent = series == null || series.isEmpty() ? null : toBigDecimal(series.get(0).get("working_current"));
        BigDecimal volatility = null;
        if (current != null && baseCurrent != null && baseCurrent.compareTo(BigDecimal.ZERO) > 0) {
            volatility = current.subtract(baseCurrent).multiply(HUNDRED).divide(baseCurrent, 1, RoundingMode.HALF_UP);
        }
        BigDecimal currentThreshold = currentThresholdOf(rules, device.getDeviceType());

        List<ModelAnomalyAnalysisVo.MonitorParam> params = new ArrayList<>();

        ModelAnomalyAnalysisVo.MonitorParam currentParam = new ModelAnomalyAnalysisVo.MonitorParam();
        currentParam.setName("电流");
        currentParam.setValue(current == null ? "--" : current.stripTrailingZeros().toPlainString() + "A");
        currentParam.setRange(currentThreshold == null
            ? "--" : "波动≤" + currentThreshold.stripTrailingZeros().toPlainString() + "%");
        currentParam.setStatus(judgeVolatility(volatility, currentThreshold));
        params.add(currentParam);

        ModelAnomalyAnalysisVo.MonitorParam vibrationParam = new ModelAnomalyAnalysisVo.MonitorParam();
        vibrationParam.setName("振动");
        vibrationParam.setValue(vibration == null ? "--" : vibration.stripTrailingZeros().toPlainString() + "mm/s");
        vibrationParam.setRange("0-" + vibrationLimit.stripTrailingZeros().toPlainString() + "mm/s");
        vibrationParam.setStatus(judgeStatus(vibration, vibrationLimit));
        params.add(vibrationParam);

        ModelAnomalyAnalysisVo.MonitorParam tempParam = new ModelAnomalyAnalysisVo.MonitorParam();
        tempParam.setName("温度");
        tempParam.setValue(temp == null ? "--" : temp.stripTrailingZeros().toPlainString() + "℃");
        tempParam.setRange(tempLow.toPlainString() + "-" + tempLimit.stripTrailingZeros().toPlainString() + "℃");
        tempParam.setStatus(judgeStatus(temp, tempLimit));
        params.add(tempParam);

        return params;
    }

    /**
     * 取设备类型对应的异物规则电流波动阈值（%）
     */
    private BigDecimal currentThresholdOf(List<ModelAnomalyAnalysisVo.ForeignRule> rules, Integer deviceType) {
        for (ModelAnomalyAnalysisVo.ForeignRule rule : rules) {
            if (deviceType != null && deviceType.equals(rule.getDeviceType())) {
                return rule.getCurrentThreshold();
            }
        }
        return null;
    }

    /**
     * 按波动阈值判定电流状态：波动超过阈值为异常，达到阈值 90% 为关注，否则正常
     */
    private String judgeVolatility(BigDecimal volatility, BigDecimal threshold) {
        if (volatility == null || threshold == null || threshold.compareTo(BigDecimal.ZERO) <= 0) {
            return "正常";
        }
        if (volatility.compareTo(threshold) > 0) {
            return "异常";
        }
        return volatility.compareTo(threshold.multiply(WARN_FACTOR)) >= 0 ? "关注" : "正常";
    }

    /**
     * 按阈值判定监测状态：达到上限为异常，达到 90% 为关注，否则正常
     */
    private String judgeStatus(BigDecimal value, BigDecimal limit) {
        if (value == null || limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return "正常";
        }
        if (value.compareTo(limit) >= 0) {
            return "异常";
        }
        return value.compareTo(limit.multiply(WARN_FACTOR)) >= 0 ? "关注" : "正常";
    }

    /**
     * 构建异物检测规则配置（extra_config 中提取提前预警分钟数）
     */
    private List<ModelAnomalyAnalysisVo.ForeignRule> buildForeignRules() {
        List<ModelAnomalyAnalysisVo.ForeignRule> rules = new ArrayList<>();
        for (Map<String, Object> row : baseMapper.selectForeignObjectRules()) {
            ModelAnomalyAnalysisVo.ForeignRule rule = new ModelAnomalyAnalysisVo.ForeignRule();
            Object type = row.get("device_type");
            rule.setDeviceType(type instanceof Number number ? number.intValue() : null);
            rule.setCurrentThreshold(toBigDecimal(row.get("current_threshold")));
            rule.setVibrationThreshold(toBigDecimal(row.get("vibration_threshold")));
            rule.setAlertLevel(row.get("current_vibration_alert") == null ? null : String.valueOf(row.get("current_vibration_alert")));
            rule.setAdvanceMinutes(parseAdvanceMinutes(row.get("extra_config")));
            rules.add(rule);
        }
        return rules;
    }

    /**
     * 从 extra_config JSON 中解析提前预警分钟数
     */
    private Integer parseAdvanceMinutes(Object extraConfig) {
        if (extraConfig == null) {
            return null;
        }
        try {
            String json = String.valueOf(extraConfig);
            int keyIndex = json.indexOf("advanceMinutes");
            if (keyIndex < 0) {
                return null;
            }
            int colonIndex = json.indexOf(':', keyIndex);
            int end = colonIndex + 1;
            while (end < json.length() && (Character.isDigit(json.charAt(end)))) {
                end++;
            }
            return Integer.parseInt(json.substring(colonIndex + 1, end));
        } catch (Exception e) {
            log.warn("解析异物规则 extra_config 失败: {}", extraConfig, e);
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
