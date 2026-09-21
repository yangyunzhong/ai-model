<script setup lang="ts">
import type { PlatformTableColumn } from '@st/platform-ui';

import type {
  SafetyStudyArchiveQuery,
  SafetyStudyArchiveRecord,
} from '#/mock/modules/battery/archive/safety-study';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformFormItem,
  PlatformIcon,
  PlatformInput,
  PlatformQueryPanel,
  PlatformSelect,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Space } from 'antdv-next';

import {
  getSafetyStudyArchiveList,
  getSafetyStudyStatusMeta,
  removeSafetyStudyArchiveRecord,
  safetyStudyDepartmentOptions,
  safetyStudyProjectCategoryOptions,
  safetyStudyStatusOptions,
  safetyStudyWorkTypeOptions,
} from '#/mock/modules/battery/archive/safety-study';

const createDefaultQuery = (): SafetyStudyArchiveQuery => ({
  department: '',
  keyword: '',
  projectCategory: '',
  status: '',
  workType: '',
});

const query = reactive<SafetyStudyArchiveQuery>(createDefaultQuery());
const loading = ref(false);
const rows = ref<SafetyStudyArchiveRecord[]>([]);
const queryCollapsed = ref(false);
const tablePanelRef = ref<HTMLElement>();
const archiveTableRef = ref<InstanceType<typeof PlatformTable>>();

const tableColumns = computed<PlatformTableColumn[]>(() => [
  {
    dataIndex: 'code',
    key: 'code',
    title: '工号',
    width: 170,
  },
  {
    dataIndex: 'name',
    key: 'name',
    title: '姓名',
    width: 110,
  },
  {
    dataIndex: 'gender',
    key: 'gender',
    title: '性别',
    width: 90,
  },
  {
    dataIndex: 'department',
    key: 'department',
    title: '管理/归属部门',
    width: 220,
  },
  {
    dataIndex: 'workType',
    key: 'workType',
    title: '工种',
    width: 160,
  },
  {
    dataIndex: 'status',
    key: 'status',
    title: '人员状态',
    width: 150,
  },
  {
    dataIndex: 'idCardMasked',
    key: 'idCardMasked',
    title: '身份证号',
    width: 220,
  },
  {
    dataIndex: 'phone',
    key: 'phone',
    title: '联系方式',
    width: 160,
  },
  {
    fixed: 'right',
    key: 'action',
    title: '操作',
    width: 190,
  },
]);

const pagination = computed(() => ({
  pageSize: 10,
  showTotal: (total: number) => `共 ${total} 条`,
  total: rows.value.length,
}));

onMounted(loadArchiveList);

async function loadArchiveList() {
  loading.value = true;
  try {
    rows.value = await getSafetyStudyArchiveList(query);
  } finally {
    loading.value = false;
  }
}

async function handleSearch() {
  await loadArchiveList();
}

async function handleReset() {
  Object.assign(query, createDefaultQuery());
  await loadArchiveList();
}

function handleAdd() {
  window.message.info('已保留“新增档案”入口，后续确认表单后再接入。');
}

function handleExport() {
  window.message.success(`已导出 ${rows.value.length} 条档案台账数据`);
}

function handleView(record: SafetyStudyArchiveRecord) {
  window.message.info(`已保留“${record.name}”档案查看入口。`);
}

function handleEdit(record: SafetyStudyArchiveRecord) {
  window.message.info(`已保留“${record.name}”档案编辑入口。`);
}

async function handleDelete(record: SafetyStudyArchiveRecord) {
  await removeSafetyStudyArchiveRecord(record.id);
  window.message.success(`已删除 ${record.name} 的档案记录`);
  await loadArchiveList();
}

function toArchiveRecord(record: unknown) {
  return record as SafetyStudyArchiveRecord;
}

function handleTableSetting(event: MouseEvent) {
  archiveTableRef.value?.openColumnSetting(event);
}

async function handleTableFullscreen() {
  const panel = tablePanelRef.value;

  if (!panel) {
    return;
  }

  try {
    if (document.fullscreenElement) {
      await document.exitFullscreen();
      return;
    }

    await panel.requestFullscreen();
  } catch {
    window.message.warning('当前浏览器暂不支持全屏展示。');
  }
}
</script>

<template>
  <Page auto-content-height>
    <div class="safety-study-page">
      <PlatformViewToolbar
        description="人员档案管理，支持编辑、删除、查看和导出操作"
        title="人员档案"
      />

      <PlatformQueryPanel
        v-model:collapsed="queryCollapsed"
        :columns="4"
        :default-collapsed="false"
        @query="handleSearch"
        @reset="handleReset"
      >
        <PlatformFormItem label="关键词">
          <PlatformInput
            v-model:value="query.keyword"
            allow-clear
            placeholder="搜索工号、姓名、身份证、电话"
            @press-enter="handleSearch"
          />
        </PlatformFormItem>
        <PlatformFormItem label="管理/归属部门">
          <PlatformSelect
            v-model:value="query.department"
            :options="safetyStudyDepartmentOptions"
            placeholder="全部部门"
          />
        </PlatformFormItem>
        <PlatformFormItem label="工种">
          <PlatformSelect
            v-model:value="query.workType"
            :options="safetyStudyWorkTypeOptions"
            placeholder="全部工种"
          />
        </PlatformFormItem>
        <PlatformFormItem label="人员状态">
          <PlatformSelect
            v-model:value="query.status"
            :options="safetyStudyStatusOptions"
            placeholder="全部状态"
          />
        </PlatformFormItem>
        <PlatformFormItem label="委外项目类别">
          <PlatformSelect
            v-model:value="query.projectCategory"
            :options="safetyStudyProjectCategoryOptions"
            placeholder="全部类别"
          />
        </PlatformFormItem>
      </PlatformQueryPanel>

      <section
        ref="tablePanelRef"
        class="platform-surface safety-study-page__panel"
      >
        <PlatformTableToolbar
          :tools="['search', 'refresh', 'setting', 'fullscreen']"
          @fullscreen="handleTableFullscreen"
          @refresh="handleSearch"
          @search="handleSearch"
          @setting="handleTableSetting"
        >
          <template #actions>
            <PlatformButton scene="toolbar" type="primary" @click="handleAdd">
              <template #icon>
                <PlatformIcon icon="icon-xinzeng" />
              </template>
              新增档案
            </PlatformButton>
            <PlatformButton scene="toolbar" @click="handleExport">
              导出台账
            </PlatformButton>
          </template>
        </PlatformTableToolbar>

        <PlatformTable
          ref="archiveTableRef"
          column-setting-key="battery-archive-safety-study"
          :columns="tableColumns"
          :data-source="rows"
          :index-column="{ title: '序', width: 72 }"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <PlatformStatusTag
                :label="getSafetyStudyStatusMeta(record.status).label"
                :status="getSafetyStudyStatusMeta(record.status).status"
                variant="dot"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <Space :size="12">
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="handleView(toArchiveRecord(record))"
                >
                  查看
                </PlatformButton>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="handleEdit(toArchiveRecord(record))"
                >
                  编辑
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认删除该档案记录？"
                  @confirm="handleDelete(toArchiveRecord(record))"
                >
                  <PlatformButton
                    danger
                    scene="action"
                    size="small"
                    type="link"
                  >
                    删除
                  </PlatformButton>
                </Popconfirm>
              </Space>
            </template>
          </template>
        </PlatformTable>
      </section>
    </div>
  </Page>
</template>

<style scoped>
.safety-study-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.safety-study-page__panel {
  overflow: hidden;
  padding-bottom: 8px;
}

.safety-study-page__panel :deep(.platform-table-toolbar__actions) {
  gap: 20px;
}

.safety-study-page__panel :deep(.platform-table__index-column-header),
.safety-study-page__panel :deep(.platform-table__index-column-cell),
.safety-study-page__panel :deep(.ant-table-thead > tr > th),
.safety-study-page__panel :deep(.ant-table-tbody > tr > td) {
  text-align: center;
}

.safety-study-page__panel :deep(.ant-table-tbody > tr > td:nth-child(2)),
.safety-study-page__panel :deep(.ant-table-thead > tr > th:nth-child(2)),
.safety-study-page__panel :deep(.ant-table-tbody > tr > td:nth-child(5)),
.safety-study-page__panel :deep(.ant-table-thead > tr > th:nth-child(5)),
.safety-study-page__panel :deep(.ant-table-tbody > tr > td:nth-child(6)),
.safety-study-page__panel :deep(.ant-table-thead > tr > th:nth-child(6)),
.safety-study-page__panel :deep(.ant-table-tbody > tr > td:nth-child(8)),
.safety-study-page__panel :deep(.ant-table-thead > tr > th:nth-child(8)),
.safety-study-page__panel :deep(.ant-table-tbody > tr > td:nth-child(9)),
.safety-study-page__panel :deep(.ant-table-thead > tr > th:nth-child(9)) {
  text-align: left;
}

.safety-study-page__panel :deep(.ant-table-thead > tr > th) {
  background: #f3f5f9;
  color: hsl(var(--foreground));
  font-weight: 700;
}

.safety-study-page__panel :deep(.ant-table-tbody > tr > td) {
  height: 72px;
  color: hsl(var(--foreground));
  font-size: 15px;
}

.safety-study-page__panel :deep(.ant-table-pagination.ant-pagination) {
  margin: 20px 24px 8px;
}
</style>
