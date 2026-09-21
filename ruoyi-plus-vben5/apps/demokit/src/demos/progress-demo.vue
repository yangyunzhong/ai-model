<script setup lang="ts">
import { PlatformProgress } from '@st/platform-ui';
</script>

<template>
  <div class="demo-surface">
    <div class="demo-progress-list">
      <PlatformProgress label="项目进度" :value="72" show-value />
      <PlatformProgress label="质量整改" color="hsl(var(--warning))" :value="48" show-value />
      <PlatformProgress label="资料归档" color="hsl(var(--success))" :value="92" show-value />
    </div>
  </div>
</template>
