<script setup lang="ts">
import type { Key } from 'antdv-next/dist/table/interface';

import type { PropType } from 'vue';

import type { CategoryTree } from '#/api/workflow/category/model';

import { h, onMounted, ref } from 'vue';

import { SyncOutlined } from '@antdv-next/icons';
import { Button, InputSearch, Skeleton, Tree } from 'antdv-next';

import { categoryTree } from '#/api/workflow/category';

defineOptions({ inheritAttrs: false });

const emit = defineEmits<{
  /**
   * 点击刷新按钮的事件
   */
  reload: [];
  /**
   * 点击节点的事件
   */
  select: [keys: string[]];
}>();

const selectCode = defineModel('selectCode', {
  required: true,
  type: Array as PropType<number[] | string[]>,
});

const searchValue = defineModel('searchValue', {
  type: String,
  default: '',
});

const categoryTreeArray = ref<CategoryTree[]>([]);
/** 骨架屏加载 */
const showTreeSkeleton = ref<boolean>(true);

async function loadTree() {
  showTreeSkeleton.value = true;
  searchValue.value = '';
  selectCode.value = [];

  const treeData = await categoryTree();

  categoryTreeArray.value = treeData;
  showTreeSkeleton.value = false;
}

async function handleReload() {
  await loadTree();
  emit('reload');
}

onMounted(loadTree);

function handleSelect(keys: Key[]) {
  emit('select', keys as string[]);
}

function renderReloadButton() {
  return h(
    Button,
    {
      onClick: handleReload,
    },
    {
      default: () => h(SyncOutlined, { class: 'text-primary' }),
    },
  );
}

function renderTitle(node: any) {
  const label = String(node.label ?? '');
  const keyword = searchValue.value;

  if (!keyword || !label.includes(keyword)) {
    return label;
  }

  const index = label.indexOf(keyword);
  return h('span', [
    label.slice(0, index),
    h('span', { class: 'text-primary' }, keyword),
    label.slice(index + keyword.length),
  ]);
}
</script>

<template>
  <div :class="$attrs.class">
    <Skeleton
      :loading="showTreeSkeleton"
      :paragraph="{ rows: 8 }"
      active
      class="p-[8px]"
    >
      <div
        class="bg-background flex h-full flex-col overflow-y-auto rounded-lg"
      >
        <!-- 固定在顶部 必须加上bg-background背景色 否则会产生'穿透'效果 -->
        <div class="bg-background z-100 sticky left-0 top-0 p-[8px]">
          <InputSearch
            v-model:value="searchValue"
            :enter-button="renderReloadButton()"
            :placeholder="$t('pages.common.search')"
            size="small"
            allow-clear
          />
        </div>
        <div class="h-full overflow-x-hidden px-[8px]">
          <!-- TODO: 适配antdv-next -->
          <Tree
            v-bind="$attrs"
            v-if="categoryTreeArray.length > 0"
            v-model:selected-keys="selectCode"
            :class="$attrs.class"
            :field-names="{ title: 'label', key: 'id' }"
            :show-line="{ showLeafIcon: false }"
            :title-render="renderTitle"
            :tree-data="categoryTreeArray"
            :virtual="false"
            default-expand-all
            @select="handleSelect"
            :styles="{
              item: {
                '--ant-tree-node-selected-bg':
                  'var(--ant-color-primary-bg-hover)',
              },
            }"
          />
        </div>
      </div>
    </Skeleton>
  </div>
</template>
