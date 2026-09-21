<script setup lang="ts">
import type { EchartsUIType } from '@st/platform-adapter/echarts';

import type { ContractPaymentNode } from '#/mock/modules/project/contract';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@st/platform-adapter/echarts';

import { paymentNodeStatusMap } from '#/mock/modules/project/contract';

const props = defineProps<{
  nodes: ContractPaymentNode[];
}>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

onMounted(() => {
  renderChart();
});

watch(
  () => props.nodes,
  () => {
    renderChart();
  },
  { deep: true },
);

function formatAmount(value: number) {
  return `${value.toLocaleString()}万`;
}

function getSegmentBorderRadius(index: number) {
  if (index === 0) {
    return [8, 0, 0, 8];
  }
  if (index === props.nodes.length - 1) {
    return [0, 8, 8, 0];
  }
  return 0;
}

function renderChart() {
  renderEcharts({
    animationDuration: 400,
    grid: {
      bottom: 6,
      left: 0,
      right: 0,
      top: 6,
    },
    series: props.nodes.map((node, index) => ({
      barWidth: 16,
      data: [
        {
          amount: node.amount,
          nodeLabel: node.label,
          nodeStatus: paymentNodeStatusMap[node.status].label,
          percent: node.percent,
          value: node.percent,
        },
      ],
      emphasis: {
        focus: 'series' as const,
      },
      itemStyle: {
        borderRadius: getSegmentBorderRadius(index),
        color: paymentNodeStatusMap[node.status].color,
      },
      name: node.label,
      stack: 'payment',
      type: 'bar',
    })),
    tooltip: {
      borderColor: '#dbe3ea',
      formatter(params: unknown) {
        const data = (params as { data?: Record<string, unknown> }).data;
        const label = data?.nodeLabel || '';
        const percent = data?.percent || '';
        const status = data?.nodeStatus || '';
        const amount =
          typeof data?.amount === 'number' ? formatAmount(data.amount) : '';

        return `${label}（${percent}%）<br/>${status}：${amount}`;
      },
      trigger: 'item',
    },
    xAxis: {
      axisLabel: { show: false },
      axisLine: { show: false },
      axisTick: { show: false },
      max: 100,
      min: 0,
      splitLine: { show: false },
      type: 'value',
    },
    yAxis: {
      axisLabel: { show: false },
      axisLine: { show: false },
      axisTick: { show: false },
      data: ['付款节点'],
      splitLine: { show: false },
      type: 'category',
    },
  });
}
</script>

<template>
  <div class="contract-payment-mini-bar">
    <ul class="contract-payment-mini-bar__amounts">
      <li v-for="node in nodes" :key="`${node.label}-amount`">
        <strong>{{ formatAmount(node.amount) }}</strong>
      </li>
    </ul>

    <EchartsUI ref="chartRef" height="34px" width="100%" />

    <ul class="contract-payment-mini-bar__nodes">
      <li v-for="node in nodes" :key="node.label">
        <div class="contract-payment-mini-bar__title">
          <span
            class="contract-payment-mini-bar__dot"
            :style="{ backgroundColor: paymentNodeStatusMap[node.status].color }"
          ></span>
          <strong>{{ node.label }} {{ node.percent }}%</strong>
        </div>
        <span>{{ paymentNodeStatusMap[node.status].label }}</span>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.contract-payment-mini-bar {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.contract-payment-mini-bar__amounts,
.contract-payment-mini-bar__nodes {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  padding: 0;
  margin: 0;
  list-style: none;
}

.contract-payment-mini-bar__amounts {
  gap: 0;
}

.contract-payment-mini-bar__amounts li,
.contract-payment-mini-bar__nodes li {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.contract-payment-mini-bar__amounts li {
  align-items: center;
  justify-content: center;
}

.contract-payment-mini-bar__amounts strong {
  min-width: 0;
  color: hsl(var(--foreground));
  font-size: 18px;
  font-weight: 700;
  line-height: 26px;
  text-align: center;
}

.contract-payment-mini-bar__nodes {
  gap: 10px;
  margin-top: 12px;
}

.contract-payment-mini-bar__nodes li {
  gap: 4px;
}

.contract-payment-mini-bar__title {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.contract-payment-mini-bar__dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  flex: none;
}

.contract-payment-mini-bar__nodes strong {
  min-width: 0;
  overflow-wrap: anywhere;
  color: hsl(var(--foreground));
  font-weight: 700;
}

.contract-payment-mini-bar__nodes span {
  min-width: 0;
  color: hsl(var(--muted-foreground));
}

@media (max-width: 720px) {
  .contract-payment-mini-bar__amounts,
  .contract-payment-mini-bar__nodes {
    grid-template-columns: 1fr;
  }
}
</style>
