<script setup lang="ts">
import { PlatformStatCard } from '@st/platform-ui';
</script>

<template>
  <div class="demo-stat-grid">
    <PlatformStatCard
      title="本月收入"
      trend-text="+12.4% 较上月"
      trend-type="up"
      type="primary"
      value="￥2.48M"
    />
    <PlatformStatCard
      title="活跃项目"
      trend-text="+6 本周新增"
      trend-type="up"
      type="success"
      value="38"
    />
    <PlatformStatCard
      title="超时任务"
      trend-text="-3 较昨日"
      trend-type="down"
      type="warning"
      value="7"
    />
    <PlatformStatCard
      title="高风险项"
      description="待复核"
      type="danger"
      value="2"
    />
  </div>
</template>
