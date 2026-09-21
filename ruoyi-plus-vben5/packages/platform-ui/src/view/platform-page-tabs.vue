<script setup lang="ts">
import type { TabDefinition, TabsStyleType } from '../../../@core/base/typings/src';

import { computed } from 'vue';

import { TabsView } from '../../../@core/ui-kit/tabs-ui/src';

const props = withDefaults(
  defineProps<{
    active?: string;
    showIcon?: boolean;
    styleType?: TabsStyleType;
    tabs?: TabDefinition[];
  }>(),
  {
    active: '',
    showIcon: false,
    styleType: 'chrome',
    tabs: undefined,
  },
);

const emit = defineEmits<{
  close: [key: string];
  unpin: [tab: TabDefinition];
  'update:active': [key: string];
}>();

const currentTabs = computed(() => {
  return props.tabs || [];
});

function handleClick(key: string) {
  emit('update:active', key);
}

function handleClose(key: string) {
  emit('close', key);
}

function handleUnpin(tab: TabDefinition) {
  emit('unpin', tab);
}
</script>

<template>
  <div
    v-if="currentTabs.length > 0"
    class="platform-page-tabs"
  >
    <TabsView
      :active="active"
      content-class="platform-page-tabs__content"
      :show-icon="showIcon"
      show-disabled-close-icon
      :style-type="styleType"
      :tabs="currentTabs"
      @close="handleClose"
      @unpin="handleUnpin"
      @update:active="handleClick"
    />
  </div>
</template>

<style scoped>
.platform-page-tabs {
  display: flex;
  align-items: center;
  min-width: 0;
  min-height: 40px;
  padding: 0;
  background: hsl(var(--card));
}

.platform-page-tabs :deep(.platform-page-tabs__content) {
  min-height: 32px;
}

.platform-page-tabs :deep(.vben-scrollbar.left-shadow),
.platform-page-tabs :deep(.vben-scrollbar.right-shadow),
.platform-page-tabs :deep(.vben-scrollbar.both-shadow) {
  -webkit-mask-image: none;
  mask-image: none;
}

.platform-page-tabs :deep(.tabs-chrome) {
  align-items: center;
  gap: 8px;
  padding-right: 0;
}

.platform-page-tabs :deep(.tabs-chrome__item) {
  margin-right: 0;
}

.platform-page-tabs :deep(.tabs-chrome__background-before),
.platform-page-tabs :deep(.tabs-chrome__background-after) {
  display: none;
}

.platform-page-tabs :deep(.tabs-chrome__divider) {
  display: block;
  left: 0;
  width: 1px;
  height: 14px;
  opacity: 1;
  background: hsl(var(--st-color-text-secondary) / 28%);
}

.platform-page-tabs :deep(.tabs-chrome__background) {
  padding: 0;
}

.platform-page-tabs :deep(.tabs-chrome__background-content) {
  border: 1px solid transparent;
  border-radius: 6px 6px 0 0;
  background: hsl(var(--card));
  box-shadow: none;
}

.platform-page-tabs :deep(.tabs-chrome__item-main) {
  height: 32px;
  min-height: 32px;
  margin: 0;
  padding: 0 24px 0 16px;
  color: hsl(var(--st-color-text-secondary));
  font-size: 13px;
  font-weight: 500;
}

.platform-page-tabs :deep(.tabs-chrome__item-main > span) {
  font-size: 13px;
  line-height: 1;
}

.platform-page-tabs :deep(.tabs-chrome__item:hover:not(.is-active) .tabs-chrome__background-content) {
  background: hsl(var(--card));
  border-color: transparent;
}

.platform-page-tabs :deep(.tabs-chrome__item:hover:not(.is-active) .tabs-chrome__divider),
.platform-page-tabs :deep(.tabs-chrome__item:hover:not(.is-active) + .tabs-chrome__item .tabs-chrome__divider),
.platform-page-tabs :deep(.tabs-chrome__item.is-active + .tabs-chrome__item .tabs-chrome__divider) {
  opacity: 1 !important;
}

.platform-page-tabs :deep(.tabs-chrome__item:hover:not(.is-active) .tabs-chrome__item-main) {
  color: hsl(var(--st-color-brand));
}

.platform-page-tabs :deep(.tabs-chrome__item.is-active .tabs-chrome__background-content) {
  background: hsl(var(--st-color-fill-selected));
  border-color: transparent;
  box-shadow: none;
}

.platform-page-tabs :deep(.tabs-chrome__item.is-active .tabs-chrome__item-main) {
  color: hsl(var(--st-color-brand));
  font-weight: 700;
}

.platform-page-tabs :deep(.tabs-chrome__item.is-active .tabs-chrome__extra .lucide-x) {
  color: hsl(var(--st-color-brand));
  stroke: hsl(var(--st-color-brand));
}

.platform-page-tabs :deep(.tabs-chrome__extra) {
  right: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  cursor: pointer;
}

.platform-page-tabs :deep(.tabs-chrome__extra .lucide-x) {
  margin-top: 0;
  width: 14px;
  height: 14px;
  color: hsl(var(--st-color-text-secondary));
  stroke: hsl(var(--st-color-text-secondary));
  cursor: pointer;
  border-radius: 0;
  background: transparent;
  transition: transform 0.16s ease;
}

.platform-page-tabs :deep(.tabs-chrome__extra .lucide-x:hover) {
  background: transparent;
  color: hsl(var(--st-color-text-secondary));
  stroke: hsl(var(--st-color-text-secondary));
  transform: scale(1.2);
}

.platform-page-tabs :deep(.tabs-chrome__close-icon--disabled:hover) {
  background: transparent;
  transform: none;
}
</style>
