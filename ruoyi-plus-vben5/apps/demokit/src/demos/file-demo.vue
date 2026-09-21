<script setup lang="ts">
import type { PlatformFileListItem } from '@st/platform-ui';

import { ref } from 'vue';

import {
  PlatformFileItem,
  PlatformFileList,
  PlatformSectionTitle,
  PlatformStatCard,
  PlatformViewToolbar,
} from '@st/platform-ui';

const downloadingId = ref<null | number>(null);

const files: PlatformFileListItem[] = [
  {
    date: '2026-05-28',
    id: 1,
    name: '项目验收报告.pdf',
    projectName: '华东治理专项',
    size: '2.4 MB',
    type: 'pdf',
  },
  {
    date: '2026-05-26',
    id: 2,
    name: '施工计划表.xlsx',
    projectName: '智慧工厂建设',
    size: '860 KB',
    type: 'xls',
  },
  {
    date: '2026-05-22',
    id: 3,
    name: '月度汇报.docx',
    projectName: '经营分析中台',
    size: '1.1 MB',
    type: 'doc',
  },
  {
    date: '2026-05-20',
    id: 4,
    name: '安全检查清单.zip',
    projectName: '园区升级工程',
    size: '4.8 MB',
    type: 'other',
  },
];

const statCards = [
  { title: '文档总数', value: '128', trendText: '本月新增 18 份', trendType: 'up' },
  { title: '待归档', value: '14', trendText: '较昨日 -3', trendType: 'down' },
  { title: '临期文档', value: '7', trendText: '需要尽快处理', trendType: 'up' },
  { title: '本周下载', value: '42', trendText: '下载频次提升', trendType: 'up' },
] as const;

function handleDownload(item: PlatformFileListItem) {
  downloadingId.value = Number(item.id);
  window.setTimeout(() => {
    downloadingId.value = null;
  }, 600);
}

const props = defineProps<{
  demoId?: string;
}>();
</script>

<template>
  <div class="demo-flow">
    <div v-if="props.demoId !== 'platform-file-list'" class="demo-grid-2">
      <div class="demo-surface">
        <PlatformFileItem
          :downloading="downloadingId === 1"
          :item="files[0]!"
          @download="handleDownload"
        />
      </div>

      <div class="demo-surface demo-flow">
        <h3 class="demo-title">文件项说明</h3>
        <p class="demo-muted">
          单文件卡片适合放在详情页附件区；多文件场景优先使用 `PlatformFileList` 做统一编排。
        </p>
      </div>
    </div>

    <div v-if="props.demoId !== 'platform-file-item'" class="demo-surface">
      <div class="demo-flow">
        <PlatformViewToolbar
          :actions="[{ key: 'upload', label: '上传文件', type: 'primary' }]"
          description="合同、技术方案、验收报告等文档分类管理"
          title="文档与台账管理"
        />

        <div class="demo-stat-grid">
          <PlatformStatCard v-for="card in statCards" :key="card.title" v-bind="card" />
        </div>

        <div class="demo-surface demo-flow">
          <PlatformSectionTitle title="文档列表" />
          <PlatformFileList
            :columns="4"
            :downloading-id="downloadingId"
            :items="files"
            @download="handleDownload"
          />
        </div>
      </div>
    </div>
  </div>
</template>
