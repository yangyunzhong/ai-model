<script setup lang="ts">
import { reactive } from 'vue';

import {
  PlatformButton,
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformQueryPanel,
  PlatformSearchForm,
  PlatformSelect,
  PlatformTable,
  PlatformTableToolbar,
} from '@st/platform-ui';
import type { PlatformTableColumns } from '@st/platform-ui';

const queryState = reactive({
  keyword: '',
  status: '',
});

const editState = reactive({
  leader: '张衡',
  projectName: '设备资产升级',
});

const statusOptions = [
  { label: '全部', value: '' },
  { label: '进行中', value: 'processing' },
  { label: '已完成', value: 'finished' },
  { label: '已暂停', value: 'paused' },
];

const queryColumns: PlatformTableColumns = [
  { dataIndex: 'name', key: 'name', title: '项目名称', width: 220 },
  { dataIndex: 'owner', key: 'owner', title: '负责人', width: 120 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 120 },
  { dataIndex: 'updatedAt', key: 'updatedAt', title: '更新时间', width: 180 },
];

const queryRows = [
  { id: 1, name: '华东治理专项', owner: '王敏', status: '进行中', updatedAt: '2026-05-28 10:20' },
  { id: 2, name: '智慧工厂建设', owner: '陈涛', status: '待审批', updatedAt: '2026-05-28 09:12' },
  { id: 3, name: '质量整改闭环', owner: '李蕾', status: '已完成', updatedAt: '2026-05-27 17:48' },
];

function handleReset() {
  queryState.keyword = '';
  queryState.status = '';
}

const props = defineProps<{
  demoId?: string;
}>();
</script>

<template>
  <div class="demo-flow">
    <div v-if="props.demoId === 'platform-query-panel'" class="demo-surface demo-flow">
      <h3 class="demo-title">筛选区 / QueryPanel</h3>
      <p class="demo-muted">
        QueryPanel 是“带外框、可展开、带查询/重置行为”的完整筛选区容器，不是单个字段组件。
      </p>
      <PlatformQueryPanel
        collapsible
        :columns="3"
        @reset="handleReset"
      >
        <PlatformFormItem label="项目关键词">
          <PlatformInput
            v-model:value="queryState.keyword"
            allow-clear
            placeholder="请输入项目名称"
          />
        </PlatformFormItem>
        <PlatformFormItem label="执行状态">
          <PlatformSelect
            v-model:value="queryState.status"
            :options="statusOptions"
            placeholder="请选择状态"
          />
        </PlatformFormItem>
        <PlatformFormItem label="责任人">
          <PlatformInput placeholder="请输入责任人" />
        </PlatformFormItem>
        <PlatformFormItem label="项目编码">
          <PlatformInput placeholder="请输入项目编码" />
        </PlatformFormItem>
      </PlatformQueryPanel>

      <PlatformTableToolbar
        :bleed="false"
        description="与筛选区搭配后，更接近真实业务列表页。"
        title="项目台账"
        :tools="['refresh', 'setting', 'fullscreen']"
      />

      <div class="demo-surface demo-query-panel-result">
        <PlatformTable :columns="queryColumns" :data-source="queryRows" row-key="id" />
      </div>
    </div>

    <div v-if="props.demoId !== 'platform-query-panel'" class="demo-grid-2">
      <div v-if="props.demoId === 'platform-search-form'" class="demo-surface demo-flow">
        <h3 class="demo-title">SearchForm</h3>
        <p class="demo-muted">
          SearchForm 解决的是“紧凑搜索栏编排 + actions 区”，所以里面出现 Input / Select 是正常组合，不是重复定义基础字段。
        </p>
        <PlatformSearchForm :model="queryState">
          <PlatformFormItem label="项目名称">
            <PlatformInput
              v-model:value="queryState.keyword"
              placeholder="可直接复用在顶部筛选条"
            />
          </PlatformFormItem>
          <PlatformFormItem label="状态">
            <PlatformSelect
              v-model:value="queryState.status"
              :options="statusOptions"
              placeholder="请选择"
            />
          </PlatformFormItem>
          <template #actions>
            <PlatformButton scene="toolbar">重置</PlatformButton>
            <PlatformButton scene="toolbar" type="primary">查询</PlatformButton>
          </template>
        </PlatformSearchForm>
      </div>

      <div v-if="props.demoId === 'platform-edit-form'" class="demo-surface demo-flow">
        <h3 class="demo-title">EditForm</h3>
        <p class="demo-muted">
          EditForm 是编辑场景表单预设，重点在编辑布局和字段编排；Input 只是它内部消费的基础控件。
        </p>
        <PlatformEditForm
          :model="editState"
          label-preset="inline-compact"
          layout="horizontal"
        >
          <PlatformFormItem label="项目名称" name="projectName">
            <PlatformInput
              v-model:value="editState.projectName"
              placeholder="请输入项目名称"
            />
          </PlatformFormItem>
          <PlatformFormItem label="项目负责人" name="leader">
            <PlatformInput
              v-model:value="editState.leader"
              placeholder="请输入负责人"
            />
          </PlatformFormItem>
        </PlatformEditForm>
      </div>
    </div>
  </div>
</template>
