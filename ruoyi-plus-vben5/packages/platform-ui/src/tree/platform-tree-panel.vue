<script lang="ts">
export default {
  inheritAttrs: false,
};
</script>

<script setup lang="ts">
import { computed, useAttrs, useSlots } from 'vue';

import { Empty, Skeleton } from 'antdv-next';

import { PlatformButton } from '../button';
import { PlatformInput } from '../field';
import PlatformTree from './platform-tree.vue';

type TreeKey = number | string;

interface TreeNode {
  [key: string]: any;
  children?: TreeNode[];
}

const props = withDefaults(
  defineProps<{
    emptyDescription?: string;
    loading?: boolean;
    searchPlaceholder?: string;
    showRefresh?: boolean;
    showSearch?: boolean;
    showTitle?: boolean;
    title?: string;
    treeData?: TreeNode[];
  }>(),
  {
    emptyDescription: '无数据',
    loading: false,
    searchPlaceholder: '请输入关键词',
    showRefresh: false,
    showSearch: true,
    showTitle: false,
    title: '',
    treeData: () => [],
  },
);

const emit = defineEmits<{
  reload: [];
  search: [value: string];
  select: [keys: TreeKey[]];
}>();

const attrs = useAttrs();
const slots = useSlots();
const selectedKeys = defineModel<TreeKey[]>('selectedKeys', {
  default: () => [],
});
const searchValue = defineModel<string>('searchValue', {
  default: '',
});

const rootClass = computed(() => attrs.class);
const rootStyle = computed(() => attrs.style);
const hasTitle = computed(() =>
  Boolean(slots.title || (props.showTitle && props.title)),
);
const hasToolbar = computed(() => props.showSearch || props.showRefresh);
const hasHeader = computed(() => hasTitle.value || hasToolbar.value);
const treeSlotNames = computed(() =>
  Object.keys(slots).filter((name) => name !== 'title'),
);
const treeAttrs = computed(() => {
  const { class: _class, style: _style, ...rest } = attrs;
  return rest;
});

function handleSelect(keys: TreeKey[]) {
  emit('select', keys);
}

function handleSearch() {
  emit('search', searchValue.value);
}
</script>

<template>
  <div class="platform-tree-panel" :class="rootClass" :style="rootStyle">
    <Skeleton
      :loading="loading"
      :paragraph="{ rows: 8 }"
      active
      class="platform-tree-panel__skeleton"
    >
      <div class="platform-tree-panel__surface">
        <div v-if="hasHeader" class="platform-tree-panel__header">
          <div v-if="hasTitle" class="platform-tree-panel__title">
            <slot name="title">{{ title }}</slot>
          </div>
          <div v-if="hasToolbar" class="platform-tree-panel__toolbar">
            <PlatformInput
              v-if="showSearch"
              v-model:value="searchValue"
              allow-clear
              :placeholder="searchPlaceholder"
              @press-enter="handleSearch"
            >
              <template #suffix>
                <span
                  class="platform-tree-panel__search-icon iconfont icon-sousuo"
                  aria-hidden="true"
                  role="button"
                  tabindex="0"
                  @click="handleSearch"
                  @keydown.enter.prevent="handleSearch"
                ></span>
              </template>
            </PlatformInput>
            <PlatformButton
              v-if="showRefresh"
              aria-label="刷新树结构"
              scene="toolbar"
              shape="circle"
              @click="emit('reload')"
            >
              <template #icon>
                <span class="i-lucide-refresh-cw text-[14px]"></span>
              </template>
            </PlatformButton>
          </div>
        </div>
        <div class="platform-tree-panel__content">
          <PlatformTree
            v-if="treeData.length > 0"
            v-bind="treeAttrs"
            v-model:selected-keys="selectedKeys"
            class="platform-tree-panel__tree"
            :tree-data="treeData"
            @select="handleSelect"
          >
            <template v-for="name in treeSlotNames" #[name]="slotProps">
              <slot :name="name" v-bind="slotProps || {}"></slot>
            </template>
          </PlatformTree>
          <div v-else class="platform-tree-panel__empty">
            <Empty
              :image="Empty.PRESENTED_IMAGE_SIMPLE"
              :description="emptyDescription"
            />
          </div>
        </div>
      </div>
    </Skeleton>
  </div>
</template>

<style scoped>
.platform-tree-panel {
  min-height: 0;
}

.platform-tree-panel__surface {
  --platform-tree-panel-padding: 24px;
  display: flex;
  height: 100%;
  min-height: 0;
  flex-direction: column;
  overflow: hidden;
  border-radius: 6px;
  background: hsl(var(--background));
}

.platform-tree-panel__header {
  position: sticky;
  top: 0;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
  padding: var(--platform-tree-panel-padding);
  background: hsl(var(--background));
}

.platform-tree-panel__title {
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  font-weight: 600;
  line-height: var(--st-line-height-base);
}

.platform-tree-panel__toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.platform-tree-panel__toolbar :deep(.platform-input .ant-input-affix-wrapper) {
  min-height: 36px;
  padding-inline: 8px;
}

.platform-tree-panel__toolbar :deep(.platform-input .ant-input-affix-wrapper .ant-input) {
  padding-inline: 0;
}

.platform-tree-panel__toolbar :deep(.platform-input .ant-input::placeholder) {
  color: hsl(var(--foreground) / 40%);
}

.platform-tree-panel__toolbar :deep(.platform-input .ant-input-suffix) {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.platform-tree-panel__toolbar :deep(.platform-input .ant-input-clear-icon) {
  display: inline-flex;
  width: 20px;
  height: 20px;
  align-items: center;
  justify-content: center;
  margin: 0;
  font-size: 20px;
  line-height: 1;
}

.platform-tree-panel__content {
  min-height: 0;
  flex: 1;
  overflow: hidden;
}

.platform-tree-panel__tree {
  height: 100%;
  overflow: auto;
  padding: 0 var(--platform-tree-panel-padding)
    var(--platform-tree-panel-padding);
}

.platform-tree-panel__empty {
  padding: 20px var(--platform-tree-panel-padding)
    var(--platform-tree-panel-padding);
}

.platform-tree-panel__skeleton {
  height: 100%;
}

.platform-tree-panel__search-icon {
  display: inline-flex;
  width: 20px;
  height: 20px;
  align-items: center;
  justify-content: center;
  color: hsl(var(--foreground));
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
  transition: color 0.16s ease;
}

.platform-tree-panel__search-icon:hover,
.platform-tree-panel__search-icon:focus-visible {
  color: hsl(var(--primary));
}
</style>
