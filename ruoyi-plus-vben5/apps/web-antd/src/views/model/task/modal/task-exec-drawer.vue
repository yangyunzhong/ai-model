<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { ExecLogVO, TaskVO } from '#/api/model/model';

import { computed, ref } from 'vue';

import {
  PlatformButton,
  PlatformDrawer,
  PlatformStatusTag,
  PlatformTable,
} from '@st/platform-ui';

import { execLogList } from '#/api/model';

import TaskLogModal from './task-log-modal.vue';

const execOpen = ref(false);
const execLoading = ref(false);
const execTask = ref<null | TaskVO>(null);
const records = ref<ExecLogVO[]>([]);

/** 执行状态展示元数据（与后端 status 取值一致） */
const execStatusMeta: Record<
  string,
  { label: string; status: 'default' | 'error' | 'processing' | 'success' | 'warning' }
> = {
  运行中: { label: '运行中', status: 'processing' },
  成功: { label: '成功', status: 'success' },
  部分成功: { label: '部分成功', status: 'warning' },
  失败: { label: '失败', status: 'error' },
  超时: { label: '超时', status: 'error' },
};

const execColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'execNo', key: 'execNo', title: '批次号', width: 190 },
  { dataIndex: 'triggerType', key: 'triggerType', title: '触发方式', width: 100 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 96 },
  { dataIndex: 'startTime', key: 'startTime', title: '开始时间', width: 165 },
  { key: 'cost', title: '耗时', width: 90 },
  { key: 'deviceResult', title: '设备成功/总数', width: 130 },
  { fixed: 'right', key: 'logAction', title: '操作', width: 80 },
]);

const logModalRef = ref<InstanceType<typeof TaskLogModal>>();

function statusMetaOf(status?: string) {
  return execStatusMeta[status ?? ''] ?? { label: status ?? '-', status: 'default' as const };
}

/** 打开某任务的执行记录抽屉 */
async function open(task: TaskVO) {
  execTask.value = task;
  execOpen.value = true;
  await loadRecords();
}

async function loadRecords() {
  if (!execTask.value) {
    return;
  }
  execLoading.value = true;
  try {
    const res = await execLogList({
      pageNum: 1,
      pageSize: 100,
      taskId: execTask.value.id,
    });
    records.value = res.rows ?? [];
  } finally {
    execLoading.value = false;
  }
}

function formatCost(ms?: number) {
  if (!ms) {
    return '-';
  }
  return `${(ms / 1000).toFixed(1)}s`;
}

function openLog(record: ExecLogVO) {
  logModalRef.value?.open(record.id, record.execNo);
}

defineExpose({ open });
</script>

<template>
  <PlatformDrawer
    v-model:open="execOpen"
    destroy-on-close
    size="large"
    :title="`执行记录 - ${execTask?.taskName || execTask?.id || ''}`"
  >
    <PlatformTable
      :action-column-width="80"
      :columns="execColumns"
      :data-source="records"
      :loading="execLoading"
      :pagination="false"
      :scroll="{ x: 900 }"
      row-key="id"
      size="small"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'cost'">
          {{ formatCost((record as ExecLogVO).costMs) }}
        </template>
        <template v-else-if="column.key === 'deviceResult'">
          {{ (record as ExecLogVO).successCount ?? 0 }} / {{ (record as ExecLogVO).deviceCount ?? 0 }}
        </template>
        <template v-else-if="column.key === 'status'">
          <PlatformStatusTag
            :label="statusMetaOf((record as ExecLogVO).status).label"
            :status="statusMetaOf((record as ExecLogVO).status).status"
          />
        </template>
        <template v-else-if="column.key === 'logAction'">
          <PlatformButton
            scene="action"
            size="small"
            type="link"
            @click="openLog(record as ExecLogVO)"
          >
            日志
          </PlatformButton>
        </template>
      </template>
    </PlatformTable>
  </PlatformDrawer>

  <TaskLogModal ref="logModalRef" />
</template>
