<script setup lang="ts">
import type { DescriptionsProps, TableEmits, TableProps } from 'antdv-next';

import type {
  ProjectOverviewQuery,
  ProjectRecord,
  ProjectStatus,
} from '#/mock/modules/project/overview';

import { computed, h, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformDescriptions,
  PlatformDrawer,
  PlatformEditForm,
  PlatformFormItem,
  PlatformIcon,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
  PlatformStatCard,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Progress, Space } from 'antdv-next';

import {
  archiveProjectOverviewProject,
  deleteProjectOverviewProject,
  getProjectOverviewList,
  getProjectOverviewStats,
  projectStatusMap,
  projectStatusOptions,
  projectTypeOptions,
  saveProjectOverviewProject,
} from '#/mock/modules/project/overview';

type ProjectFormModel = Partial<ProjectRecord>;

const query = reactive<ProjectOverviewQuery>({
  keyword: '',
  status: '',
  type: '',
});
const router = useRouter();
const formModel = reactive<ProjectFormModel>({});
const tableRows = ref<ProjectRecord[]>([]);
const statCards = ref<Awaited<ReturnType<typeof getProjectOverviewStats>>>([]);
const loading = ref(false);
const saving = ref(false);
const formOpen = ref(false);
const detailOpen = ref(false);
const currentRecord = ref<null | ProjectRecord>(null);
const projectListPanelRef = ref<HTMLElement>();
const projectTableRef = ref<InstanceType<typeof PlatformTable>>();
const headerActions = [
  {
    icon: 'icon-shuiliduixiangshuju',
    key: 'board',
    label: '查看进度看板',
  },
];

const tableColumns = computed<TableProps['columns']>(() => [
  {
    dataIndex: 'name',
    key: 'project',
    title: '项目信息',
    width: 280,
  },
  {
    dataIndex: 'type',
    filterMultiple: false,
    filteredValue: query.type ? [query.type] : null,
    filters: projectTypeOptions
      .filter((item) => item.value)
      .map((item) => ({ text: item.label, value: item.value })),
    key: 'type',
    title: '类型',
    width: 120,
  },
  {
    dataIndex: 'department',
    key: 'department',
    title: '部门',
    width: 130,
  },
  {
    dataIndex: 'amount',
    key: 'amount',
    title: '合同金额',
    width: 130,
  },
  {
    dataIndex: 'durationDays',
    key: 'duration',
    title: '工期',
    width: 110,
  },
  {
    dataIndex: 'manager',
    key: 'manager',
    title: '负责人',
    width: 120,
  },
  {
    dataIndex: 'progress',
    key: 'progress',
    title: '进度',
    width: 170,
  },
  {
    dataIndex: 'status',
    filterMultiple: false,
    filteredValue: query.status ? [query.status] : null,
    filters: projectStatusOptions
      .filter((item) => item.value)
      .map((item) => ({ text: item.label, value: item.value })),
    key: 'status',
    title: '状态',
    width: 110,
  },
  {
    fixed: 'right',
    key: 'action',
    title: '操作',
    width: 190,
  },
]);
const formTitle = computed(() =>
  currentRecord.value ? '编辑项目' : '新建项目',
);
const pagination = computed(() => ({
  pageSize: 10,
  showTotal: (total: number) => `共 ${total} 条`,
  total: tableRows.value.length,
}));
const detailDescriptionItems = computed<DescriptionsProps['items']>(() => {
  const record = currentRecord.value;

  if (!record) {
    return [];
  }

  return [
    {
      content: record.type,
      label: '项目类型',
    },
    {
      content: record.department,
      label: '所属部门',
    },
    {
      content: formatAmount(record.amount),
      label: '合同金额',
    },
    {
      content: `${record.durationDays}天`,
      label: '工期',
    },
    {
      content: record.manager,
      label: '负责人',
    },
    {
      content: h(
        'div',
        {
          class: [
            'project-detail-status-bar',
            `project-detail-status-bar--${projectStatusMap[record.status].status}`,
          ],
        },
        projectStatusMap[record.status].label,
      ),
      label: '项目状态',
    },
    {
      content: `${record.startDate} 至 ${record.endDate}`,
      label: '计划周期',
    },
    {
      content: h('div', { class: 'project-detail-progress-cell' }, [
        h(Progress, {
          class: 'project-detail-progress',
          percent: record.progress,
          showInfo: false,
          size: ['100%', 6],
          strokeColor: 'hsl(var(--primary))',
        }),
        h('span', `${record.progress}%`),
      ]),
      label: '当前进度',
    },
  ];
});

async function loadOverview() {
  loading.value = true;
  try {
    const [rows, stats] = await Promise.all([
      getProjectOverviewList(query),
      getProjectOverviewStats(),
    ]);
    tableRows.value = rows;
    statCards.value = stats;
  } finally {
    loading.value = false;
  }
}

async function handleSearch() {
  await loadOverview();
}

async function handleTableChange(
  ...args: Parameters<TableEmits['change']>
) {
  const [, filters] = args;
  query.type = getFirstFilterValue(filters.type);
  query.status = getFirstFilterValue(filters.status) as '' | ProjectStatus;
  await loadOverview();
}

function handleBoardEntry() {
  router.push('/project/progress');
}

function handleHeaderAction(key: string) {
  if (key === 'board') {
    handleBoardEntry();
  }
}

function handleExport() {
  window.message.info('导出入口已保留，后续确认接口后再接入。');
}

function handleTableSetting(event: MouseEvent) {
  projectTableRef.value?.openColumnSetting(event);
}

async function handleTableFullscreen() {
  const panel = projectListPanelRef.value;

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

function handleAdd() {
  currentRecord.value = null;
  resetForm();
  formOpen.value = true;
}

function handleEdit(record: unknown) {
  const project = toProjectRecord(record);
  currentRecord.value = project;
  resetForm(project);
  formOpen.value = true;
}

function handleDetail(record: unknown) {
  currentRecord.value = toProjectRecord(record);
  detailOpen.value = true;
}

async function handleSave() {
  saving.value = true;
  try {
    await saveProjectOverviewProject({
      ...formModel,
      amount: Number(formModel.amount) || 0,
      durationDays: Number(formModel.durationDays) || 0,
      progress: Number(formModel.progress) || 0,
    });
    window.message.success(currentRecord.value ? '项目已更新' : '项目已创建');
    formOpen.value = false;
    await loadOverview();
  } finally {
    saving.value = false;
  }
}

async function handleArchive(record: unknown) {
  await archiveProjectOverviewProject(toProjectRecord(record).id);
  window.message.success('项目已归档');
  await loadOverview();
}

async function handleDelete(record: unknown) {
  await deleteProjectOverviewProject(toProjectRecord(record).id);
  window.message.success('项目已删除');
  await loadOverview();
}

function resetForm(record?: ProjectRecord) {
  Object.assign(formModel, {
    amount: record?.amount ?? 0,
    code: record?.code ?? '',
    color: record?.color ?? '#10b981',
    department: record?.department ?? '工程一部',
    durationDays: record?.durationDays ?? 0,
    endDate: record?.endDate ?? '2026-12-31',
    id: record?.id,
    manager: record?.manager ?? '',
    name: record?.name ?? '',
    progress: record?.progress ?? 0,
    startDate: record?.startDate ?? '2026-01-01',
    status: record?.status ?? 'pending',
    type: record?.type ?? projectTypeOptions[1]?.value,
  });
}

function formatAmount(amount: number) {
  return `${amount.toLocaleString()}万`;
}

function getProjectInitial(name: string) {
  return name.slice(0, 1);
}

function getStatusMeta(status: ProjectStatus) {
  return projectStatusMap[status];
}

function getFirstFilterValue(value: unknown) {
  if (Array.isArray(value)) {
    return String(value[0] ?? '');
  }
  return '';
}

function toProjectRecord(record: unknown) {
  return record as ProjectRecord;
}

onMounted(loadOverview);
</script>

<template>
  <Page :auto-content-height="true">
    <div class="project-overview-page">
      <PlatformViewToolbar
        :actions="headerActions"
        description="全生命周期数字化管理"
        title="项目总览"
        @action="handleHeaderAction"
      />

      <section class="project-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          v-bind="card"
        />
      </section>

      <section ref="projectListPanelRef" class="platform-surface project-list-panel">
        <PlatformTableToolbar
          v-model:search-value="query.keyword"
          search-placeholder="搜索项目名称 / 编号 / 负责人"
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
              新建项目
            </PlatformButton>
            <PlatformButton scene="toolbar" @click="handleExport">
              <template #icon>
                <PlatformIcon icon="icon-xiazai1" />
              </template>
              导出
            </PlatformButton>
          </template>
        </PlatformTableToolbar>

        <PlatformTable
          ref="projectTableRef"
          column-setting-key="project-overview"
          :columns="tableColumns"
          :data-source="tableRows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'project'">
              <div class="project-info-cell">
                <span
                  class="project-avatar"
                  :style="{ backgroundColor: record.color }"
                >
                  {{ getProjectInitial(record.name) }}
                </span>
                <div>
                  <PlatformButton
                    scene="action"
                    type="link"
                    @click="handleDetail(record)"
                  >
                    {{ record.name }}
                  </PlatformButton>
                  <p>{{ record.code }}</p>
                </div>
              </div>
            </template>
            <template v-else-if="column.key === 'amount'">
              <strong>{{ formatAmount(record.amount) }}</strong>
            </template>
            <template v-else-if="column.key === 'duration'">
              {{ record.durationDays }}天
            </template>
            <template v-else-if="column.key === 'progress'">
              <div class="project-progress-cell">
                <Progress
                  :percent="record.progress"
                  :show-info="false"
                  :size="['100%', 6]"
                  class="project-progress"
                />
                <span>{{ record.progress }}%</span>
              </div>
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="getStatusMeta(record.status).label"
                :status="getStatusMeta(record.status).status"
                variant="dot"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <Space>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="handleEdit(record)"
                >
                  编辑
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认归档该项目？"
                  @confirm="handleArchive(record)"
                >
                  <PlatformButton scene="action" size="small" type="link">
                    归档
                  </PlatformButton>
                </Popconfirm>
                <Popconfirm
                  placement="left"
                  title="确认删除该项目？"
                  @confirm="handleDelete(record)"
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

    <PlatformModal
      v-model:open="formOpen"
      :confirm-loading="saving"
      :title="formTitle"
      destroy-on-close
      width="720px"
      @cancel="formOpen = false"
      @ok="handleSave"
    >
      <PlatformEditForm
        :model="formModel"
        class="project-form-grid"
        layout="vertical"
      >
        <PlatformFormItem label="项目名称">
          <PlatformInput
            v-model:value="formModel.name"
            placeholder="请输入项目名称"
          />
        </PlatformFormItem>
        <PlatformFormItem label="项目编号">
          <PlatformInput
            v-model:value="formModel.code"
            placeholder="请输入项目编号"
          />
        </PlatformFormItem>
        <PlatformFormItem label="项目类型">
          <PlatformSelect
            v-model:value="formModel.type"
            :options="projectTypeOptions.filter((item) => item.value)"
            placeholder="请选择项目类型"
          />
        </PlatformFormItem>
        <PlatformFormItem label="所属部门">
          <PlatformInput
            v-model:value="formModel.department"
            placeholder="请输入所属部门"
          />
        </PlatformFormItem>
        <PlatformFormItem label="合同金额（万）">
          <PlatformInput
            v-model:value="formModel.amount"
            placeholder="请输入合同金额"
            type="number"
          />
        </PlatformFormItem>
        <PlatformFormItem label="工期（天）">
          <PlatformInput
            v-model:value="formModel.durationDays"
            placeholder="请输入工期"
            type="number"
          />
        </PlatformFormItem>
        <PlatformFormItem label="负责人">
          <PlatformInput
            v-model:value="formModel.manager"
            placeholder="请输入负责人"
          />
        </PlatformFormItem>
        <PlatformFormItem label="进度（%）">
          <PlatformInput
            v-model:value="formModel.progress"
            placeholder="请输入进度"
            type="number"
          />
        </PlatformFormItem>
        <PlatformFormItem label="状态">
          <PlatformSelect
            v-model:value="formModel.status"
            :options="projectStatusOptions.filter((item) => item.value)"
            placeholder="请选择状态"
          />
        </PlatformFormItem>
        <PlatformFormItem label="图标颜色">
          <PlatformInput
            v-model:value="formModel.color"
            placeholder="请输入图标颜色"
          />
        </PlatformFormItem>
      </PlatformEditForm>
    </PlatformModal>

    <PlatformDrawer
      v-model:open="detailOpen"
      destroy-on-close
      size="large"
      title="项目详情"
    >
      <div v-if="currentRecord" class="project-detail">
        <div class="project-detail-title">
          <span
            class="project-avatar project-avatar--large"
            :style="{ backgroundColor: currentRecord.color }"
          >
            {{ getProjectInitial(currentRecord.name) }}
          </span>
          <div>
            <h3>{{ currentRecord.name }}</h3>
            <p>{{ currentRecord.code }}</p>
          </div>
        </div>
        <PlatformDescriptions :items="detailDescriptionItems" />
      </div>
    </PlatformDrawer>
  </Page>
</template>

<style scoped>
.project-overview-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.project-info-cell p,
.project-detail-title p {
  margin: 0;
  color: hsl(var(--muted-foreground));
}

.project-stat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.project-list-panel:fullscreen {
  overflow: auto;
  background: hsl(var(--st-color-card-bg));
}

.project-info-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.project-avatar {
  display: inline-flex;
  flex: none;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  font-weight: 700;
  color: #fff;
  border-radius: 8px;
}

.project-avatar--large {
  width: 48px;
  height: 48px;
  font-size: 18px;
}

.project-progress-cell,
.project-detail-progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.project-progress,
.project-detail-progress {
  width: 92px;
}

.project-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.project-detail-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.project-detail-title h3 {
  margin: 0 0 4px;
  font-size: 18px;
}

.project-detail-status-bar {
  display: inline-flex;
  width: 100%;
  min-height: 26px;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  color: hsl(var(--primary-foreground));
  border-radius: var(--st-radius-control);
}

.project-detail-status-bar--processing {
  background: hsl(var(--st-color-stat-card-info));
}

.project-detail-status-bar--success {
  background: hsl(var(--primary));
}

.project-detail-status-bar--warning {
  background: hsl(var(--warning));
}

.project-detail-status-bar--default {
  background: hsl(var(--muted-foreground));
}

.project-detail-status-bar--error {
  background: hsl(var(--destructive));
}

@media (max-width: 1280px) {
  .project-stat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 960px) {
  .project-stat-grid,
  .project-form-grid {
    grid-template-columns: 1fr;
  }

}
</style>
