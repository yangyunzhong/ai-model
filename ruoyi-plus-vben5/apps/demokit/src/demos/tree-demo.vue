<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import { computed, reactive, ref } from 'vue';

import {
  PlatformButton,
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformSectionTitle,
  PlatformTable,
  PlatformTree,
  PlatformTreePanel,
} from '@st/platform-ui';

type TreeNode = {
  children?: TreeNode[];
  key: string;
  title: string;
};

const rawTreeData: TreeNode[] = [
  {
    children: [
      {
        children: [
          { key: 'analysis', title: '质量缺陷分析' },
          { key: 'board', title: '经营数据看板' },
        ],
        key: 'project',
        title: '项目分析',
      },
      {
        children: [{ key: 'assets', title: '设备资产详情' }],
        key: 'resource',
        title: '资源中心',
      },
    ],
    key: 'root',
    title: '业务场景',
  },
];

const selectedKeys = ref<string[]>(['analysis']);
const searchValue = ref('');
const submittedKeyword = ref('');
const maintenanceForm = reactive({
  createdAt: '2026-05-03 09:20',
  createdUser: '张明远',
  updatedAt: '2026-05-05 11:05',
  updatedUser: '王栋',
});

const documentRows = [
  {
    createdAt: '2026-05-03 09:20',
    fileName: '会议纪要-2026Q1.pdf',
    id: 1,
    size: '2.4MB',
    uploader: '张明远',
  },
  {
    createdAt: '2026-05-04 10:12',
    fileName: '非停事件处置记录.docx',
    id: 2,
    size: '1.8MB',
    uploader: '李林杰',
  },
  {
    createdAt: '2026-05-05 11:05',
    fileName: '年度总结与计划.xlsx',
    id: 3,
    size: '920KB',
    uploader: '王栋',
  },
];

const documentColumns: PlatformTableColumns = [
  { dataIndex: 'id', key: 'id', title: '序号', width: 90 },
  { dataIndex: 'fileName', key: 'fileName', title: '文件名', width: 320 },
  { dataIndex: 'size', key: 'size', title: '文件大小', width: 140 },
  { dataIndex: 'createdAt', key: 'createdAt', title: '上传时间', width: 180 },
  { dataIndex: 'uploader', key: 'uploader', title: '上传人', width: 120 },
];

const props = defineProps<{
  demoId?: string;
}>();

const filteredTreeData = computed(() => {
  const keyword = submittedKeyword.value.trim();
  if (!keyword) {
    return rawTreeData;
  }
  return filterTreeNodes(rawTreeData, keyword);
});

function handleSearch(value: string) {
  submittedKeyword.value = value;
}

function filterTreeNodes(nodes: TreeNode[], keyword: string): TreeNode[] {
  const result: TreeNode[] = [];

  for (const node of nodes) {
    const children = node.children ? filterTreeNodes(node.children, keyword) : undefined;
    const matched = node.title.includes(keyword);

    if (!matched && (!children || children.length === 0)) {
      continue;
    }

    result.push({
      ...node,
      ...(children ? { children } : {}),
    });
  }

  return result;
}
</script>

<template>
  <div class="demo-sidebar-layout">
    <template v-if="props.demoId === 'platform-tree-panel'">
      <div class="demo-surface demo-surface--bare">
        <PlatformTreePanel
          v-model:search-value="searchValue"
          v-model:selected-keys="selectedKeys"
          :default-expand-all="true"
          :show-refresh="false"
          :tree-data="filteredTreeData"
          :virtual="false"
          empty-description="无文档目录"
          search-placeholder="请输入文档类型/名称"
          show-line
          @search="handleSearch"
        />
      </div>

      <section class="demo-surface demo-flow">
        <PlatformSectionTitle title="文档列表">
          <template #extra>
            <PlatformButton scene="toolbar" type="primary">上传文件</PlatformButton>
          </template>
        </PlatformSectionTitle>

        <div class="demo-surface demo-surface--bare">
          <PlatformTable
            :columns="documentColumns"
            :data-source="documentRows"
            row-key="id"
          />
        </div>

        <section class="demo-flow">
          <PlatformSectionTitle title="维护信息" />

          <PlatformEditForm
            class="demo-document-form"
            label-preset="inline-compact"
            layout="horizontal"
          >
            <div class="demo-document-form__grid">
              <PlatformFormItem label="建立用户">
                <PlatformInput v-model:value="maintenanceForm.createdUser" disabled placeholder="无" />
              </PlatformFormItem>
              <PlatformFormItem label="建立时间">
                <PlatformInput v-model:value="maintenanceForm.createdAt" disabled placeholder="无" />
              </PlatformFormItem>
              <PlatformFormItem label="更新用户">
                <PlatformInput v-model:value="maintenanceForm.updatedUser" disabled placeholder="无" />
              </PlatformFormItem>
              <PlatformFormItem label="更新时间">
                <PlatformInput v-model:value="maintenanceForm.updatedAt" disabled placeholder="无" />
              </PlatformFormItem>
            </div>
          </PlatformEditForm>
        </section>

        <div class="demo-document-footer">
          <PlatformButton disabled>暂存</PlatformButton>
          <PlatformButton disabled type="primary">保存</PlatformButton>
        </div>
      </section>
    </template>

    <div v-else class="demo-surface demo-flow">
      <h3 class="demo-title">Tree</h3>
      <p class="demo-muted">
        Tree 只负责节点展开、选中和层级展示；如果需要搜索、标题和刷新，再用 TreePanel 包裹。
      </p>
      <PlatformTree
        default-expand-all
        :selected-keys="selectedKeys"
        :tree-data="filteredTreeData"
      />
    </div>
  </div>
</template>
