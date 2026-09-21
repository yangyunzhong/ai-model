<script setup lang="ts">
import type { TableEmits, TableProps } from 'antdv-next';

import type {
  PersonnelOverviewFormModel,
  PersonnelOverviewQuery,
  PersonnelOverviewRecord,
  PersonnelOverviewStatCard,
} from '#/mock/modules/personnel/overview';

import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformDatePicker,
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
import { Popconfirm, Space } from 'antdv-next';

import {
  getPersonnelOverviewList,
  getPersonnelOverviewStats,
  getPersonnelStatusMeta,
  getQualificationStatusMeta,
  personnelContractorOptions,
  personnelGenderOptions,
  personnelHealthOptions,
  personnelPositionOptions,
  personnelProjectOptions,
  personnelStatusOptions,
  qualificationStatusOptions,
  removePersonnelOverviewRecord,
  savePersonnelOverviewRecord,
} from '#/mock/modules/personnel/overview';

const createDefaultQuery = (): PersonnelOverviewQuery => ({
  keyword: '',
  qualificationStatus: '',
  status: '',
});

const query = reactive<PersonnelOverviewQuery>(createDefaultQuery());
const router = useRouter();
const formModel = reactive<PersonnelOverviewFormModel>({});
const tableRows = ref<PersonnelOverviewRecord[]>([]);
const statCards = ref<PersonnelOverviewStatCard[]>([]);
const loading = ref(false);
const saving = ref(false);
const formOpen = ref(false);
const currentRecord = ref<null | PersonnelOverviewRecord>(null);
const tablePanelRef = ref<HTMLElement>();
const personnelTableRef = ref<InstanceType<typeof PlatformTable>>();

const tableColumns = computed<TableProps['columns']>(() => [
  {
    dataIndex: 'name',
    key: 'person',
    title: '人员信息',
    width: 220,
  },
  {
    dataIndex: 'gender',
    key: 'gender',
    title: '性别',
    width: 80,
  },
  {
    dataIndex: 'age',
    key: 'age',
    title: '年龄',
    width: 80,
  },
  {
    dataIndex: 'contractor',
    key: 'contractor',
    title: '承包商',
    width: 180,
  },
  {
    dataIndex: 'project',
    key: 'project',
    title: '所属项目',
    width: 220,
  },
  {
    dataIndex: 'position',
    key: 'position',
    title: '岗位',
    width: 140,
  },
  {
    dataIndex: 'startDate',
    key: 'startDate',
    title: '入职日期',
    width: 130,
  },
  {
    dataIndex: 'status',
    filterMultiple: false,
    filteredValue: query.status ? [query.status] : null,
    filters: personnelStatusOptions
      .filter((item) => item.value)
      .map((item) => ({ text: item.label, value: item.value })),
    key: 'status',
    title: '状态',
    width: 120,
  },
  {
    dataIndex: 'qualificationStatus',
    filterMultiple: false,
    filteredValue: query.qualificationStatus
      ? [query.qualificationStatus]
      : null,
    filters: qualificationStatusOptions
      .filter((item) => item.value)
      .map((item) => ({ text: item.label, value: item.value })),
    key: 'qualificationStatus',
    title: '资质状态',
    width: 140,
  },
  {
    fixed: 'right',
    key: 'action',
    title: '操作',
    width: 160,
  },
]);
const pagination = computed(() => ({
  pageSize: 10,
  showTotal: (total: number) => `共 ${total} 条`,
  total: tableRows.value.length,
}));
const formTitle = computed(() => (currentRecord.value ? '编辑人员' : '新增人员'));

async function loadPersonnelOverview() {
  loading.value = true;
  try {
    const [list, stats] = await Promise.all([
      getPersonnelOverviewList(query),
      getPersonnelOverviewStats(),
    ]);
    tableRows.value = list;
    statCards.value = stats;
  } finally {
    loading.value = false;
  }
}

async function handleSearch() {
  await loadPersonnelOverview();
}

async function handleTableChange(...args: Parameters<TableEmits['change']>) {
  const [, filters] = args;
  query.status = getFirstFilterValue(
    filters.status,
  ) as PersonnelOverviewQuery['status'];
  query.qualificationStatus = getFirstFilterValue(
    filters.qualificationStatus,
  ) as PersonnelOverviewQuery['qualificationStatus'];
  await loadPersonnelOverview();
}

function handleAdd() {
  currentRecord.value = null;
  resetForm();
  formOpen.value = true;
}

function handleEdit(record: unknown) {
  const personnel = toPersonnelRecord(record);
  currentRecord.value = personnel;
  resetForm(personnel);
  formOpen.value = true;
}

function handleView(record: unknown) {
  const personnel = toPersonnelRecord(record);
  router.push({
    path: '/personnel/overview/detail',
    query: {
      id: String(personnel.id),
    },
  });
}

async function handleSave() {
  if (!formModel.name?.trim()) {
    window.message.warning('请填写姓名。');
    return;
  }
  if (!formModel.contractor) {
    window.message.warning('请选择承包商。');
    return;
  }
  if (!formModel.position) {
    window.message.warning('请选择岗位。');
    return;
  }

  saving.value = true;
  try {
    await savePersonnelOverviewRecord(formModel);
    window.message.success(currentRecord.value ? '人员已更新' : '人员已新增');
    formOpen.value = false;
    await loadPersonnelOverview();
  } finally {
    saving.value = false;
  }
}

async function handleDelete(record: unknown) {
  await removePersonnelOverviewRecord(toPersonnelRecord(record).id);
  window.message.success('人员已删除');
  await loadPersonnelOverview();
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

function handleTableSetting(event: MouseEvent) {
  personnelTableRef.value?.openColumnSetting(event);
}

function resetForm(record?: PersonnelOverviewRecord) {
  Object.assign(formModel, {
    age: record?.age ?? '',
    contractor: record?.contractor ?? '',
    gender: record?.gender ?? '',
    health: record?.health ?? '健康',
    id: record?.id,
    idCard: record?.idCard ?? '',
    name: record?.name ?? '',
    phone: record?.phone ?? '',
    position: record?.position ?? '',
    project: record?.project ?? '',
    qualificationExpireDate: record?.qualificationExpireDate ?? '',
    qualificationName: record?.qualificationName ?? '',
    qualificationStatus: record?.qualificationStatus ?? undefined,
    startDate: record?.startDate ?? '',
    status: record?.status ?? 'pending',
  });
}

function getFirstFilterValue(value: unknown) {
  if (Array.isArray(value)) {
    return String(value[0] ?? '');
  }
  return '';
}

function toPersonnelRecord(record: unknown) {
  return record as PersonnelOverviewRecord;
}

onMounted(loadPersonnelOverview);
</script>

<template>
  <Page :auto-content-height="true">
    <div class="personnel-overview-page">
      <PlatformViewToolbar
        description="人员全生命周期管理"
        title="人员总览"
      />

      <section class="personnel-overview-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :icon="card.icon"
          :title="card.title"
          :trend-text="card.trendText"
          :trend-type="card.trendType"
          :type="card.type"
          :value="card.value"
        />
      </section>

      <section
        ref="tablePanelRef"
        class="platform-surface personnel-overview-panel"
      >
        <PlatformTableToolbar
          v-model:search-value="query.keyword"
          search-placeholder="搜索姓名 / 编号 / 承包商 / 项目"
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
              新增人员
            </PlatformButton>
          </template>
        </PlatformTableToolbar>

        <PlatformTable
          ref="personnelTableRef"
          column-setting-key="personnel-overview"
          :columns="tableColumns"
          :data-source="tableRows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'person'">
              <div class="personnel-overview-person-cell">
                <div>
                  <strong>{{ record.name }}</strong>
                  <span>{{ record.code }}</span>
                </div>
              </div>
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="getPersonnelStatusMeta(record.status).label"
                :status="getPersonnelStatusMeta(record.status).status"
                variant="dot"
              />
            </template>
            <template v-else-if="column.key === 'qualificationStatus'">
              <PlatformStatusTag
                :label="
                  getQualificationStatusMeta(record.qualificationStatus).label
                "
                :status="
                  getQualificationStatusMeta(record.qualificationStatus).status
                "
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
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="handleView(record)"
                >
                  查看
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认删除该人员？删除后将从当前列表移除。"
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
      cancel-text="取消"
      centered
      destroy-on-close
      ok-text="保存"
      :title="formTitle"
      width="960px"
      @cancel="formOpen = false"
      @ok="handleSave"
    >
      <PlatformEditForm
        :model="formModel"
        class="personnel-overview-form"
        layout="vertical"
      >
        <div class="personnel-overview-form__grid">
          <PlatformFormItem label="姓名" required>
            <PlatformInput
              v-model:value="formModel.name"
              placeholder="请输入姓名"
            />
          </PlatformFormItem>
          <PlatformFormItem label="性别">
            <PlatformSelect
              v-model:value="formModel.gender"
              :options="personnelGenderOptions"
              placeholder="请选择性别"
            />
          </PlatformFormItem>
          <PlatformFormItem label="年龄">
            <PlatformInput
              v-model:value="formModel.age"
              placeholder="请输入年龄"
              type="number"
            />
          </PlatformFormItem>
          <PlatformFormItem label="联系电话">
            <PlatformInput
              v-model:value="formModel.phone"
              placeholder="请输入联系电话"
            />
          </PlatformFormItem>
          <PlatformFormItem label="身份证号">
            <PlatformInput
              v-model:value="formModel.idCard"
              placeholder="请输入身份证号"
            />
          </PlatformFormItem>
          <PlatformFormItem label="身体状况">
            <PlatformSelect
              v-model:value="formModel.health"
              :options="personnelHealthOptions"
              placeholder="请选择身体状况"
            />
          </PlatformFormItem>
          <PlatformFormItem label="承包商" required>
            <PlatformSelect
              v-model:value="formModel.contractor"
              :options="personnelContractorOptions"
              placeholder="请选择承包商"
            />
          </PlatformFormItem>
          <PlatformFormItem label="所属项目">
            <PlatformSelect
              v-model:value="formModel.project"
              :options="personnelProjectOptions"
              placeholder="请选择所属项目"
            />
          </PlatformFormItem>
          <PlatformFormItem label="岗位" required>
            <PlatformSelect
              v-model:value="formModel.position"
              :options="personnelPositionOptions"
              placeholder="请选择岗位"
            />
          </PlatformFormItem>
          <PlatformFormItem label="入职日期">
            <PlatformDatePicker
              v-model:value="formModel.startDate"
              placeholder="请选择入职日期"
              value-format="YYYY-MM-DD"
            />
          </PlatformFormItem>
          <PlatformFormItem label="资质证书">
            <PlatformInput
              v-model:value="formModel.qualificationName"
              placeholder="请输入资质证书"
            />
          </PlatformFormItem>
          <PlatformFormItem label="证书到期日">
            <PlatformDatePicker
              v-model:value="formModel.qualificationExpireDate"
              placeholder="请选择证书到期日"
              value-format="YYYY-MM-DD"
            />
          </PlatformFormItem>
        </div>
      </PlatformEditForm>
    </PlatformModal>
  </Page>
</template>

<style scoped>
.personnel-overview-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.personnel-overview-stat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.personnel-overview-panel:fullscreen {
  overflow: auto;
  background: hsl(var(--st-color-card-bg));
}

.personnel-overview-person-cell {
  display: flex;
  align-items: center;
  min-width: 0;
}

.personnel-overview-person-cell div {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.personnel-overview-person-cell strong {
  color: hsl(var(--foreground));
  font-weight: 600;
}

.personnel-overview-person-cell span:last-child {
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

.personnel-overview-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 24px;
}

.personnel-overview-form :deep(.ant-form-item) {
  margin-bottom: 18px;
}

@media (max-width: 1280px) {
  .personnel-overview-stat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 960px) {
  .personnel-overview-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .personnel-overview-stat-grid,
  .personnel-overview-form__grid {
    grid-template-columns: 1fr;
  }
}
</style>
