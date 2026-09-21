<script setup lang="ts">
import type { DescriptionsProps } from 'antdv-next';

import { Descriptions } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});

withDefaults(
  defineProps<{
    bordered?: boolean;
    colon?: boolean;
    column?: DescriptionsProps['column'];
    items?: DescriptionsProps['items'];
    size?: DescriptionsProps['size'];
  }>(),
  {
    bordered: true,
    colon: false,
    column: 1,
    items: () => [],
    size: 'middle',
  },
);
</script>

<template>
  <Descriptions
    v-bind="$attrs"
    :bordered="bordered"
    class="platform-descriptions"
    :colon="colon"
    :column="column"
    :items="items"
    :size="size"
  >
    <template v-if="$slots.title" #title>
      <slot name="title"></slot>
    </template>
    <template v-if="$slots.extra" #extra>
      <slot name="extra"></slot>
    </template>
  </Descriptions>
</template>

<style scoped>
.platform-descriptions :deep(.ant-descriptions-header) {
  margin-bottom: 16px;
}

.platform-descriptions :deep(.ant-descriptions-view) {
  overflow: hidden;
  border-radius: var(--st-radius-card);
  border-color: hsl(var(--st-color-border-subtle));
}

.platform-descriptions :deep(.ant-descriptions-row > th),
.platform-descriptions :deep(.ant-descriptions-row > td) {
  padding: 14px 18px;
  border-color: hsl(var(--st-color-border-subtle));
}

.platform-descriptions :deep(.ant-descriptions-item-label) {
  width: 112px;
  color: hsl(var(--muted-foreground));
  font-weight: 400;
  background: hsl(var(--background));
  white-space: nowrap;
}

.platform-descriptions :deep(.ant-descriptions-item-content) {
  color: hsl(var(--foreground));
  font-weight: 600;
}
</style>
