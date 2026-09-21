<script setup lang="ts">
import { ref } from 'vue';

import { PlatformSection, PlatformStatCard, PlatformTaskCard, PlatformViewToolbar } from '@st/platform-ui';

const view = ref('overview');

const statCards = [
  { title: '活跃项目', value: '38', trendText: '本周新增 6 个', trendType: 'up' },
  { title: '高风险事项', value: '5', trendText: '较昨日 -1', trendType: 'down' },
  { title: '待审批流程', value: '14', trendText: '今日新增 3 条', trendType: 'up' },
] as const;
</script>

<template>
  <div class="demo-flow">
    <PlatformViewToolbar
      v-model:view-value="view"
      title="项目总览"
      description="集中查看项目进度、风险和关键里程碑。"
      :actions="[
        { key: 'export', label: '导出' },
        { key: 'create', label: '新建项目', type: 'primary' },
      ]"
      :view-options="[
        { label: '总览', value: 'overview' },
        { label: '风险', value: 'risk' },
        { label: '进度', value: 'progress' },
      ]"
    />

    <div class="demo-stat-grid demo-stat-grid--three">
      <PlatformStatCard v-for="card in statCards" :key="card.title" v-bind="card" />
    </div>

    <PlatformSection title="重点任务">
      <PlatformTaskCard
        title="质量缺陷复核"
        description="华东数据治理专项"
        :progress="72"
        progress-label="72%"
      />
    </PlatformSection>
  </div>
</template>
