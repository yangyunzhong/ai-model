<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { DocumentListRecord } from '#/mock/modules/battery/archive/document-list';

import { computed, reactive, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformSectionTitle,
  PlatformTable,
  PlatformTreePanel,
} from '@st/platform-ui';

import {
  collectTreeDescendantKeys,
  documentListRecords,
  documentListTreeData,
  filterDocumentTreeNodes,
} from '#/mock/modules/battery/archive/document-list';

const treeSearchValue = ref('');
const treeKeyword = ref('');
const selectedTreeKeys = ref<string[]>(['standard-file']);
const selectedRowKeys = ref<number[]>([]);
const saving = ref(false);
const drafting = ref(false);
const fileInputRef = ref<HTMLInputElement>();

const maintenanceForm = reactive({
  createdAt: '无',
  createdUser: '无',
  updatedAt: '无',
  updatedUser: '无',
});

const treeData = computed(() =>
  filterDocumentTreeNodes(documentListTreeData, treeKeyword.value),
);
const activeTreeKey = computed(() => selectedTreeKeys.value[0] ?? '');
const visibleTreeKeys = computed(() => {
  if (!activeTreeKey.value) {
    return documentListRecords.map((item) => item.treeKey);
  }

  const descendantKeys = collectTreeDescendantKeys(
    documentListTreeData,
    activeTreeKey.value,
  );

  return descendantKeys.length > 0
    ? descendantKeys
    : documentListRecords.map((item) => item.treeKey);
});
const tableData = computed(() =>
  documentListRecords.filter((item) => visibleTreeKeys.value.includes(item.treeKey)),
);
const selectedRecord = computed(() =>
  tableData.value.find((item) => item.id === selectedRowKeys.value[0]),
);
const tableColumns = computed<TableProps['columns']>(() => [
  {
    dataIndex: 'name',
    key: 'name',
    title: '文件名',
    width: 320,
  },
  {
    dataIndex: 'size',
    key: 'size',
    title: '文件大小',
    width: 130,
  },
  {
    dataIndex: 'uploadTime',
    key: 'uploadTime',
    title: '上传时间',
    width: 180,
  },
  {
    dataIndex: 'uploader',
    key: 'uploader',
    title: '上传人',
    width: 140,
  },
]);
const canEditMaintenance = computed(() => Boolean(selectedRecord.value));

watch(
  selectedRecord,
  (record) => {
    syncMaintenanceForm(record);
  },
  { immediate: true },
);

watch(activeTreeKey, () => {
  selectedRowKeys.value = [];
  syncMaintenanceForm();
});

function handleTreeSelect(keys: Array<number | string>) {
  selectedTreeKeys.value = keys.length > 0 ? [String(keys[0])] : [];
}

function handleTreeSearch(value: string) {
  treeKeyword.value = value.trim();
}

function handleRowSelectionChange(keys: Array<number | string>) {
  selectedRowKeys.value = keys.length > 0 ? [Number(keys[0])] : [];
}

function handleUploadClick() {
  fileInputRef.value?.click();
}

function handleDraft() {
  if (!selectedRecord.value) {
    window.message.warning('请先选择一条文档记录。');
    return;
  }

  drafting.value = true;
  window.message.success(`已暂存 ${selectedRecord.value.name}`);
  drafting.value = false;
}

function handleSave() {
  if (!selectedRecord.value) {
    window.message.warning('请先选择一条文档记录。');
    return;
  }

  saving.value = true;
  window.message.success(`已保存 ${selectedRecord.value.name} 的维护信息`);
  saving.value = false;
}

function syncMaintenanceForm(record?: DocumentListRecord) {
  if (!record) {
    maintenanceForm.createdAt = '无';
    maintenanceForm.createdUser = '无';
    maintenanceForm.updatedAt = '无';
    maintenanceForm.updatedUser = '无';
    return;
  }

  maintenanceForm.createdAt = record.createdAt;
  maintenanceForm.createdUser = record.createdUser;
  maintenanceForm.updatedAt = record.updatedAt;
  maintenanceForm.updatedUser = record.updatedUser;
}

function handleUploadChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const files = [...(input.files || [])];

  if (files.length === 0) {
    return;
  }

  window.message.success(`已选择 ${files.length} 个文件`);
  input.value = '';
}

</script>

<template>
  <Page :auto-content-height="true">
    <div class="document-list-page">
      <aside class="document-list-page__tree">
        <PlatformTreePanel
          v-model:search-value="treeSearchValue"
          v-model:selected-keys="selectedTreeKeys"
          :default-expand-all="true"
          :loading="false"
          search-placeholder="请输入文档类型/名称"
          :show-refresh="false"
          :tree-data="treeData"
          :virtual="false"
          empty-description="无文档目录"
          show-line
          @search="handleTreeSearch"
          @select="handleTreeSelect"
        />
      </aside>

      <section class="document-list-page__content platform-surface">
        <input
          ref="fileInputRef"
          class="document-list-page__upload-input"
          multiple
          type="file"
          @change="handleUploadChange"
        />

        <PlatformSectionTitle title="文档列表">
          <template #extra>
            <PlatformButton
              ghost
              type="primary"
              @click="handleUploadClick"
            >
              <template #icon>
                <span class="i-lucide-upload text-[14px]"></span>
              </template>
              上传文件
            </PlatformButton>
          </template>
        </PlatformSectionTitle>

        <div class="document-list-page__table-wrap">
          <PlatformTable
            row-key="id"
            :columns="tableColumns"
            :data-source="tableData"
            :pagination="{ pageSize: 4, showTotal: (total) => `共 ${total} 条` }"
            :row-selection="{
              type: 'radio',
              selectedRowKeys,
              onChange: handleRowSelectionChange,
            }"
          />
        </div>

        <section class="document-list-page__maintenance">
          <PlatformSectionTitle
            class="document-list-page__section-title"
            title="维护信息"
          />

          <PlatformEditForm
            :colon="false"
            class="document-list-page__form"
            label-preset="inline-compact"
            layout="horizontal"
          >
            <div class="document-list-page__form-grid">
              <PlatformFormItem label="建立用户">
                <PlatformInput
                  v-model:value="maintenanceForm.createdUser"
                  :disabled="!canEditMaintenance"
                  placeholder="请输入"
                />
              </PlatformFormItem>
              <PlatformFormItem label="建立时间">
                <PlatformInput
                  v-model:value="maintenanceForm.createdAt"
                  :disabled="!canEditMaintenance"
                  placeholder="请输入"
                />
              </PlatformFormItem>
              <PlatformFormItem label="更新用户">
                <PlatformInput
                  v-model:value="maintenanceForm.updatedUser"
                  :disabled="!canEditMaintenance"
                  placeholder="请输入"
                />
              </PlatformFormItem>
              <PlatformFormItem label="更新时间">
                <PlatformInput
                  v-model:value="maintenanceForm.updatedAt"
                  :disabled="!canEditMaintenance"
                  placeholder="请输入"
                />
              </PlatformFormItem>
            </div>
          </PlatformEditForm>
        </section>

        <div class="document-list-page__footer">
          <PlatformButton
            :disabled="!selectedRecord || drafting"
            @click="handleDraft"
          >
            暂存
          </PlatformButton>
          <PlatformButton
            :disabled="!selectedRecord || saving"
            type="primary"
            @click="handleSave"
          >
            保存
          </PlatformButton>
        </div>
      </section>
    </div>
  </Page>
</template>

<style scoped>
.document-list-page {
  display: flex;
  gap: var(--st-layout-section-gap);
  align-items: flex-start;
  min-height: calc(100vh - 112px);
}

.document-list-page__tree {
  width: 320px;
  flex: 0 0 320px;
  max-height: calc(100vh - 112px);
}

.document-list-page__content {
  display: flex;
  min-width: 0;
  flex: 1;
  min-height: calc(100vh - 112px);
  flex-direction: column;
  gap: 20px;
  padding: 24px;
}

.document-list-page__upload-input {
  display: none;
}

.document-list-page__table-wrap {
  min-width: 0;
}

.document-list-page__maintenance {
  display: flex;
  min-height: 0;
  flex-direction: column;
  gap: 18px;
}

.document-list-page__form {
  width: 100%;
}

.document-list-page__section-title {
  margin-bottom: 2px;
}

.document-list-page__form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 4px 32px;
}

.document-list-page__footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: auto;
  padding: 16px 0 0;
  border-top: 1px solid hsl(var(--st-color-border-subtle));
}

:deep(.document-list-page__form .ant-form-item) {
  margin-bottom: 14px;
}

:deep(.document-list-page__tree .platform-tree-panel__surface) {
  max-height: calc(100vh - 112px);
}

@media (max-width: 1440px) {
  .document-list-page {
    flex-direction: column;
  }

  .document-list-page__tree {
    width: 100%;
    flex-basis: auto;
    max-height: none;
  }

  .document-list-page__content {
    min-height: 0;
  }

  :deep(.document-list-page__tree .platform-tree-panel__surface) {
    max-height: none;
  }
}

@media (max-width: 960px) {
  .document-list-page__form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
