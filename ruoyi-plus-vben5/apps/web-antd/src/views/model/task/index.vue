<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { ModelTaskRecord } from '../data';

import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Space } from 'antdv-next';

import {
  models,
  removeTask,
  runTaskNow,
  taskStatusMeta,
  tasks,
  toggleTaskStatus,
} from '../data';

import TaskConfigModal from './modal/task-config-modal.vue';
import TaskExecDrawer from './modal/task-exec-drawer.vue';

const loading = ref(false);
const toolbarKeyword = ref('');
const taskKeyword = ref('');
const route = useRoute();

const tableColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'taskName', key: 'taskName', title: '任务名称', width: 210 },
  { key: 'model', title: '关联模型', width: 220 },
  { dataIndex: 'cronDesc', key: 'cronDesc', title: '执行周期', width: 120 },
  { dataIndex: 'lastRunTime', key: 'lastRunTime', title: '最近执行', width: 170 },
  { dataIndex: 'nextExecTime', key: 'nextExecTime', title: '下次执行', width: 170 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 96 },
  { fixed: 'right', key: 'action', title: '操作', width: 330 },
]);

const filteredRows = computed(() => {
  const keyword = taskKeyword.value.trim().toLowerCase();

  return tasks.value.filter((task) => {
    if (!keyword) {
      return true;
    }
    const modelName = models.value.find(
      (model) => model.id === task.modelId,
    )?.modelName;

    return (
      task.taskName.toLowerCase().includes(keyword) ||
      (modelName ?? '').toLowerCase().includes(keyword)
    );
  });
});

const pagination = computed(() => ({
  pageSize: 10,
  showTotal: (total: number) => `共 ${total} 条`,
  total: filteredRows.value.length,
}));

function getModelName(modelId: number) {
  return models.value.find((item) => item.id === modelId)?.modelName ?? '-';
}

function handleSearch() {
  taskKeyword.value = toolbarKeyword.value;
}

function handlePauseOrResume(task: ModelTaskRecord) {
  toggleTaskStatus(task.id);
  window.message.success(
    task.status === '运行中' ? '任务已暂停' : '任务已恢复运行',
  );
}

function handleRunNow(task: ModelTaskRecord) {
  runTaskNow(task.id);
  window.message.success('已触发立即执行，可在执行记录中查看进度');
}

function handleDelete(task: ModelTaskRecord) {
  removeTask(task.id);
  window.message.success('任务已删除，不影响模型与历史结果');
}

// ==================== 配置任务 / 执行记录 ====================
const configModalRef = ref<InstanceType<typeof TaskConfigModal>>();
const execDrawerRef = ref<InstanceType<typeof TaskExecDrawer>>();

function openConfig(task: ModelTaskRecord) {
  configModalRef.value?.open(task);
}

function openExecRecords(task: ModelTaskRecord) {
  execDrawerRef.value?.open(task);
}

onMounted(() => {
  const rawKeyword = route.query.keyword;
  if (typeof rawKeyword === 'string' && rawKeyword.trim()) {
    toolbarKeyword.value = rawKeyword.trim();
    taskKeyword.value = rawKeyword.trim();
  }

  const rawTaskId = Number(route.query.taskId);
  if (Number.isFinite(rawTaskId) && rawTaskId > 0) {
    const task = tasks.value.find((item) => item.id === rawTaskId);
    if (task) {
      execDrawerRef.value?.open(task);
    }
  }
});
</script>

<template>
  <Page>
    <div class="model-task-page">
      <PlatformViewToolbar
        description="模型开启后自动生成定时任务，支持配置、启停与执行追溯"
        title="模型任务"
      />

      <section class="platform-surface model-task-surface">
        <PlatformTableToolbar
          v-model:search-value="toolbarKeyword"
          :tools="['refresh', 'setting', 'fullscreen']"
          search-placeholder="搜索任务名称 / 关联模型"
          @refresh="handleSearch"
          @search="handleSearch"
        />

        <PlatformTable
          :action-column-width="330"
          :columns="tableColumns"
          :data-source="filteredRows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'model'">
              {{ getModelName((record as ModelTaskRecord).modelId) }}
            </template>
            <template v-else-if="column.key === 'lastRunTime'">
              {{ (record as ModelTaskRecord).lastRunTime || '暂未执行' }}
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="taskStatusMeta[(record as ModelTaskRecord).status].label"
                :status="taskStatusMeta[(record as ModelTaskRecord).status].status"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <Space :size="0" wrap>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="openConfig(record as ModelTaskRecord)"
                >
                  配置
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  :title="(record as ModelTaskRecord).status === '运行中' ? '确认暂停该任务？' : '确认恢复该任务？'"
                  @confirm="handlePauseOrResume(record as ModelTaskRecord)"
                >
                  <PlatformButton scene="action" size="small" type="link">
                    {{ (record as ModelTaskRecord).status === '运行中' ? '暂停' : '恢复' }}
                  </PlatformButton>
                </Popconfirm>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="handleRunNow(record as ModelTaskRecord)"
                >
                  立即执行
                </PlatformButton>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="openExecRecords(record as ModelTaskRecord)"
                >
                  执行记录
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认删除该任务？仅解除调度，不影响模型与历史结果。"
                  @confirm="handleDelete(record as ModelTaskRecord)"
                >
                  <PlatformButton danger scene="action" size="small" type="link">
                    删除
                  </PlatformButton>
                </Popconfirm>
              </Space>
            </template>
          </template>
        </PlatformTable>
      </section>
    </div>

    <!-- 配置任务 -->
    <TaskConfigModal ref="configModalRef" />

    <!-- 执行记录 -->
    <TaskExecDrawer ref="execDrawerRef" />
  </Page>
</template>

<style scoped>
.model-task-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.model-task-surface {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  padding: var(--st-module-content-padding);
}
</style>
