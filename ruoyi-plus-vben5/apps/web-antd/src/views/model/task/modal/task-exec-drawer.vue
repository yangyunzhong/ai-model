<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { ModelExecRecord, ModelTaskRecord } from '../../data';

import { computed, ref } from 'vue';

import {
  PlatformButton,
  PlatformDrawer,
  PlatformStatusTag,
  PlatformTable,
} from '@st/platform-ui';

import { execStatusMeta, getModelExecsOfTask } from '../../data';

import TaskLogModal from './task-log-modal.vue';

const execOpen = ref(false);
const execTask = ref<null | ModelTaskRecord>(null);

const execColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'execNo', key: 'execNo', title: '批次号', width: 190 },
  { dataIndex: 'triggerType', key: 'triggerType', title: '触发方式', width: 100 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 96 },
  { dataIndex: 'startTime', key: 'startTime', title: '开始时间', width: 165 },
  { key: 'cost', title: '耗时', width: 90 },
  { dataIndex: 'deviceCount', key: 'deviceCount', title: '设备数', width: 80 },
  { fixed: 'right', key: 'logAction', title: '操作', width: 80 },
]);

const taskExecRecords = computed<ModelExecRecord[]>(() =>
  execTask.value ? getModelExecsOfTask(execTask.value.id) : [],
);

const logModalRef = ref<InstanceType<typeof TaskLogModal>>();

/** 打开某任务的执行记录抽屉 */
function open(task: ModelTaskRecord) {
  execTask.value = task;
  execOpen.value = true;
}

function formatCost(ms: number) {
  if (!ms) {
    return '-';
  }
  return `${(ms / 1000).toFixed(1)}s`;
}

function openLog(record: ModelExecRecord) {
  logModalRef.value?.open(record.execNo);
}

defineExpose({ open });
</script>

<template>
  <PlatformDrawer
    v-model:open="execOpen"
    destroy-on-close
    size="large"
    :title="`执行记录 - ${execTask?.taskName ?? ''}`"
  >
    <PlatformTable
      :action-column-width="80"
      :columns="execColumns"
      :data-source="taskExecRecords"
      :pagination="false"
      :scroll="{ x: 900 }"
      row-key="execNo"
      size="small"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'cost'">
          {{ formatCost((record as ModelExecRecord).costMs) }}
        </template>
        <template v-else-if="column.key === 'status'">
          <PlatformStatusTag
            :label="execStatusMeta[(record as ModelExecRecord).status].label"
            :status="execStatusMeta[(record as ModelExecRecord).status].status"
          />
        </template>
        <template v-else-if="column.key === 'logAction'">
          <PlatformButton
            scene="action"
            size="small"
            type="link"
            @click="openLog(record as ModelExecRecord)"
          >
            日志
          </PlatformButton>
        </template>
      </template>
    </PlatformTable>
  </PlatformDrawer>

  <TaskLogModal ref="logModalRef" />
</template>
