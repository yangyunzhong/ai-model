<script setup lang="ts">
import { PlatformStatusTag } from '@st/platform-ui';
</script>

<template>
  <div class="demo-flow">
    <div class="demo-surface demo-flow">
      <div class="demo-row">
        <PlatformStatusTag label="未开始" status="default" />
        <PlatformStatusTag label="进行中" status="processing" />
        <PlatformStatusTag label="已通过" status="success" />
        <PlatformStatusTag label="需关注" status="warning" />
        <PlatformStatusTag label="已驳回" status="error" />
      </div>
    </div>

    <div class="demo-surface demo-flow">
      <div class="demo-row">
        <PlatformStatusTag label="待审批" status="processing" variant="dot" />
        <PlatformStatusTag label="已完成" status="success" variant="dot" />
        <PlatformStatusTag label="超时" status="error" variant="dot" />
      </div>
    </div>
  </div>
</template>
