<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { ModelExecRecord, ModelTaskRecord } from '../data';

import { computed, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformDrawer,
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Space } from 'antdv-next';

import {
  cronPresetOptions,
  execStatusMeta,
  getModelExecsOfTask,
  getModelParams,
  getExecLogLines,
  models,
  removeTask,
  runTaskNow,
  taskStatusMeta,
  tasks,
  toggleTaskStatus,
  updateTask,
} from '../data';

const loading = ref(false);
const toolbarKeyword = ref('');
const taskKeyword = ref('');

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

// ==================== 配置任务弹窗 ====================
const configOpen = ref(false);
const configSaving = ref(false);
const configForm = reactive({
  cronDesc: '每日 02:00',
  customCron: '',
  id: 0,
  remark: '',
  taskName: '',
  runParams: {} as Record<string, string>,
});
const configModel = ref<null | ModelTaskRecord>(null);

const configInputParams = computed(() =>
  configModel.value ? getModelParams(configModel.value.modelId).input : [],
);

function openConfig(task: ModelTaskRecord) {
  configModel.value = task;
  configForm.id = task.id;
  configForm.taskName = task.taskName;
  configForm.cronDesc = cronPresetOptions.some(
    (option) => option.value === task.cronDesc,
  )
    ? task.cronDesc
    : '自定义 Cron';
  configForm.customCron = cronPresetOptions.some(
    (option) => option.value === task.cronDesc,
  )
    ? ''
    : task.cronDesc;
  configForm.remark = task.remark;
  configForm.runParams = {};
  for (const param of configInputParams.value) {
    configForm.runParams[param.paramKey] = '';
  }
  configOpen.value = true;
}

function validateCron(expr: string) {
  const parts = expr.trim().split(/\s+/);

  if (parts.length !== 5) {
    return false;
  }

  const pattern = /^(\*|\d+(?:-\d+)?(?:\/\d+)?(?:,\d+(?:-\d+)?(?:\/\d+)?)*)$/;

  return parts.every((part) => pattern.test(part));
}

async function handleConfigSave() {
  if (!configForm.taskName.trim()) {
    window.message.warning('请输入任务名称');
    return;
  }

  let cronDesc = configForm.cronDesc;

  if (configForm.cronDesc === '自定义 Cron') {
    if (!validateCron(configForm.customCron)) {
      window.message.warning(
        'Cron 表达式格式不正确，需为 5 位（分 时 日 月 周），如 0 2 * * *',
      );
      return;
    }
    cronDesc = configForm.customCron.trim();
  }

  configSaving.value = true;
  try {
    updateTask(configForm.id, {
      cronDesc,
      remark: configForm.remark,
      taskName: configForm.taskName.trim(),
    });
    window.message.success('任务配置已保存');
    configOpen.value = false;
  } finally {
    configSaving.value = false;
  }
}

// ==================== 执行记录抽屉 ====================
const execOpen = ref(false);
const execTask = ref<null | ModelTaskRecord>(null);
const logOpen = ref(false);
const logLines = ref<string[]>([]);
const logExecNo = ref('');

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

function openExecRecords(task: ModelTaskRecord) {
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
  logExecNo.value = record.execNo;
  logLines.value = getExecLogLines(record.execNo);
  logOpen.value = true;
}
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
    <PlatformModal
      v-model:open="configOpen"
      :confirm-loading="configSaving"
      title="配置任务"
      destroy-on-close
      width="640px"
      @ok="handleConfigSave"
    >
      <PlatformEditForm :model="configForm" class="model-task-form" layout="vertical">
        <PlatformFormItem label="任务名称" required>
          <PlatformInput
            v-model:value="configForm.taskName"
            :maxlength="100"
            placeholder="请输入任务名称"
          />
        </PlatformFormItem>
        <PlatformFormItem label="执行周期" required>
          <PlatformSelect
            v-model:value="configForm.cronDesc"
            :options="cronPresetOptions"
            placeholder="请选择执行周期"
          />
        </PlatformFormItem>
        <PlatformFormItem
          v-if="configForm.cronDesc === '自定义 Cron'"
          label="自定义 Cron 表达式"
          required
        >
          <PlatformInput
            v-model:value="configForm.customCron"
            placeholder="5 位表达式（分 时 日 月 周），如 0 2 * * *"
          />
        </PlatformFormItem>
        <template v-if="configInputParams.length > 0">
          <PlatformFormItem
            v-for="param in configInputParams"
            :key="param.paramKey"
            :label="`${param.paramName}（${param.paramKey}）`"
          >
            <PlatformInput
              v-model:value="configForm.runParams[param.paramKey]"
              :placeholder="param.defaultValue ? `留空使用默认值 ${param.defaultValue}` : '请输入运行参数'"
            />
          </PlatformFormItem>
        </template>
        <PlatformFormItem label="备注说明">
          <PlatformInput
            v-model:value="configForm.remark"
            :maxlength="200"
            placeholder="请输入备注（≤200 字符）"
          />
        </PlatformFormItem>
      </PlatformEditForm>
    </PlatformModal>

    <!-- 执行记录 -->
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

    <!-- 日志弹窗 -->
    <PlatformModal
      v-model:open="logOpen"
      :footer="null"
      :title="`执行日志 - ${logExecNo}`"
      destroy-on-close
      width="720px"
    >
      <pre class="model-task-log">{{ logLines.join('\n') }}</pre>
    </PlatformModal>
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

.model-task-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.model-task-form > :first-child {
  grid-column: 1 / -1;
}

.model-task-log {
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

@media (max-width: 720px) {
  .model-task-form {
    grid-template-columns: 1fr;
  }
}
</style>
