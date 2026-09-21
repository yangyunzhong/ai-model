<script setup lang="ts">
import type { PlatformNoticeListItem } from '@st/platform-ui';

import { PlatformNoticeItem, PlatformNoticeList, PlatformSection, PlatformStatCard } from '@st/platform-ui';

const items: PlatformNoticeListItem[] = [
  {
    actionText: '查看',
    description: '华东数据治理专项进入风险复核阶段',
    id: 1,
    meta: '10:32',
    status: 'warning',
    tag: '风险',
    title: '项目风险待处理',
  },
  {
    actionText: '处理',
    description: '采购合同付款节点已到期，请确认付款状态',
    id: 2,
    meta: '09:18',
    status: 'danger',
    tag: '超时',
    title: '付款节点超时',
  },
  {
    actionText: '打开',
    description: '质量缺陷复核流程已通过',
    id: 3,
    meta: '昨天',
    status: 'success',
    tag: '通过',
    title: '审批流程更新',
  },
];

const statCards = [
  { title: '临期资质', value: '6', trendText: '较昨日 +2', trendType: 'up' },
  { title: '待提醒事项', value: '12', trendText: '本周新增 4 条', trendType: 'up' },
  { title: '已完成提醒', value: '28', trendText: '处理率 93%', trendType: 'up' },
] as const;

const props = defineProps<{
  demoId?: string;
}>();
</script>

<template>
  <div class="demo-flow">
    <div v-if="props.demoId === 'platform-notice-item'" class="demo-surface demo-flow">
      <p class="demo-muted">NoticeItem 是单条消息卡片，适合嵌在抽屉、卡片或侧边提醒区里。</p>
      <ul class="demo-list-reset">
        <PlatformNoticeItem :item="items[0]!" />
      </ul>
    </div>

    <div v-if="props.demoId === 'platform-notice-list'" class="demo-surface demo-flow">
      <div class="demo-stat-grid demo-stat-grid--three">
        <PlatformStatCard v-for="card in statCards" :key="card.title" v-bind="card" />
      </div>

      <PlatformSection title="资质到期预警">
        <PlatformNoticeList :items="items" />
      </PlatformSection>

      <PlatformSection title="工作台消息列表">
        <PlatformNoticeList :items="items" variant="workbench" />
      </PlatformSection>
    </div>
  </div>
</template>
