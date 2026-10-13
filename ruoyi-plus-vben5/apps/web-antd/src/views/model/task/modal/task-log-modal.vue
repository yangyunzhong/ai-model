<script setup lang="ts">
import type { ExecLogDetailVO } from '#/api/model/model';

import { ref } from 'vue';

import { PlatformEmpty, PlatformModal } from '@st/platform-ui';

import { execLogDetailList } from '#/api/model';

const logOpen = ref(false);
const loading = ref(false);
const execNo = ref('');
const logLines = ref<string[]>([]);

/** 打开执行日志弹窗 */
async function open(execId: number | string, no?: string) {
  execNo.value = no || String(execId);
  logLines.value = [];
  logOpen.value = true;
  loading.value = true;
  try {
    const res = await execLogDetailList({
      execId,
      pageNum: 1,
      pageSize: 500,
    });
    const rows: ExecLogDetailVO[] = res.rows ?? [];
    logLines.value = rows
      .slice()
      .sort((a, b) => (a.seq ?? 0) - (b.seq ?? 0))
      .map((row) => {
        const time = row.logTime ? `[${row.logTime}] ` : '';
        const level = row.level || 'INFO';
        return `${time}[${level}] ${row.message ?? ''}`.trim();
      });
  } finally {
    loading.value = false;
  }
}

defineExpose({ open });
</script>

<template>
  <PlatformModal
    v-model:open="logOpen"
    :footer="null"
    destroy-on-close
    :title="`执行日志 - ${execNo}`"
    width="720px"
  >
    <pre v-if="logLines.length > 0" class="task-exec-log">{{ logLines.join('\n') }}</pre>
    <PlatformEmpty v-else :description="loading ? '日志加载中…' : '暂无日志'" />
  </PlatformModal>
</template>

<style scoped>
.task-exec-log {
  max-height: 50vh;
  margin: 0;
  padding: 12px;
  overflow: auto;
  font-size: 12px;
  line-height: 1.8;
  color: hsl(var(--foreground));
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-control);
}
</style>
