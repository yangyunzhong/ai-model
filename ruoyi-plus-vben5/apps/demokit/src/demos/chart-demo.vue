<script setup lang="ts">
const props = defineProps<{
  demoId?: string;
}>();

const chartKindMap = {
  'platform-chart-bar': {
    description: '适合分类对比、阶段统计和多项目横向比较。',
    title: '柱状图',
  },
  'platform-chart-line': {
    description: '适合趋势变化、时间序列和连续数据观察。',
    title: '折线图',
  },
  'platform-chart-pie': {
    description: '适合占比结构、分类构成和单时点分布。',
    title: '饼图',
  },
  'platform-chart-ring': {
    description: '适合强调中间指标或总量说明的环形结构图。',
    title: '玉环图',
  },
  'platform-chart-rose': {
    description: '适合层级占比和视觉强调较强的分类展示。',
    title: '玫瑰图',
  },
} as const;

const currentChart =
  (props.demoId && chartKindMap[props.demoId as keyof typeof chartKindMap]) || chartKindMap['platform-chart-bar'];
</script>

<template>
  <div class="demo-flow">
    <div class="demo-surface demo-flow">
      <h3 class="demo-title">当前平台已封装：PlatformEchartsPanel</h3>
      <p class="demo-muted">
        目前平台层已经有统一图表容器，负责标题、描述、白底模块和 ECharts 挂载。当前页面作为“{{ currentChart.title }}”占位入口，后续封装完成后直接在这里接入真实图表内容。
      </p>
    </div>

    <article class="demo-surface demo-chart-card">
      <div class="demo-chart-card__preview">
        <span>待接入{{ currentChart.title }}</span>
      </div>
      <div class="demo-flow">
        <h3 class="demo-title">{{ currentChart.title }}</h3>
        <p class="demo-muted">{{ currentChart.description }}</p>
      </div>
    </article>
  </div>
</template>
