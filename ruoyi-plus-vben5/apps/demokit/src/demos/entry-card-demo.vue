<script setup lang="ts">
import { PlatformEntryCard, PlatformSection } from '@st/platform-ui';

const quickEntries = [
  { icon: 'lucide:corner-down-left', iconBackground: '#10b981', id: '3d', label: '三维数字化' },
  { icon: 'lucide:users', iconBackground: '#6366f1', id: 'meeting', label: '会议管理' },
  { icon: 'lucide:settings-2', iconBackground: '#f59e0b', id: 'log', label: '运行日志' },
] as const;

const appEntries = [
  { icon: 'lucide:activity', iconColor: '#16a34a', id: 'run', label: '运行管理' },
  { icon: 'lucide:ticket', iconColor: '#84cc16', id: 'ticket', label: '两票管理' },
  { icon: 'lucide:users', iconColor: '#f59e0b', id: 'team', label: '班组管理' },
] as const;
</script>

<template>
  <div class="demo-flow">
    <PlatformSection title="快捷入口卡">
      <div class="demo-entry-grid demo-entry-grid--shortcut">
        <PlatformEntryCard
          v-for="item in quickEntries"
          :key="item.id"
          :icon="item.icon"
          :icon-background="item.iconBackground"
          :label="item.label"
          variant="shortcut"
        />
      </div>
    </PlatformSection>

    <PlatformSection title="应用入口卡">
      <div class="demo-entry-grid demo-entry-grid--app">
        <PlatformEntryCard
          v-for="item in appEntries"
          :key="item.id"
          :icon="item.icon"
          :icon-color="item.iconColor"
          :label="item.label"
          variant="app"
        />
      </div>
    </PlatformSection>
  </div>
</template>

<style scoped>
.demo-entry-grid {
  display: grid;
  gap: 16px;
}

.demo-entry-grid--shortcut {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.demo-entry-grid--app {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}
</style>
