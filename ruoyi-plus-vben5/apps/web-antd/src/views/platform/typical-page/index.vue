<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { VbenIcon } from '@vben/icons';

import {
  PlatformButton,
  PlatformDatePicker,
  PlatformEditForm,
  PlatformFormItem,
  PlatformIcon,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
  PlatformViewToolbar,
} from '@st/platform-ui';

import PersonnelArchiveCard from './components/personnel-archive-card.vue';
import PersonnelArchiveDetailDrawer from './components/personnel-archive-detail-drawer.vue';
import {
  createPersonnelArchiveRecord,
  genderOptions,
  getPersonnelArchiveList,
  healthStatusOptions,
  type PersonnelArchiveFormModel,
  type PersonnelArchiveQuery,
  type PersonnelArchiveRecord,
  positionOptions,
  projectOptions,
} from '#/mock/modules/platform/typical-page';

const query = reactive<PersonnelArchiveQuery>({
  keyword: '',
});
const records = ref<PersonnelArchiveRecord[]>([]);
const loading = ref(false);
const saving = ref(false);
const formOpen = ref(false);
const detailOpen = ref(false);
const selectedRecord = ref<null | PersonnelArchiveRecord>(null);
const formModel = reactive<PersonnelArchiveFormModel>({});

const cardCount = computed(() => records.value.length);

async function loadPersonnelArchive() {
  loading.value = true;
  try {
    records.value = await getPersonnelArchiveList(query);
    if (
      selectedRecord.value &&
      !records.value.some((item) => item.id === selectedRecord.value?.id)
    ) {
      selectedRecord.value = records.value[0] ?? null;
    }
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  void loadPersonnelArchive();
}

function handleAdd() {
  resetForm();
  formOpen.value = true;
}

function handleOpenDetail(record: PersonnelArchiveRecord) {
  selectedRecord.value = record;
  detailOpen.value = true;
}

async function handleSave() {
  if (!formModel.name?.trim()) {
    window.message.warning('请填写姓名。');
    return;
  }
  if (!formModel.contractor?.trim()) {
    window.message.warning('请填写承包商。');
    return;
  }
  if (!formModel.position?.trim()) {
    window.message.warning('请填写岗位。');
    return;
  }

  saving.value = true;
  try {
    await createPersonnelArchiveRecord(formModel);
    window.message.success('人员档案已新增');
    formOpen.value = false;
    await loadPersonnelArchive();
  } finally {
    saving.value = false;
  }
}

function handleCancelForm() {
  formOpen.value = false;
}

function resetForm(record?: PersonnelArchiveRecord) {
  Object.assign(formModel, {
    age: record?.age ?? '',
    contractor: record?.contractor ?? '',
    gender: record?.gender ?? '男',
    healthStatus: record?.healthStatus ?? '正常',
    idCard: record?.idCard ?? '',
    name: record?.name ?? '',
    phone: record?.phone ?? '',
    position: record?.position ?? '',
    project: record?.project ?? '',
    qualificationCert: record?.qualificationCert ?? '',
    qualificationExpireDate: record?.qualificationExpireDate ?? '',
    startDate: record?.startDate ?? '',
  });
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key !== 'Enter' && event.key !== ' ') {
    return;
  }

  event.preventDefault();
  handleSearch();
}

onMounted(loadPersonnelArchive);
</script>

<template>
  <Page :auto-content-height="true">
    <div class="personnel-archive-page">
      <PlatformViewToolbar
        description="人员全生命周期档案与详情抽屉管理"
        title="人员档案管理"
      >
        <template #actions>
        <div class="personnel-archive-header__tools">
          <div class="personnel-archive-search">
            <PlatformInput
              v-model:value="query.keyword"
              placeholder="搜索姓名、承包商、项目、证书"
              @keydown="handleKeydown"
            >
              <template #prefix>
                <VbenIcon icon="lucide:search" />
              </template>
            </PlatformInput>
          </div>

          <PlatformButton scene="toolbar" type="primary" @click="handleAdd">
            <template #icon>
              <PlatformIcon icon="icon-xinzeng" />
            </template>
            新增人员
          </PlatformButton>
        </div>
        </template>
      </PlatformViewToolbar>

      <section class="personnel-archive-grid">
        <template v-if="loading">
          <article
            v-for="index in 12"
            :key="index"
            class="personnel-archive-skeleton"
          >
            <div class="personnel-archive-skeleton__avatar"></div>
            <div class="personnel-archive-skeleton__content">
              <div class="personnel-archive-skeleton__line personnel-archive-skeleton__line--title"></div>
              <div class="personnel-archive-skeleton__line"></div>
              <div class="personnel-archive-skeleton__chips">
                <span></span>
                <span></span>
              </div>
            </div>
          </article>
        </template>

        <template v-else>
          <PersonnelArchiveCard
            v-for="record in records"
            :key="record.id"
            :record="record"
            @click="handleOpenDetail"
          />
        </template>
      </section>

      <div v-if="!loading && cardCount === 0" class="personnel-archive-empty">
        <VbenIcon icon="lucide:inbox" />
        <p>暂无符合条件的人员档案</p>
      </div>
    </div>

    <PersonnelArchiveDetailDrawer
      v-model:open="detailOpen"
      :record="selectedRecord"
    />

    <PlatformModal
      v-model:open="formOpen"
      :confirm-loading="saving"
      destroy-on-close
      title="新增人员"
      width="1120px"
      @cancel="handleCancelForm"
      @ok="handleSave"
    >
      <PlatformEditForm
        :model="formModel"
        class="personnel-archive-form"
        layout="vertical"
      >
        <PlatformFormItem label="姓名" required>
          <PlatformInput v-model:value="formModel.name" placeholder="请输入姓名" />
        </PlatformFormItem>
        <PlatformFormItem label="性别">
          <PlatformSelect
            v-model:value="formModel.gender"
            :options="genderOptions"
            placeholder="请选择性别"
          />
        </PlatformFormItem>
        <PlatformFormItem label="年龄">
          <PlatformInput v-model:value="formModel.age" placeholder="请输入年龄" />
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
            v-model:value="formModel.healthStatus"
            :options="healthStatusOptions"
            placeholder="请选择身体状况"
          />
        </PlatformFormItem>
        <PlatformFormItem label="承包商" required>
          <PlatformInput
            v-model:value="formModel.contractor"
            placeholder="请输入承包商"
          />
        </PlatformFormItem>
        <PlatformFormItem label="所属项目">
          <PlatformSelect
            v-model:value="formModel.project"
            :options="projectOptions"
            placeholder="请选择项目"
          />
        </PlatformFormItem>
        <PlatformFormItem label="岗位" required>
          <PlatformSelect
            v-model:value="formModel.position"
            :options="positionOptions"
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
            v-model:value="formModel.qualificationCert"
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
      </PlatformEditForm>
    </PlatformModal>
  </Page>
</template>

<style scoped>
.personnel-archive-page {
  display: flex;
  min-height: 100%;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
}

.personnel-archive-header__tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.personnel-archive-search {
  width: 320px;
}

.personnel-archive-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.personnel-archive-skeleton {
  display: flex;
  min-height: 118px;
  gap: 14px;
  padding: 16px;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-card);
}

.personnel-archive-skeleton__avatar {
  width: 56px;
  height: 56px;
  flex: 0 0 56px;
  border-radius: 14px;
  background: hsl(var(--st-color-border-subtle));
}

.personnel-archive-skeleton__content {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  padding-top: 4px;
}

.personnel-archive-skeleton__line {
  height: 14px;
  border-radius: 999px;
  background: hsl(var(--st-color-border-subtle));
}

.personnel-archive-skeleton__line--title {
  width: 72%;
  height: 18px;
}

.personnel-archive-skeleton__chips {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.personnel-archive-skeleton__chips span {
  width: 82px;
  height: 24px;
  border-radius: 999px;
  background: hsl(var(--st-color-border-subtle));
}

.personnel-archive-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 240px;
  color: hsl(var(--muted-foreground));
  background: hsl(var(--background));
  border: 1px dashed hsl(var(--border));
  border-radius: var(--st-radius-card);
}

.personnel-archive-empty :deep(svg) {
  width: 28px;
  height: 28px;
}

.personnel-archive-empty p {
  margin: 0;
}

.personnel-archive-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 24px;
}

.personnel-archive-form :deep(.ant-form-item) {
  margin-bottom: 0;
}

@media (max-width: 1400px) {
  .personnel-archive-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1120px) {
  .personnel-archive-header {
    align-items: stretch;
    flex-direction: column;
  }

  .personnel-archive-header__tools {
    justify-content: space-between;
  }

  .personnel-archive-search {
    width: 100%;
  }
}

@media (max-width: 900px) {
  .personnel-archive-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .personnel-archive-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .personnel-archive-grid {
    grid-template-columns: 1fr;
  }
}
</style>
