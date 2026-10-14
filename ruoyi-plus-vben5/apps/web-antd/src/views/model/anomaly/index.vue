<script setup lang="ts">
import type { TableProps } from 'antdv-next';
import type { EChartsOption } from 'echarts';

import type {
  AnomalyAnalysisForeignRule,
  AnomalyEventVO,
} from '#/api/model/model';
import type { AnomalyFilter } from '../data';

import { computed, onMounted, onUnmounted, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  anomalyAnalysis,
  anomalyEventList,
  deviceList,
  lineList,
  stationList,
} from '#/api/model';
import {
  PlatformButton,
  PlatformEchartsPanel,
  PlatformInput,
  PlatformModal,
  PlatformQueryPanel,
  PlatformRangePicker,
  PlatformSection,
  PlatformSelect,
  PlatformStatusTag,
  PlatformTable,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Empty } from 'antdv-next';

import { anomalyDeviceTypeOptions } from '../data';

const refreshInterval = 30;

const filter = ref<AnomalyFilter>({
  device: '',
  line: '',
  station: '',
  type: '',
});
const refreshedAt = ref('');

function currentClock() {
  return new Date().toLocaleTimeString('zh-CN', { hour12: false });
}

// ==================== 线路 / 车站 / 设备下拉 ====================
const lineOptions = ref<{ label: string; value: string }[]>([]);
const stationOptions = ref<{ label: string; value: string }[]>([]);
const deviceOptions = ref<{ label: string; value: string }[]>([]);

/**
 * 线路下拉由 metro_line 真实配置数据提供，下拉值存线路ID，
 * 用于联动车站与后端按线路过滤查询。
 */
async function loadLineOptions() {
  try {
    const res = await lineList({ pageNum: 1, pageSize: 500 });
    lineOptions.value = (res.rows ?? []).map((line) => ({
      label: line.name,
      value: String(line.id),
    }));
  } catch {
    lineOptions.value = [];
  }
}

async function loadStationOptions(lineId: string) {
  try {
    const res = await stationList({
      pageNum: 1,
      pageSize: 1000,
      ...(lineId ? { lineId } : {}),
    });
    return (res.rows ?? []).map((station) => ({
      label: station.name,
      value: String(station.id),
    }));
  } catch {
    return [];
  }
}

async function refreshStationOptions(lineId: string) {
  stationOptions.value = await loadStationOptions(lineId);
}

let deviceOptionsSeq = 0;

/** 设备下拉由 metro_device 真实台账数据提供，按线路/车站/品种过滤 */
async function loadDeviceOptions() {
  const requestId = ++deviceOptionsSeq;
  try {
    const res = await deviceList({
      deviceType: filter.value.type || undefined,
      lineId: filter.value.line || undefined,
      pageNum: 1,
      pageSize: 500,
      stationId: filter.value.station || undefined,
    });
    if (requestId !== deviceOptionsSeq) return;
    deviceOptions.value = (res.rows ?? []).map((device) => ({
      label: `${device.deviceName}（${device.id}）`,
      value: String(device.id),
    }));
  } catch {
    if (requestId === deviceOptionsSeq) {
      deviceOptions.value = [];
    }
  }
}

watch(
  () => filter.value.line,
  (lineId) => {
    filter.value.station = '';
    filter.value.device = '';
    void refreshStationOptions(lineId);
    void loadAnalysisAndEvents();
  },
);
watch(
  () => filter.value.station,
  () => {
    filter.value.device = '';
    void loadAnalysisAndEvents();
  },
);
watch(
  () => filter.value.type,
  () => {
    filter.value.device = '';
    void loadAnalysisAndEvents();
  },
);
watch(
  () => filter.value.device,
  () => void loadAnalysisAndEvents(),
);

// ==================== 分析数据（趋势/监测面板/规则） ====================
interface AnalysisState {
  anomalyIndex: number;
  anomalyPercent: number;
  chartLabel: string;
  monitorParams: {
    name: string;
    range: string;
    status: string;
    value: string;
  }[];
  rules: AnomalyAnalysisForeignRule[];
  trend: { current: number; time: string }[];
}

type MonitorStatus = '关注' | '异常' | '正常';

const monitorStatusMeta: Record<
  MonitorStatus,
  { label: MonitorStatus; status: 'error' | 'success' | 'warning' }
> = {
  关注: { label: '关注', status: 'warning' },
  异常: { label: '异常', status: 'error' },
  正常: { label: '正常', status: 'success' },
};

function statusMeta(status?: string) {
  if (status === '关注' || status === '异常') {
    return monitorStatusMeta[status];
  }
  return monitorStatusMeta['正常'];
}

const analysis = ref<AnalysisState>({
  anomalyIndex: 0,
  anomalyPercent: 0,
  chartLabel: '--',
  monitorParams: [],
  rules: [],
  trend: [],
});

async function loadAnalysis() {
  try {
    const res = await anomalyAnalysis({
      deviceId: filter.value.device || undefined,
      deviceType: filter.value.type || undefined,
      lineId: filter.value.line || undefined,
      stationId: filter.value.station || undefined,
    });
    analysis.value = {
      anomalyIndex: res.anomalyIndex ?? 0,
      anomalyPercent: res.anomalyPercent ?? 0,
      chartLabel: res.deviceName || '全部设备',
      monitorParams: res.monitorParams ?? [],
      rules: res.rules ?? [],
      trend: res.trend ?? [],
    };
  } catch {
    // 接口异常时保留当前数据，避免自动刷新清空页面
  }
}

// ==================== 异常事件记录 ====================
interface AnomalyEventRow {
  abnormalValue: string;
  deviation: string;
  deviceName: string;
  id: string;
  line: string;
  normalValue: string;
  param: string;
  station: string;
  time: string;
}

function mapEventRow(item: AnomalyEventVO): AnomalyEventRow {
  const unit = item.unit ?? '';
  return {
    abnormalValue:
      item.abnormalValue == null ? '--' : `${item.abnormalValue}${unit}`,
    deviation:
      item.deviationPercent == null ? '--' : `+${item.deviationPercent}%`,
    deviceName: item.deviceName ?? '',
    id: String(item.id),
    line: item.lineName ?? '',
    normalValue:
      item.normalValue == null ? '--' : `${item.normalValue}${unit}`,
    param: item.paramName ?? '',
    station: item.stationName ?? '',
    time: item.eventTime ?? '',
  };
}

const events = ref<AnomalyEventRow[]>([]);

async function loadEvents() {
  try {
    const res = await anomalyEventList({
      deviceId: filter.value.device || undefined,
      deviceType: filter.value.type || undefined,
      lineId: filter.value.line || undefined,
      pageNum: 1,
      pageSize: 10,
      stationId: filter.value.station || undefined,
    });
    events.value = (res.rows ?? []).map(mapEventRow);
  } catch {
    // 接口异常时保留当前数据
  }
}

async function loadAnalysisAndEvents() {
  await Promise.all([loadAnalysis(), loadEvents()]);
}

function handleReset() {
  filter.value = { device: '', line: '', station: '', type: '' };
  refreshedAt.value = currentClock();
  void refreshStationOptions('');
  void loadDeviceOptions();
  void loadAnalysisAndEvents();
}

function handleRefresh() {
  refreshedAt.value = currentClock();
  void loadDeviceOptions();
  void loadAnalysisAndEvents();
}

let refreshTimer: ReturnType<typeof setInterval> | undefined;

onMounted(async () => {
  refreshedAt.value = currentClock();
  refreshTimer = setInterval(handleRefresh, refreshInterval * 1000);
  await loadLineOptions();
  await refreshStationOptions(filter.value.line);
  await loadDeviceOptions();
  await loadAnalysisAndEvents();
});

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
});

// ==================== 电流趋势 ====================
const trendChartOption = computed<EChartsOption>(() => {
  const points = analysis.value.trend;
  const times = points.map((point) => point.time);
  const spikeIndex = Math.min(
    Math.max(analysis.value.anomalyIndex, 0),
    points.length - 1,
  );
  const spike = points[spikeIndex];
  // 后端已按异物规则阈值（如扶梯电流波动 >20%）判定是否异常，未达标不标注异常点
  const hasAnomaly =
    analysis.value.anomalyIndex >= 0 && analysis.value.anomalyPercent > 0 && !!spike;
  // 用单点折线系列标注异常点，避免引入未注册的 scatter / markPoint 模块
  const spikeSeriesData: Array<null | number> = points.map(() => null);
  if (hasAnomaly) {
    spikeSeriesData[spikeIndex] = spike.current;
  }

  return {
    grid: { bottom: 8, containLabel: true, left: 8, right: 8, top: 52 },
    legend: { data: ['电流'], right: 0, top: 0 },
    series: [
      {
        data: points.map((point) => point.current),
        itemStyle: { color: '#ef4444' },
        lineStyle: { width: 2 },
        name: '电流',
        smooth: true,
        type: 'line',
      },
      {
        data: spikeSeriesData,
        itemStyle: { color: '#ef4444' },
        label: {
          color: '#ef4444',
          fontSize: 12,
          formatter: `异常波动 +${analysis.value.anomalyPercent}%`,
          position: 'top',
          show: hasAnomaly,
        },
        lineStyle: { width: 0 },
        name: '异常点',
        symbol: 'circle',
        symbolSize: 14,
        type: 'line',
      },
    ],
    tooltip: { trigger: 'axis' },
    xAxis: {
      axisLabel: { interval: 4 },
      boundaryGap: false,
      data: times,
      type: 'category',
    },
    yAxis: { name: '电流(A)', scale: true, type: 'value' },
  };
});

// ==================== 事件表列定义 ====================
const eventColumns: TableProps['columns'] = [
  { dataIndex: 'time', key: 'time', title: '检测时间', width: 180 },
  { dataIndex: 'deviceName', key: 'deviceName', title: '设备名称', width: 220 },
  { dataIndex: 'param', key: 'param', title: '检测参数', width: 120 },
  { dataIndex: 'normalValue', key: 'normalValue', title: '正常值', width: 140 },
  {
    dataIndex: 'abnormalValue',
    key: 'abnormalValue',
    title: '异常值',
    width: 160,
  },
  { dataIndex: 'deviation', key: 'deviation', title: '偏差', width: 100 },
];

const allEventColumns: TableProps['columns'] = [
  { dataIndex: 'time', key: 'time', title: '检测时间', width: 180 },
  { dataIndex: 'deviceName', key: 'deviceName', title: '设备名称', width: 220 },
  { dataIndex: 'line', key: 'line', title: '线路', width: 100 },
  { dataIndex: 'station', key: 'station', title: '车站', width: 120 },
  { dataIndex: 'param', key: 'param', title: '检测参数', width: 120 },
  { dataIndex: 'normalValue', key: 'normalValue', title: '正常值', width: 140 },
  {
    dataIndex: 'abnormalValue',
    key: 'abnormalValue',
    title: '异常值',
    width: 160,
  },
  { dataIndex: 'deviation', key: 'deviation', title: '偏差', width: 100 },
];

// ==================== 「全部记录」弹窗 ====================
const allEventsOpen = ref(false);
const allFilter = ref({ deviceName: '', line: '', station: '' });
const allDateRange = ref<string[]>([]);

const allStationOptions = ref<{ label: string; value: string }[]>([]);

async function refreshAllStationOptions(lineId: string) {
  allStationOptions.value = await loadStationOptions(lineId);
}

function handleAllLineChange() {
  allFilter.value.station = '';
  void refreshAllStationOptions(allFilter.value.line);
}

const allEventsRaw = ref<AnomalyEventVO[]>([]);

async function loadAllEvents() {
  try {
    const res = await anomalyEventList({
      lineId: allFilter.value.line || undefined,
      pageNum: 1,
      pageSize: 200,
      stationId: allFilter.value.station || undefined,
    });
    allEventsRaw.value = res.rows ?? [];
  } catch {
    // 接口异常时保留当前数据
  }
}

const allEvents = computed(() => {
  const startDate = allDateRange.value[0] ?? '';
  const endDate = allDateRange.value[1] ?? '';
  const keyword = allFilter.value.deviceName.trim();

  return allEventsRaw.value
    .filter((item) => {
      if (keyword && !(item.deviceName ?? '').includes(keyword)) {
        return false;
      }

      const date = (item.eventTime ?? '').slice(0, 10);

      if (startDate && date < startDate) {
        return false;
      }
      if (endDate && date > endDate) {
        return false;
      }

      return true;
    })
    .map(mapEventRow);
});

function handleAllReset() {
  allFilter.value = { deviceName: '', line: '', station: '' };
  allDateRange.value = [];
  void refreshAllStationOptions('');
  void loadAllEvents();
}

watch(allEventsOpen, (open) => {
  if (open) {
    void loadAllEvents();
    void refreshAllStationOptions(allFilter.value.line);
  }
});

watch(
  () => [allFilter.value.line, allFilter.value.station],
  () => {
    if (allEventsOpen.value) {
      void loadAllEvents();
    }
  },
);

// ==================== 预警机制 ====================
function deviceTypeLabel(type?: number) {
  if (type === 1) return '自动扶梯';
  if (type === 2) return '垂直电梯';
  return '通用设备';
}
</script>

<template>
  <Page>
    <div class="model-anomaly-page">
      <PlatformViewToolbar
        description="扶梯 / 电梯异物卡滞实时监测，电流异常自动预警，数据每 30 秒自动刷新"
        title="异物检测分析"
      >
        <template #actions>
          <span class="model-anomaly-refresh-hint">
            最近刷新 {{ refreshedAt || '--:--:--' }}
          </span>
          <PlatformButton scene="toolbar" @click="handleRefresh">
            立即刷新
          </PlatformButton>
        </template>
      </PlatformViewToolbar>

      <PlatformQueryPanel :columns="4" :show-query="false" @reset="handleReset">
        <PlatformSelect
          v-model:value="filter.line"
          :options="lineOptions"
          allow-clear
          placeholder="请选择线路"
        />
        <PlatformSelect
          v-model:value="filter.station"
          :options="stationOptions"
          allow-clear
          placeholder="请选择车站"
        />
        <PlatformSelect
          v-model:value="filter.type"
          :options="anomalyDeviceTypeOptions"
          allow-clear
          placeholder="请选择设备品种"
        />
        <PlatformSelect
          v-model:value="filter.device"
          :options="deviceOptions"
          allow-clear
          placeholder="请选择设备"
        />
      </PlatformQueryPanel>

      <PlatformEchartsPanel
        :height="'340px'"
        :description="`对象：${analysis.chartLabel} · ${analysis.anomalyPercent > 0 ? `峰值偏差 +${analysis.anomalyPercent}%` : '未见异常波动'}`"
        :option="trendChartOption"
        title="异物卡滞事件 — 电流异常检测"
      />

      <PlatformSection title="异物检测异常事件记录">
        <template #extra>
          <PlatformButton
            scene="action"
            size="small"
            type="link"
            @click="allEventsOpen = true"
          >
            更多 &gt;&gt;
          </PlatformButton>
        </template>

        <PlatformTable
          :action-column-width="0"
          :columns="eventColumns"
          :data-source="events"
          :pagination="false"
          :scroll="{ x: 920 }"
          row-key="id"
          size="small"
        >
          <template #emptyText>
            <Empty description="当前筛选条件下暂无异常事件" />
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'deviation'">
              <span class="model-anomaly-deviation">{{ record.deviation }}</span>
            </template>
          </template>
        </PlatformTable>
      </PlatformSection>

      <PlatformSection
        :description="`每 ${refreshInterval} 秒自动采集一次，超出正常范围即标记关注`"
        title="实时监测参数面板"
      >
        <div class="model-anomaly-monitor">
          <div
            v-for="param in analysis.monitorParams"
            :key="param.name"
            class="model-anomaly-monitor__item"
          >
            <div class="model-anomaly-monitor__head">
              <span class="model-anomaly-monitor__name">{{ param.name }}</span>
              <PlatformStatusTag
                :label="statusMeta(param.status).label"
                :status="statusMeta(param.status).status"
              />
            </div>
            <strong class="model-anomaly-monitor__value">{{ param.value }}</strong>
            <span class="model-anomaly-monitor__range">
              正常范围：{{ param.range }}
            </span>
          </div>
        </div>
      </PlatformSection>

      <PlatformSection title="异物检测预警机制">
        <ul v-if="analysis.rules.length > 0" class="model-anomaly-rules">
          <li v-for="rule in analysis.rules" :key="rule.deviceType">
            {{ deviceTypeLabel(rule.deviceType) }}：
            <ul class="model-anomaly-rules__detail">
              <li>
                电流瞬时波动 &gt;
                <strong>{{ rule.currentThreshold ?? '--' }}%</strong>
                → 触发异物卡滞预警
              </li>
              <li>
                振动值突增 &gt;
                <strong>{{ rule.vibrationThreshold ?? '--' }}%</strong>
                → 触发机械异物检测
              </li>
              <li>
                电流 + 振动同时异常 →
                <strong>{{ rule.alertLevel || '紧急告警' }}</strong>
                ，建议立即停机检查（可提前
                <strong>{{ rule.advanceMinutes ?? '--' }} 分钟</strong>
                预警）
              </li>
            </ul>
          </li>
        </ul>
        <ul v-else class="model-anomaly-rules">
          <li>暂未配置异物检测规则，请在异物检测规则表中维护规则数据。</li>
        </ul>
      </PlatformSection>
    </div>

    <PlatformModal
      v-model:open="allEventsOpen"
      :footer="null"
      destroy-on-close
      title="异物检测异常事件全部记录"
      width="1100px"
    >
      <div class="model-anomaly-all">
        <div class="model-anomaly-all__filters">
          <PlatformRangePicker
            v-model:value="allDateRange"
            :placeholder="['开始日期', '结束日期']"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
          <PlatformSelect
            v-model:value="allFilter.line"
            :options="lineOptions"
            allow-clear
            placeholder="线路"
            @change="handleAllLineChange"
          />
          <PlatformSelect
            v-model:value="allFilter.station"
            :options="allStationOptions"
            allow-clear
            placeholder="车站"
          />
          <PlatformInput
            v-model:value="allFilter.deviceName"
            allow-clear
            placeholder="设备名称"
          />
          <PlatformButton scene="toolbar" @click="handleAllReset">
            重置
          </PlatformButton>
        </div>

        <PlatformTable
          :action-column-width="0"
          :columns="allEventColumns"
          :data-source="allEvents"
          :scroll="{ x: 1140 }"
          row-key="id"
          size="small"
        >
          <template #emptyText>
            <Empty description="没有符合条件的异常记录" />
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'deviation'">
              <span class="model-anomaly-deviation">{{ record.deviation }}</span>
            </template>
          </template>
        </PlatformTable>
      </div>
    </PlatformModal>
  </Page>
</template>

<style scoped>
.model-anomaly-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.model-anomaly-refresh-hint {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-anomaly-deviation {
  font-weight: 600;
  color: hsl(var(--destructive));
}

.model-anomaly-monitor {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.model-anomaly-monitor__item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px 16px;
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-card);
}

.model-anomaly-monitor__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.model-anomaly-monitor__name {
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}

.model-anomaly-monitor__value {
  font-size: 22px;
  font-weight: 700;
  color: hsl(var(--foreground));
}

.model-anomaly-monitor__range {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-anomaly-rules {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 0;
  padding-left: 20px;
  font-size: 13px;
  color: hsl(var(--foreground));
}

.model-anomaly-rules__detail {
  margin: 6px 0 0;
  padding-left: 18px;
  list-style: disc;
}

.model-anomaly-rules__detail li + li {
  margin-top: 4px;
}

.model-anomaly-rules strong {
  color: hsl(var(--primary));
}

.model-anomaly-all {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
}

.model-anomaly-all__filters {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  align-items: center;
}
</style>
