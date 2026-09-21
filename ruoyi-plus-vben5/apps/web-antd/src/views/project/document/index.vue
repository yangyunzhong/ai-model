<script setup lang="ts">
import type { PlatformFileListItem } from '@st/platform-ui';

import type {
  ProjectDocumentRecord,
  ProjectDocumentStatCard,
} from '#/mock/modules/project/document';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformFileList,
  PlatformSectionTitle,
  PlatformStatCard,
  PlatformViewToolbar,
} from '@st/platform-ui';

import {
  downloadProjectDocument,
  exportProjectDocumentLedger,
  getProjectDocumentList,
  getProjectDocumentStats,
  uploadProjectDocuments,
} from '#/mock/modules/project/document';

const documents = ref<ProjectDocumentRecord[]>([]);
const statCards = ref<ProjectDocumentStatCard[]>([]);
const loading = ref(false);
const uploading = ref(false);
const exporting = ref(false);
const downloadingId = ref<null | number | string>(null);
const fileInputRef = ref<HTMLInputElement>();
const headerActions = computed(() => [
  {
    icon: 'icon-xiazai1',
    key: 'export',
    label: '导出台账',
    loading: exporting.value,
  },
  {
    icon: 'lucide:upload',
    key: 'upload',
    label: '批量上传',
    loading: uploading.value,
    type: 'primary' as const,
  },
]);

onMounted(loadDocumentManagement);

async function loadDocumentManagement() {
  loading.value = true;
  try {
    const [rows, stats] = await Promise.all([
      getProjectDocumentList(),
      getProjectDocumentStats(),
    ]);
    documents.value = rows;
    statCards.value = stats;
  } finally {
    loading.value = false;
  }
}

async function handleDownload(item: PlatformFileListItem) {
  downloadingId.value = item.id;
  try {
    const document = await downloadProjectDocument(item.id);
    window.message.success(`已下载 ${document?.name || item.name}`);
  } finally {
    downloadingId.value = null;
  }
}

async function handleExportLedger() {
  exporting.value = true;
  try {
    const result = await exportProjectDocumentLedger();
    window.message.success(
      `已导出 ${result.fileName}，共 ${result.total} 条文档记录`,
    );
  } finally {
    exporting.value = false;
  }
}

function handleUploadClick() {
  fileInputRef.value?.click();
}

function handleHeaderAction(key: string) {
  if (key === 'export') {
    void handleExportLedger();
  }

  if (key === 'upload') {
    handleUploadClick();
  }
}

async function handleUploadChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const files = [...(input.files || [])];

  if (files.length === 0) {
    return;
  }

  uploading.value = true;
  try {
    const uploadedRows = await uploadProjectDocuments(files);
    window.message.success(`已上传 ${uploadedRows.length} 个文件`);
    await loadDocumentManagement();
  } finally {
    uploading.value = false;
    input.value = '';
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="project-document-page">
      <PlatformViewToolbar
        :actions="headerActions"
        description="合同、技术方案、验收报告等文档分类管理"
        title="文档与台账管理"
        @action="handleHeaderAction"
      />
      <input
        ref="fileInputRef"
        class="project-document-upload-input"
        multiple
        type="file"
        @change="handleUploadChange"
      />

      <section class="project-document-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :loading="loading"
          v-bind="card"
        />
      </section>

      <section class="platform-surface project-document-panel">
        <PlatformSectionTitle
          class="project-document-panel__header"
          title="文档列表"
        />

        <PlatformFileList
          :columns="4"
          :downloading-id="downloadingId"
          empty-text="暂无文档"
          :items="documents"
          :loading="loading"
          @download="handleDownload"
        />
      </section>
    </div>
  </Page>
</template>

<style scoped>
.project-document-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.project-document-upload-input {
  display: none;
}

.project-document-stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.project-document-panel {
  display: flex;
  min-height: 0;
  flex: 1;
  flex-direction: column;
}

.project-document-panel__header {
  margin-bottom: 20px;
}

@media (max-width: 1280px) {
  .project-document-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .project-document-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
