<script setup lang="ts">
import type { SegmentedProps } from 'antdv-next';

import { Segmented } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});

defineProps<{
  options: SegmentedProps['options'];
}>();

const value = defineModel<number | string>('value');
</script>

<template>
  <Segmented
    v-bind="$attrs"
    v-model:value="value"
    class="platform-segmented"
    :options="options"
  >
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Segmented>
</template>

<style scoped>
.platform-segmented {
  display: block;
  width: fit-content;
  margin-inline: auto;
  --ant-segmented-item-selected-bg: hsl(var(--primary));
  box-shadow: none !important;
}

.platform-segmented :deep(.ant-segmented-item-input) {
  inset: 0;
}

.platform-segmented :deep(.ant-segmented-item-icon + *) {
  margin-inline-start: 0 !important;
}

.platform-segmented :deep(.ant-segmented-item) {
  cursor: pointer;
  color: hsl(var(--muted-foreground));
  font-size: 14px;
  font-weight: 500;
  transition: color 0.16s ease;
}

.platform-segmented :deep(.ant-segmented-item-label) {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  padding: 0 20px;
  line-height: 32px;
  font-size: 14px;
  transition: color 0.16s ease;
}

.platform-segmented :deep(.ant-segmented-thumb) {
  background: hsl(var(--primary)) !important;
  box-shadow: none !important;
}

.platform-segmented :deep(.ant-segmented-item-selected) {
  color: hsl(var(--primary-foreground)) !important;
  background: hsl(var(--primary)) !important;
}

.platform-segmented
  :deep(.ant-segmented-item-selected .ant-segmented-item-label) {
  color: hsl(var(--primary-foreground)) !important;
}

.platform-segmented
  :deep(.ant-segmented-item-selected .ant-segmented-item-label *) {
  color: hsl(var(--primary-foreground)) !important;
}

.platform-segmented
  :deep(.ant-segmented-item:not(.ant-segmented-item-selected):hover),
.platform-segmented
  :deep(.ant-segmented-item:not(.ant-segmented-item-selected):focus),
.platform-segmented
  :deep(.ant-segmented-item:not(.ant-segmented-item-selected):hover .ant-segmented-item-label) {
  color: hsl(var(--primary)) !important;
}

.platform-segmented
  :deep(.ant-segmented-item:not(.ant-segmented-item-selected):hover .ant-segmented-item-label *) {
  color: hsl(var(--primary)) !important;
}
</style>
