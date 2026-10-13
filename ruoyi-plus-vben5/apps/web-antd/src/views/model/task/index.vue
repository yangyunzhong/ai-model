<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { DefinitionVO, ExecLogVO, TaskVO } from '#/api/model/model';

import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Space, Tooltip } from 'antdv-next';

import {
  definitionList,
  execLogList,
  taskInfo,
  taskList,
  taskRemove,
  taskRunNow,
  taskToggleStatus,
} from '#/api/model';

import TaskConfigModal from './modal/task-config-modal.vue';
import TaskExecDrawer from './modal/task-exec-drawer.vue';

const loading = ref(false);
const toolbarKeyword = ref('');
const taskKeyword = ref('');
const route = useRoute();

const rows = ref<TaskVO[]>([]);
const total = ref(0);
const pageParams = reactive({ pageNum: 1, pageSize: 10 });

/** 模型ID -> 模型名称 */
const modelNameMap = ref<Record<string, string>>({});
/** 模型ID -> 最近一次执行记录（含模型管理页「单次运行」产生的结果） */
const lastRunMap = ref<Record<string, ExecLogVO>>({});

/** 任务状态展示元数据（与后端 status 取值一致：暂停 / 运行中） */
const taskStatusMeta: Record<
  string,
  { label: string; status: 'default' | 'processing' }
> = {
  暂停: { label: '暂停', status: 'default' },
  运行中: { label: '运行中', status: 'processing' },
};

const tableColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'taskName', key: 'taskName', title: '任务名称', width: 210 },
  { key: 'model', title: '关联模型', width: 220 },
  { key: 'cronDescription', title: '执行周期', width: 120 },
  { key: 'lastRunTime', title: '最近执行', width: 170 },
  { key: 'nextExecuteTime', title: '下次执行', width: 170 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 96 },
  { fixed: 'right', key: 'action', title: '操作', width: 330 },
]);

const pagination = computed(() => ({
  current: pageParams.pageNum,
  pageSize: pageParams.pageSize,
  showTotal: (t: number) => `共 ${t} 条`,
  total: total.value,
}));

function statusMetaOf(status?: string) {
  return taskStatusMeta[status ?? ''] ?? { label: status ?? '-', status: 'default' as const };
}

/** 任务是否已配置执行周期（周期任务看 cron，一次性任务看执行时间） */
function hasSchedule(task: TaskVO) {
  if (task.scheduleType === 'once') {
    return Boolean(task.nextExecuteTime);
  }
  return Boolean(task.cronExpression?.trim());
}

function getModelName(modelId?: string | number) {
  if (modelId === undefined || modelId === null) {
    return '-';
  }
  return modelNameMap.value[String(modelId)] ?? '-';
}

/** 最近执行：优先与该模型的最新执行记录关联（含「单次运行」/「立即执行」/「定时」） */
function lastRunOf(task: TaskVO) {
  return lastRunMap.value[String(task.modelId)]?.startTime || task.lastExecuteTime || '';
}

// ==================== 数据加载 ====================
async function loadModels() {
  const res = await definitionList({ pageNum: 1, pageSize: 500 });
  const list: DefinitionVO[] = res.rows ?? [];
  modelNameMap.value = Object.fromEntries(
    list.map((item) => [String(item.id), item.modelName]),
  );
}

function toTimestamp(value?: string) {
  if (!value) {
    return 0;
  }
  const time = new Date(value.replace('T', ' ').replaceAll('-', '/')).getTime();
  return Number.isNaN(time) ? 0 : time;
}

/** 拉取执行记录，按模型取最新一条（与模型管理页「最近运行」口径保持一致） */
async function loadExecLogs() {
  const res = await execLogList({ pageNum: 1, pageSize: 500 });
  const mapping: Record<string, ExecLogVO> = {};
  for (const log of res.rows ?? []) {
    const key = String(log.modelId);
    const current = mapping[key];
    if (!current || toTimestamp(log.startTime) > toTimestamp(current.startTime)) {
      mapping[key] = log;
    }
  }
  lastRunMap.value = mapping;
}

async function loadList() {
  loading.value = true;
  try {
    const res = await taskList({
      pageNum: pageParams.pageNum,
      pageSize: pageParams.pageSize,
      taskName: taskKeyword.value.trim() || undefined,
    });
    rows.value = res.rows ?? [];
    total.value = res.total ?? 0;
    await loadExecLogs();
  } finally {
    loading.value = false;
  }
}

function handleTableChange(page: { current?: number; pageSize?: number }) {
  pageParams.pageNum = Number(page?.current ?? 1);
  pageParams.pageSize = Number(page?.pageSize ?? 10);
  loadList();
}

function handleSearch() {
  taskKeyword.value = toolbarKeyword.value;
  pageParams.pageNum = 1;
  loadList();
}

// ==================== 操作 ====================
async function handlePauseOrResume(task: TaskVO) {
  await taskToggleStatus(task.id);
  await loadList();
}

async function handleRunNow(task: TaskVO) {
  await taskRunNow(task.id);
  await loadList();
}

async function handleDelete(task: TaskVO) {
  await taskRemove(task.id);
  if (rows.value.length === 1 && pageParams.pageNum > 1) {
    pageParams.pageNum -= 1;
  }
  await loadList();
}

// ==================== 配置任务 / 执行记录 ====================
const configModalRef = ref<InstanceType<typeof TaskConfigModal>>();
const execDrawerRef = ref<InstanceType<typeof TaskExecDrawer>>();

function openConfig(task: TaskVO) {
  configModalRef.value?.open(task, getModelName(task.modelId));
}

function openExecRecords(task: TaskVO) {
  execDrawerRef.value?.open(task);
}

onMounted(async () => {
  const rawKeyword = route.query.keyword;
  if (typeof rawKeyword === 'string' && rawKeyword.trim()) {
    toolbarKeyword.value = rawKeyword.trim();
    taskKeyword.value = rawKeyword.trim();
  }

  await Promise.all([loadModels(), loadList()]);

  const rawTaskId = route.query.taskId;
  if (rawTaskId !== undefined && String(rawTaskId).trim()) {
    try {
      const task = await taskInfo(String(rawTaskId));
      if (task) {
        execDrawerRef.value?.open(task);
      }
    } catch {
      // 任务已被删除等场景静默忽略
    }
  }
});
</script>

<template>
  <Page>
    <div class="model-task-page">
      <PlatformViewToolbar
        description="模型注册后自动创建初始任务，配置周期后可开启定时执行与执行追溯"
        title="模型任务"
      />

      <section class="platform-surface model-task-surface">
        <PlatformTableToolbar
          v-model:search-value="toolbarKeyword"
          :tools="['refresh', 'setting', 'fullscreen']"
          search-placeholder="搜索任务名称"
          @refresh="loadList"
          @search="handleSearch"
        />

        <PlatformTable
          :action-column-width="330"
          :columns="tableColumns"
          :data-source="rows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'model'">
              {{ getModelName((record as TaskVO).modelId) }}
            </template>
            <template v-else-if="column.key === 'cronDescription'">
              {{ (record as TaskVO).cronDescription || '待配置' }}
            </template>
            <template v-else-if="column.key === 'lastRunTime'">
              {{ lastRunOf(record as TaskVO) || '暂未执行' }}
            </template>
            <template v-else-if="column.key === 'nextExecuteTime'">
              {{ (record as TaskVO).nextExecuteTime || '待启用' }}
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="statusMetaOf((record as TaskVO).status).label"
                :status="statusMetaOf((record as TaskVO).status).status"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <Space :size="0" wrap>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="openConfig(record as TaskVO)"
                >
                  配置
                </PlatformButton>
                <Tooltip
                  :title="(record as TaskVO).status !== '运行中' && !hasSchedule(record as TaskVO)
                    ? '请先配置执行周期，再开启任务'
                    : ''"
                >
                  <Popconfirm
                    v-if="(record as TaskVO).status === '运行中' || hasSchedule(record as TaskVO)"
                    placement="left"
                    :title="(record as TaskVO).status === '运行中' ? '确认暂停该任务？' : '确认开启该任务？'"
                    @confirm="handlePauseOrResume(record as TaskVO)"
                  >
                    <PlatformButton scene="action" size="small" type="link">
                      {{ (record as TaskVO).status === '运行中' ? '暂停' : '开启' }}
                    </PlatformButton>
                  </Popconfirm>
                  <PlatformButton
                    v-else
                    disabled
                    scene="action"
                    size="small"
                    type="link"
                  >
                    开启
                  </PlatformButton>
                </Tooltip>
                <Tooltip
                  :title="!hasSchedule(record as TaskVO) ? '请先配置执行周期，再立即执行' : ''"
                >
                  <Popconfirm
                    v-if="hasSchedule(record as TaskVO)"
                    placement="left"
                    title="确认立即执行该任务？将对关联模型适用品种下的全部设备计算一次。"
                    @confirm="handleRunNow(record as TaskVO)"
                  >
                    <PlatformButton scene="action" size="small" type="link">
                      立即执行
                    </PlatformButton>
                  </Popconfirm>
                  <PlatformButton v-else disabled scene="action" size="small" type="link">
                    立即执行
                  </PlatformButton>
                </Tooltip>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="openExecRecords(record as TaskVO)"
                >
                  执行记录
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认删除该任务？仅解除调度，不影响模型与历史结果。"
                  @confirm="handleDelete(record as TaskVO)"
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
    <TaskConfigModal ref="configModalRef" @success="loadList" />

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
