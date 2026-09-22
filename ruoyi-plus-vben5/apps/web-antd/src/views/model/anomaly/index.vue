<script setup lang="ts">
import type { TableProps } from 'antdv-next';
import type { EChartsOption } from 'echarts';

import type { AnomalyFilter } from '../data';

import { computed, onMounted, onUnmounted, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

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

import {
  anomalyDeviceTypeOptions,
  anomalyStatusMeta,
  getAnomalyAllEvents,
  getAnomalyAnalysis,
  getAnomalyDeviceOptions,
  getAnomalyLineOptions,
  getAnomalyStationOptions,
} from '../data';

const refreshInterval = 30;

const filter = ref<AnomalyFilter>({
  device: '',
  line: '',
  station: '',
  type: '',
});
const tick = ref(0);
const refreshedAt = ref('');

function currentClock() {
  return new Date().toLocaleTimeString('zh-CN', { hour12: false });
}

const lineOptions = computed(() => getAnomalyLineOptions());
const stationOptions = computed(() =>
  getAnomalyStationOptions(filter.value.line),
);
const deviceOptions = computed(() =>
  getAnomalyDeviceOptions({
    line: filter.value.line,
    station: filter.value.station,
    type: filter.value.type,
  }),
);

watch(
  () => filter.value.line,
  () => {
    filter.value.station = '';
    filter.value.device = '';
  },
);
watch(
  () => filter.value.station,
  () => {
    filter.value.device = '';
  },
);
watch(
  () => filter.value.type,
  () => {
    filter.value.device = '';
  },
);

const analysis = computed(() => getAnomalyAnalysis(filter.value, tick.value));

function handleReset() {
  filter.value = { device: '', line: '', station: '', type: '' };
  tick.value = 0;
  refreshedAt.value = currentClock();
}

function handleRefresh() {
  tick.value += 1;
  refreshedAt.value = currentClock();
}

let refreshTimer: ReturnType<typeof setInterval> | undefined;

onMounted(() => {
  refreshedAt.value = currentClock();
  refreshTimer = setInterval(handleRefresh, refreshInterval * 1000);
});

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
});

// ==================== 电流 / 电压趋势 ====================
const trendChartOption = computed<EChartsOption>(() => {
  const points = analysis.value.trend;
  const times = points.map((point) => point.time);
  const spikeIndex = Math.min(
    Math.max(analysis.value.anomalyIndex, 0),
    points.length - 1,
  );
  const spike = points[spikeIndex];
  // 用单点折线系列标注异常点，避免引入未注册的 scatter / markPoint 模块
  const spikeSeriesData: Array<null | number> = points.map(() => null);
  if (spike) {
    spikeSeriesData[spikeIndex] = spike.current;
  }

  return {
    grid: { bottom: 8, containLabel: true, left: 8, right: 8, top: 52 },
    legend: { data: ['电流', '电压'], right: 0, top: 0 },
    series: [
      {
        data: points.map((point) => point.current),
        itemStyle: { color: '#ef4444' },
        lineStyle: { width: 2 },
        name: '电流',
        smooth: true,
        type: 'line',
        yAxisIndex: 0,
      },
      {
        data: points.map((point) => point.voltage),
        itemStyle: { color: '#3b82f6' },
        lineStyle: { width: 2 },
        name: '电压',
        smooth: true,
        type: 'line',
        yAxisIndex: 1,
      },
      {
        data: spikeSeriesData,
        itemStyle: { color: '#ef4444' },
        label: {
          color: '#ef4444',
          fontSize: 12,
          formatter: `异常波动 +${analysis.value.anomalyPercent}%`,
          position: 'top',
          show: true,
        },
        lineStyle: { width: 0 },
        name: '异常点',
        symbol: 'circle',
        symbolSize: 14,
        type: 'line',
        yAxisIndex: 0,
      },
    ],
    tooltip: { trigger: 'axis' },
    xAxis: {
      axisLabel: { interval: 4 },
      boundaryGap: false,
      data: times,
      type: 'category',
    },
    yAxis: [
      { name: '电流(A)', scale: true, type: 'value' },
      { name: '电压(V)', scale: true, type: 'value' },
    ],
  };
});

// ==================== 异常事件记录 ====================
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

const allEventsOpen = ref(false);
const allFilter = ref({ deviceName: '', line: '', station: '' });
const allDateRange = ref<string[]>([]);

const allStationOptions = computed(() =>
  getAnomalyStationOptions(allFilter.value.line),
);

const allEventsRaw = computed(() =>
  getAnomalyAllEvents({
    device: '',
    line: allFilter.value.line,
    station: allFilter.value.station,
    type: '',
  }),
);

const allEvents = computed(() => {
  const startDate = allDateRange.value[0] ?? '';
  const endDate = allDateRange.value[1] ?? '';
  const keyword = allFilter.value.deviceName.trim();

  return allEventsRaw.value.filter((item) => {
    if (keyword && !item.deviceName.includes(keyword)) {
      return false;
    }

    const date = item.time.slice(0, 10);

    if (startDate && date < startDate) {
      return false;
    }
    if (endDate && date > endDate) {
      return false;
    }

    return true;
  });
});

function handleAllReset() {
  allFilter.value = { deviceName: '', line: '', station: '' };
  allDateRange.value = [];
}
</script>

<template>
  <Page>
    <div class="model-anomaly-page">
      <PlatformViewToolbar
        description="扶梯 / 电梯异物卡滞实时监测，电流电压异常自动预警，数据每 30 秒自动刷新"
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
        :description="`对象：${analysis.chartLabel} · 峰值偏差 +${analysis.anomalyPercent}%`"
        :option="trendChartOption"
        title="异物卡滞事件 — 电流/电压异常检测"
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
          :data-source="analysis.events"
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
                :label="anomalyStatusMeta[param.status].label"
                :status="anomalyStatusMeta[param.status].status"
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
        <ul class="model-anomaly-rules">
          <li>电流波动超过 <strong>20%</strong>：自动触发异物卡滞疑似告警。</li>
          <li>振动突增超过 <strong>30%</strong>：判定为机械异物卡滞风险。</li>
          <li>电流与振动<strong>同时异常</strong>：升级为紧急告警，推送运维工单。</li>
          <li>综合历史趋势，平均可提前 <strong>15 分钟</strong>预警异物卡滞事件。</li>
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
            @change="allFilter.station = ''"
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
