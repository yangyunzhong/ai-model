<script setup lang="ts">
import type { EchartsUIType } from '@st/platform-adapter/echarts';
import type { EChartsOption } from 'echarts';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@st/platform-adapter/echarts';

import { PlatformSection } from '../view';

const props = withDefaults(
  defineProps<{
    description?: string;
    height?: string;
    option: EChartsOption;
    title?: string;
  }>(),
  {
    description: '',
    height: '360px',
    title: '',
  },
);

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

onMounted(() => {
  renderEcharts(props.option);
});

watch(
  () => props.option,
  (option) => {
    renderEcharts(option);
  },
  { deep: true },
);
</script>

<template>
  <PlatformSection :description="description" :title="title">
    <EchartsUI ref="chartRef" :height="height" width="100%" />
  </PlatformSection>
</template>
