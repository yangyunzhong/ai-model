<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import { reactive, ref } from 'vue';

import {
  PlatformButton,
  PlatformFormItem,
  PlatformInput,
  PlatformQueryPanel,
  PlatformSelect,
  PlatformTable,
  PlatformTableToolbar,
} from '@st/platform-ui';

const searchValue = ref('');
const statusValue = ref('');
const typeValue = ref('');
const panelQuery = reactive({
  keyword: '',
  owner: '',
  status: '',
});

const props = defineProps<{
  demoId?: string;
}>();

const typeOptions = [
  { label: '全部类型', value: '' },
  { label: '建设项目', value: 'build' },
  { label: '整改任务', value: 'fix' },
];

const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '进行中', value: 'processing' },
  { label: '待审批', value: 'pending' },
  { label: '已完成', value: 'finished' },
];

const columns: PlatformTableColumns = [
  {
    dataIndex: 'name',
    key: 'name',
    title: '项目名称',
    width: 220,
  },
  {
    dataIndex: 'owner',
    key: 'owner',
    title: '负责人',
    width: 140,
  },
  {
    dataIndex: 'status',
    key: 'status',
    title: '执行状态',
    width: 140,
  },
  {
    dataIndex: 'updatedAt',
    key: 'updatedAt',
    title: '更新时间',
    width: 180,
  },
  {
    dataIndex: 'action',
    key: 'action',
    title: '操作',
    width: 180,
  },
];

const tableData = [
  {
    action: '查看 / 编辑',
    id: 1,
    name: '华东治理专项',
    owner: '王敏',
    status: '进行中',
    updatedAt: '2026-05-28 10:20',
  },
  {
    action: '查看 / 审批',
    id: 2,
    name: '智慧工厂建设',
    owner: '陈涛',
    status: '待审批',
    updatedAt: '2026-05-28 09:12',
  },
  {
    action: '查看 / 归档',
    id: 3,
    name: '质量整改闭环',
    owner: '李蕾',
    status: '已完成',
    updatedAt: '2026-05-27 17:48',
  },
];

function handleReset() {
  panelQuery.keyword = '';
  panelQuery.owner = '';
  panelQuery.status = '';
}
</script>

<template>
  <div class="demo-flow">
    <div v-if="props.demoId !== 'platform-table-toolbar'" class="demo-surface demo-surface--bare">
      <PlatformTable
        :columns="columns"
        :data-source="tableData"
        row-key="id"
      />
    </div>

    <div v-else class="demo-surface demo-flow">
      <div class="demo-surface demo-flow">
        <h3 class="demo-title">无筛选区表格面板</h3>
        <PlatformTableToolbar
          description="适合直接进入列表的人员档案、台账、名录类页面。"
          title="人员档案列表"
          :actions="[{ key: 'create', label: '新增人员', type: 'primary' }]"
          :tools="['search', 'refresh', 'setting', 'fullscreen']"
        />
        <div class="demo-surface demo-surface--bare">
          <PlatformTable
            :columns="columns"
            :data-source="tableData"
            row-key="id"
          />
        </div>
      </div>

      <div class="demo-surface demo-flow">
        <h3 class="demo-title">带筛选区表格面板</h3>
        <PlatformQueryPanel collapsible :columns="3" @reset="handleReset">
          <PlatformFormItem label="项目关键词">
            <PlatformInput v-model:value="panelQuery.keyword" placeholder="请输入项目名称" />
          </PlatformFormItem>
          <PlatformFormItem label="负责人">
            <PlatformSelect
              v-model:value="panelQuery.owner"
              :options="[
                { label: '全部负责人', value: '' },
                { label: '王敏', value: 'wangmin' },
                { label: '陈涛', value: 'chentao' },
                { label: '李蕾', value: 'lilei' },
              ]"
              placeholder="请选择负责人"
            />
          </PlatformFormItem>
          <PlatformFormItem label="执行状态">
            <PlatformSelect
              v-model:value="panelQuery.status"
              :options="statusOptions"
              placeholder="请选择状态"
            />
          </PlatformFormItem>
          <template #actions>
            <PlatformButton scene="toolbar">重置</PlatformButton>
            <PlatformButton scene="toolbar" type="primary">查询</PlatformButton>
          </template>
        </PlatformQueryPanel>

        <PlatformTableToolbar
          description="适合先筛选再入表的文档、项目、整改任务列表。"
          v-model:search-value="searchValue"
          v-model:status-value="statusValue"
          title="项目文档列表"
          :status-options="statusOptions"
          :tools="['search', 'refresh', 'setting', 'fullscreen']"
          :type-options="typeOptions"
          v-model:type-value="typeValue"
        />

        <div class="demo-surface demo-surface--bare">
          <PlatformTable
            :columns="columns"
            :data-source="tableData"
            row-key="id"
          />
        </div>
      </div>
    </div>
  </div>
</template>
