<script setup lang="ts">
import { computed, useSlots } from 'vue';

import { Tree } from 'antdv-next';
defineOptions({
  inheritAttrs: false,
});
const slots = useSlots();
const passthroughSlotNames = computed(() =>
  Object.keys(slots).filter((name) => name !== 'switcherIcon'),
);

function isLeafNode(slotProps: any) {
  if (slotProps?.isLeaf || slotProps?.dataRef?.isLeaf || slotProps?.data?.isLeaf) {
    return true;
  }

  const children = slotProps?.dataRef?.children ?? slotProps?.data?.children;
  return !children || children.length === 0;
}

function isExpanded(slotProps: any) {
  return Boolean(slotProps?.expanded);
}
</script>

<template>
  <Tree v-bind="$attrs" class="platform-tree">
    <template #switcherIcon="slotProps">
      <slot name="switcherIcon" v-bind="slotProps || {}">
        <span
          v-if="!isLeafNode(slotProps)"
          class="platform-tree__switcher iconfont icon-qiehuan"
          :class="{ 'is-expanded': isExpanded(slotProps) }"
          aria-hidden="true"
        ></span>
      </slot>
    </template>
    <template v-for="name in passthroughSlotNames" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Tree>
</template>

<style scoped>
.platform-tree {
  padding-block: 4px;
}

.platform-tree :deep(.ant-tree-treenode) {
  min-height: 32px;
  align-items: center;
}

.platform-tree :deep(.ant-tree-indent-unit) {
  width: 20px;
}

.platform-tree :deep(.ant-tree-node-content-wrapper) {
  min-height: 32px;
  display: inline-flex;
  align-items: center;
  padding: 8px 4px;
  border-radius: 4px;
  color: hsl(var(--foreground));
  font-size: 14px;
  line-height: 18px;
  transition:
    color 0.16s ease,
    background-color 0.16s ease;
}

.platform-tree :deep(.ant-tree-switcher) {
  display: inline-flex;
  width: 28px;
  min-width: 28px;
  height: 28px;
  align-items: center;
  justify-content: center;
  align-self: center;
  border-radius: 6px;
  flex: 0 0 28px;
  background: transparent !important;
}

.platform-tree :deep(.ant-tree-switcher::before) {
  transform: translate(1px, 1px);
}

.platform-tree__switcher {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  color: hsl(var(--st-color-tree-switcher));
  font-size: 18px;
  line-height: 1;
  transition: color 0.16s ease;
}

.platform-tree__switcher::before {
  display: inline-block;
  transform: rotate(0deg);
  transition: transform 0.16s ease;
}

.platform-tree :deep(.ant-tree-node-content-wrapper:hover) {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 10%);
}

.platform-tree :deep(.ant-tree-node-content-wrapper.ant-tree-node-selected) {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 10%);
}

.platform-tree :deep(.ant-tree-node-content-wrapper:hover),
.platform-tree :deep(.ant-tree-node-content-wrapper:hover .ant-tree-title),
.platform-tree :deep(.ant-tree-node-content-wrapper.ant-tree-node-selected),
.platform-tree :deep(.ant-tree-node-content-wrapper.ant-tree-node-selected .ant-tree-title) {
  color: hsl(var(--primary)) !important;
}

.platform-tree :deep(.ant-tree-node-content-wrapper.ant-tree-node-selected .ant-tree-title),
.platform-tree :deep(.ant-tree-node-content-wrapper:hover .ant-tree-title) {
  color: inherit;
}

.platform-tree__switcher.is-expanded::before {
  transform: rotate(90deg);
}
</style>
