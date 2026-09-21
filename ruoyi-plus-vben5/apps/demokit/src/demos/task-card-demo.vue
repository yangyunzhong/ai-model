<script setup lang="ts">
import { PlatformTaskCard } from '@st/platform-ui';
</script>

<template>
  <div class="demo-card-grid">
    <PlatformTaskCard
      action-label="进入"
      description="华东数据治理专项"
      meta="负责人：林青 · 更新于 10:32"
      :progress="72"
      progress-label="72%"
      :tags="[
        { label: '进行中', status: 'processing' },
        { label: '高优先级', status: 'warning' },
      ]"
      title="质量缺陷复核"
    />
    <PlatformTaskCard
      action-label="查看"
      description="设备资产盘点"
      meta="负责人：周沐 · 今天截止"
      :progress="38"
      progress-label="38%"
      :tags="[{ label: '待处理', status: 'error' }]"
      title="资产变更审批"
    />
  </div>
</template>
