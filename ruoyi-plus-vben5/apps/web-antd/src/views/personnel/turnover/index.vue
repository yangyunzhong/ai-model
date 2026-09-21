<script setup lang="ts">
import type { EChartsOption } from 'echarts';

import type {
  ContractorTurnoverRate,
  PersonnelTurnoverStatCard,
} from '#/mock/modules/personnel/turnover';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformEchartsPanel,
  PlatformStatCard,
  PlatformViewToolbar,
} from '@st/platform-ui';

import {
  getContractorTurnoverRates,
  getPersonnelTurnoverStats,
} from '#/mock/modules/personnel/turnover';

const loading = ref(false);
const statCards = ref<PersonnelTurnoverStatCard[]>([]);
const turnoverRows = ref<ContractorTurnoverRate[]>([]);

const rankedTurnoverRows = computed(() =>
  turnoverRows.value
    .toSorted((left, right) => {
      if (right.lossRate !== left.lossRate) {
        return right.lossRate - left.lossRate;
      }

      if (right.lostCount !== left.lostCount) {
        return right.lostCount - left.lostCount;
      }

      return right.totalCount - left.totalCount;
    })
    .map((item, index) => ({
      ...item,
      rank: index + 1,
    })),
);

const turnoverChartHeight = computed(
  () => `${Math.max(400, rankedTurnoverRows.value.length * 44 + 92)}px`,
);

function formatTurnoverRateLabel(row?: ContractorTurnoverRate) {
  if (!row) {
    return '';
  }

  return `${row.lossRate.toFixed(1)}%（${row.lostCount}/${row.totalCount}）`;
}

const turnoverChartOption = computed<EChartsOption>(() => {
  const rows = rankedTurnoverRows.value;
  const maxRate = Math.max(35, ...rows.map((item) => item.lossRate));

  return {
    animationDuration: 600,
    animationEasing: 'cubicOut',
    grid: {
      bottom: 8,
      containLabel: true,
      left: 16,
      right: 168,
      top: 18,
    },
    series: [
      {
        backgroundStyle: {
          borderRadius: 999,
          color: '#edf2f7',
        },
        barMaxWidth: 18,
        data: rows.map((item) => ({
          itemStyle: {
            color:
              item.lossRate > 0
                ? {
                    colorStops: [
                      {
                        color: '#fb7185',
                        offset: 0,
                      },
                      {
                        color: '#f97316',
                        offset: 1,
                      },
                    ],
                    type: 'linear',
                    x: 0,
                    x2: 1,
                    y: 0,
                    y2: 0,
                  }
                : '#d7deea',
            shadowBlur: item.lossRate > 0 ? 10 : 0,
            shadowColor: item.lossRate > 0 ? 'rgba(249, 115, 22, 0.18)' : 'transparent',
          },
          value: item.lossRate,
        })),
        itemStyle: {
          borderRadius: 999,
        },
        showBackground: true,
        z: 3,
        type: 'bar',
      },
    ],
    tooltip: {
      axisPointer: {
        shadowStyle: {
          color: 'rgba(148, 163, 184, 0.08)',
        },
        type: 'shadow',
      },
      confine: true,
      formatter(params: unknown) {
        const firstParam = Array.isArray(params) ? params[0] : params;
        const dataIndex = (firstParam as { dataIndex?: number }).dataIndex ?? 0;
        const row = rows[dataIndex];

        if (!row) {
          return '';
        }

        return [
          row.contractor,
          `流失率：${row.lossRate.toFixed(1)}%`,
          `离职人数：${row.lostCount}`,
          `总人数：${row.totalCount}`,
        ].join('<br/>');
      },
      position(pos, _params, _dom, _rect, size) {
        const [x = 0, y = 0] = pos as number[];
        const { contentSize, viewSize } = size;

        return [
          Math.max(12, Math.min(x - contentSize[0] - 20, viewSize[0] - contentSize[0] - 12)),
          Math.max(12, Math.min(y - contentSize[1] / 2, viewSize[1] - contentSize[1] - 12)),
        ];
      },
      trigger: 'axis',
    },
    xAxis: {
      axisLabel: {
        show: false,
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      max: Math.max(40, maxRate + 8),
      splitLine: {
        show: false,
      },
      type: 'value',
    },
    yAxis: [
      {
        axisLabel: {
          color: '#0f172a',
          fontSize: 13,
          formatter: (_value: string, index: number) => {
            const row = rows[index];

            if (!row) {
              return '';
            }

            return `{rank|${String(row.rank).padStart(2, '0')}} {name|${row.contractor}}`;
          },
          margin: 20,
          rich: {
            name: {
              color: '#0f172a',
              fontSize: 13,
              fontWeight: 600,
            },
            rank: {
              color: '#64748b',
              fontSize: 12,
              fontWeight: 700,
              padding: [0, 6, 0, 0],
            },
          },
        },
        axisLine: {
          show: false,
        },
        axisTick: {
          show: false,
        },
        data: rows.map((item) => item.contractor),
        inverse: true,
        type: 'category',
      },
      {
        axisLabel: {
          align: 'left',
          color: '#64748b',
          fontSize: 12,
          fontWeight: 600,
          formatter: (_value: string, index: number) => {
            return formatTurnoverRateLabel(rows[index]);
          },
          margin: 24,
          width: 144,
        },
        axisLine: {
          show: false,
        },
        axisTick: {
          show: false,
        },
        data: rows.map((item) => item.contractor),
        inverse: true,
        position: 'right',
        type: 'category',
      },
    ],
  };
});

onMounted(loadPersonnelTurnover);

async function loadPersonnelTurnover() {
  loading.value = true;
  try {
    const [cards, contractorRates] = await Promise.all([
      getPersonnelTurnoverStats(),
      getContractorTurnoverRates(),
    ]);

    statCards.value = cards;
    turnoverRows.value = contractorRates;
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="personnel-turnover-page">
      <PlatformViewToolbar
        description="入职 / 离职 / 调动全流程追踪"
        title="变动与流失率统计"
      />

      <section class="personnel-turnover-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :loading="loading"
          v-bind="card"
        />
      </section>

      <PlatformEchartsPanel
        class="personnel-turnover-chart"
        :height="turnoverChartHeight"
        :option="turnoverChartOption"
        title="各承包商流失率"
      />
    </div>
  </Page>
</template>

<style scoped>
.personnel-turnover-page {
  display: flex;
  min-height: 100%;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
}

.personnel-turnover-stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.personnel-turnover-chart {
  min-height: 0;
}

@media (max-width: 1200px) {
  .personnel-turnover-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .personnel-turnover-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
