<script setup lang="ts">
import type { EchartsUIType } from '@st/platform-adapter/echarts';

import type { ProjectProgressRecord } from '#/mock/modules/project/progress';

import { computed, onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@st/platform-adapter/echarts';
import { PlatformSectionTitle } from '@st/platform-ui';

const props = withDefaults(
  defineProps<{
    rows: ProjectProgressRecord[];
    year?: number;
  }>(),
  {
    year: new Date().getFullYear(),
  },
);

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

const chartHeight = computed(() => {
  return `${Math.max(320, props.rows.length * 38 + 96)}px`;
});

onMounted(() => {
  renderChart();
});

watch(
  () => props.rows,
  () => {
    renderChart();
  },
  { deep: true },
);

function renderChart() {
  const categories = props.rows.map((row) => row.name);
  const offsetData = props.rows.map((row) => row.startMonth - 1);
  const durationData = props.rows.map((row) => ({
    itemStyle: {
      borderRadius: 4,
      color: row.color,
    },
    progress: row.progress,
    value: row.endMonth - row.startMonth + 1,
  }));
  const progressMarkers = props.rows
    .filter((row) => row.progress > 0 && row.progress < 100)
    .map((row) => ({
      itemStyle: { color: '#f59e0b' },
      label: {
        color: '#ffffff',
        distance: 2,
        fontSize: 10,
        fontWeight: 700,
        formatter: `${row.progress}%`,
        position: 'inside' as const,
        show: true as const,
      },
      name: row.name,
      progress: row.progress,
      value: [
        row.startMonth -
          1 +
          ((row.endMonth - row.startMonth + 1) * row.progress) / 100,
        row.name,
      ] as [number, string],
    }));
  const milestoneMarkers = props.rows.flatMap((row) =>
    row.milestones.map((milestone) => ({
      itemStyle: { color: '#f59e0b' },
      milestone: milestone.label,
      name: row.name,
      value: [milestone.month - 1, row.name] as [number, string],
    })),
  );

  renderEcharts({
    animationDuration: 450,
    grid: {
      bottom: 32,
      containLabel: true,
      left: 8,
      right: 18,
      top: 42,
    },
    series: [
      {
        barWidth: 16,
        data: offsetData,
        emphasis: { disabled: true },
        itemStyle: { color: 'transparent' },
        stack: 'timeline',
        type: 'bar',
      },
      {
        barWidth: 16,
        data: durationData,
        label: {
          color: '#ffffff',
          fontSize: 11,
          fontWeight: 700,
          formatter(params: { data?: unknown }) {
            const progress = (params.data as null | { progress?: unknown })
              ?.progress;
            return typeof progress === 'number' ? `${progress}%` : '';
          },
          position: 'inside' as const,
          show: true,
        },
        stack: 'timeline',
        type: 'bar',
      },
      {
        data: milestoneMarkers,
        lineStyle: { opacity: 0 },
        showSymbol: true,
        symbol: 'diamond',
        symbolSize: 14,
        type: 'line',
      },
      {
        data: progressMarkers,
        lineStyle: { opacity: 0 },
        showSymbol: true,
        symbol: 'diamond',
        symbolSize: 16,
        type: 'line',
      },
    ],
    tooltip: {
      borderColor: '#dbe3ea',
      formatter(params: unknown) {
        const item = Array.isArray(params) ? params[0] : params;
        const data = (item as { data?: Record<string, unknown> }).data;
        const name = (item as { name?: string }).name;
        const progress = data?.progress;
        const milestone = data?.milestone;

        if (milestone) {
          return `${name}<br/>节点：${milestone}`;
        }

        if (typeof progress === 'number') {
          return `${name}<br/>当前进度：${progress}%`;
        }

        return String(name || '');
      },
      trigger: 'item',
    },
    xAxis: {
      axisLabel: {
        color: '#7f8fa6',
        formatter(value: number) {
          return value >= 0 && value < 12 ? `${value + 1}月` : '';
        },
      },
      axisLine: { show: false },
      axisTick: { show: false },
      max: 12,
      min: 0,
      splitLine: {
        lineStyle: { color: '#edf3f7' },
        show: true,
      },
      type: 'value',
    },
    yAxis: {
      axisLabel: {
        color: '#334155',
        fontSize: 13,
        margin: 18,
      },
      axisLine: { show: false },
      axisTick: { show: false },
      data: categories,
      inverse: true,
      type: 'category',
    },
  });
}
</script>

<template>
  <div class="project-progress-gantt">
    <PlatformSectionTitle class="project-progress-gantt__title-row" title="项目甘特图">
      <template #extra>{{ year }}年度</template>
    </PlatformSectionTitle>
    <div class="project-progress-gantt__month-head">
      <strong>项目名称</strong>
      <span v-for="month in 12" :key="month">{{ month }}月</span>
    </div>
    <EchartsUI ref="chartRef" :height="chartHeight" width="100%" />
  </div>
</template>

<style scoped>
.project-progress-gantt {
  overflow-x: auto;
}

.project-progress-gantt__title-row {
  margin-bottom: 20px;
}

.project-progress-gantt__month-head {
  display: grid;
  min-width: 1120px;
  height: 32px;
  align-items: center;
  grid-template-columns: 220px repeat(12, minmax(64px, 1fr));
  padding-inline: 14px;
  color: #64748b;
  background: hsl(var(--st-color-fill-selected));
  border: 1px solid hsl(var(--st-color-border-subtle));
  font-size: var(--st-font-size-sm);
}

.project-progress-gantt__month-head strong {
  color: hsl(var(--foreground));
}

.project-progress-gantt__month-head span {
  text-align: center;
}
</style>
