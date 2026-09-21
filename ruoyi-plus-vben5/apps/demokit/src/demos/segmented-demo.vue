<script setup lang="ts">
import { ref } from 'vue';

import { PlatformSegmented } from '@st/platform-ui';

const currentView = ref('list');
</script>

<template>
  <div class="demo-surface demo-flow">
    <h3 class="demo-title">项目信息视图切换</h3>
    <p class="demo-muted">更适合同级内容模式切换，例如列表 / 看板 / 日历。</p>
    <PlatformSegmented
      v-model:value="currentView"
      :options="[
        { label: '列表', value: 'list' },
        { label: '看板', value: 'board' },
        { label: '日历', value: 'calendar' },
      ]"
    />
    <p class="demo-muted">当前视图：{{ currentView }}</p>
  </div>
</template>
