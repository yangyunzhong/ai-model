<script setup lang="ts">
import type { PlatformNoticeListItem } from './types';

import { Empty, Skeleton } from 'antdv-next';

import PlatformNoticeItem from './platform-notice-item.vue';

withDefaults(
  defineProps<{
    emptyText?: string;
    items: PlatformNoticeListItem[];
    loading?: boolean;
    variant?: 'default' | 'workbench';
  }>(),
  {
    emptyText: '暂无提醒',
    loading: false,
    variant: 'default',
  },
);

const emit = defineEmits<{
  action: [item: PlatformNoticeListItem];
}>();
</script>

<template>
  <div class="platform-notice-list">
    <div v-if="loading" class="platform-notice-list__loading">
      <Skeleton
        v-for="index in 5"
        :key="index"
        active
        :paragraph="{ rows: 1 }"
      />
    </div>
    <Empty v-else-if="items.length === 0" :description="emptyText" />
    <ul v-else class="platform-notice-list__items">
      <PlatformNoticeItem
        v-for="item in items"
        :key="item.id"
        :item="item"
        :variant="variant"
        @action="emit('action', $event)"
      />
    </ul>
  </div>
</template>

<style scoped>
.platform-notice-list {
  min-width: 0;
}

.platform-notice-list__items {
  padding: 0;
  margin: 0;
  list-style: none;
}

.platform-notice-list__loading {
  display: grid;
  gap: 10px;
  padding: 12px 16px;
}
</style>
