<script setup lang="ts">
import type { PlatformFileListItem } from './types';

import { computed } from 'vue';

import { Empty, Skeleton } from 'antdv-next';

import PlatformFileItem from './platform-file-item.vue';

const props = withDefaults(
  defineProps<{
    columns?: 1 | 2 | 3 | 4;
    downloadingId?: null | number | string;
    emptyText?: string;
    items: PlatformFileListItem[];
    loading?: boolean;
  }>(),
  {
    columns: 1,
    downloadingId: null,
    emptyText: '暂无文件',
    loading: false,
  },
);

const emit = defineEmits<{
  download: [item: PlatformFileListItem];
}>();

const listStyle = computed(() => ({
  '--platform-file-list-columns': String(props.columns),
}));
</script>

<template>
  <div class="platform-file-list">
    <div
      v-if="loading"
      class="platform-file-list__loading"
      :style="listStyle"
    >
      <Skeleton
        v-for="index in 6"
        :key="index"
        active
        class="platform-file-list__skeleton"
        :paragraph="{ rows: 1 }"
      />
    </div>

    <Empty v-else-if="items.length === 0" :description="emptyText" />

    <ul v-else class="platform-file-list__items" :style="listStyle">
      <PlatformFileItem
        v-for="item in items"
        :key="item.id"
        :downloading="downloadingId === item.id"
        :item="item"
        @download="emit('download', $event)"
      />
    </ul>
  </div>
</template>

<style scoped>
.platform-file-list {
  min-width: 0;
}

.platform-file-list__items {
  display: grid;
  grid-template-columns: repeat(var(--platform-file-list-columns), minmax(0, 1fr));
  gap: 20px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.platform-file-list__loading {
  display: grid;
  grid-template-columns: repeat(var(--platform-file-list-columns), minmax(0, 1fr));
  gap: 20px;
}

.platform-file-list__skeleton {
  min-height: 62px;
  padding: 10px 12px;
  border: 1px solid hsl(var(--st-color-table-outline));
  border-radius: var(--st-radius-control);
}

@media (max-width: 900px) {
  .platform-file-list__items,
  .platform-file-list__loading {
    grid-template-columns: 1fr;
  }
}
</style>
