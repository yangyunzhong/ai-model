<script setup lang="ts">
import type { PropType } from 'vue';

import type { DeptTree } from '#/api/system/user/model';

import { computed, onMounted, ref } from 'vue';

import { cloneDeep, listToTree, treeToList } from '@vben/utils';

import { PlatformTreePanel } from '@st/platform-ui';

import { getDeptTree } from '#/api/system/user';

defineOptions({ inheritAttrs: false });

const props = withDefaults(defineProps<Props>(), {
  showSearch: true,
  api: getDeptTree,
});

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

interface Props {
  /**
   * 调用的接口
   */
  api?: () => Promise<DeptTree[]>;
  /**
   * 是否显示搜索框
   */
  showSearch?: boolean;
}

const selectDeptId = defineModel('selectDeptId', {
  required: true,
  type: Array as PropType<string[]>,
});

const searchValue = defineModel('searchValue', {
  type: String,
  default: '',
});

/** 部门数据源 */
type DeptTreeArray = DeptTree[];
const deptTreeArray = ref<DeptTreeArray>([]);
/** 骨架屏加载 */
const showTreeSkeleton = ref<boolean>(true);

async function loadTree() {
  showTreeSkeleton.value = true;
  searchValue.value = '';
  selectDeptId.value = [];

  const ret = await props.api();

  deptTreeArray.value = ret;
  showTreeSkeleton.value = false;
}

const deptTreeComputed = computed(() => {
  if (!searchValue.value) {
    return deptTreeArray.value;
  }
  const toTree: DeptTreeArray = treeToList(cloneDeep(deptTreeArray.value), {
    id: 'id',
    pid: 'parentId',
  });
  toTree.forEach((i) => (i.children = []));
  const filteredTree = toTree.filter((item: DeptTree) =>
    item.label.toUpperCase().includes(searchValue.value.toUpperCase()),
  );
  return listToTree(filteredTree, {
    id: 'id',
    pid: 'parentId',
  });
});

async function handleReload() {
  await loadTree();
  emit('reload');
}

onMounted(loadTree);

function handleSelect(keys: Array<number | string>) {
  emit('select', keys as string[]);
}
</script>

<template>
  <PlatformTreePanel
    v-model:search-value="searchValue"
    v-model:selected-keys="selectDeptId"
    :loading="showTreeSkeleton"
    :show-search="showSearch"
    :tree-data="deptTreeComputed"
    :class="$attrs.class"
    :field-names="{ title: 'label', key: 'id' }"
    :show-line="{ showLeafIcon: false }"
    :styles="{
      item: {
        '--ant-tree-node-selected-bg': 'var(--ant-color-primary-bg-hover)',
      },
    }"
    :virtual="false"
    default-expand-all
    empty-description="无部门数据"
    :search-placeholder="$t('pages.common.search')"
    @reload="handleReload"
    @select="handleSelect"
  >
    <template #titleRender="{ label }">
      <span v-if="label.includes(searchValue)">
        {{ label.substring(0, label.indexOf(searchValue)) }}
        <span class="text-primary">{{ searchValue }}</span>
        {{ label.substring(label.indexOf(searchValue) + searchValue.length) }}
      </span>
      <span v-else>{{ label }}</span>
    </template>
  </PlatformTreePanel>
</template>
