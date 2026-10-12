<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type {
  CatalogVO,
  DefinitionVO,
  ExecLogVO,
  TaskForm,
  TaskVO,
} from '#/api/model/model';

import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformIcon,
  PlatformInput,
  PlatformQueryPanel,
  PlatformSelect,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Space } from 'antdv-next';

import {
  catalogList,
  definitionList,
  definitionOffline,
  definitionPublish,
  definitionRemove,
  execLogList,
  taskList,
  taskUpdate,
} from '#/api/model';

import ModelDetailDrawer from './modal/model-detail-drawer.vue';
import ModelRegisterModal from './modal/model-register-modal.vue';
import ModelResultDrawer from './modal/model-result-drawer.vue';
import ModelRunModal from './modal/model-run-modal.vue';

const route = useRoute();

const query = reactive({
  catalogId: undefined as string | number | undefined,
  keyword: '',
  status: '',
});
const loading = ref(false);
const toolbarKeyword = ref('');
const rows = ref<DefinitionVO[]>([]);
const total = ref(0);
const pageParams = reactive({ pageNum: 1, pageSize: 10 });

/** 模型状态展示元数据（与后端 status 取值一致） */
const modelStatusMeta: Record<
  string,
  { label: string; status: 'default' | 'success' | 'warning' }
> = {
  已下架: { label: '已下架', status: 'default' },
  已归档: { label: '已归档', status: 'default' },
  已发布: { label: '已发布', status: 'success' },
  草稿: { label: '草稿', status: 'warning' },
};

/** 执行状态展示元数据（对应 model_exec_log.status） */
const execStatusMeta: Record<
  string,
  { label: string; status: 'default' | 'error' | 'processing' | 'success' | 'warning' }
> = {
  成功: { label: '成功', status: 'success' },
  失败: { label: '失败', status: 'error' },
  运行中: { label: '运行中', status: 'processing' },
  部分成功: { label: '部分成功', status: 'warning' },
  超时: { label: '超时', status: 'error' },
};

const statusOptions = (['草稿', '已发布', '已下架'] as const).map((item) => ({
  label: item,
  value: item,
}));

// ==================== 下拉与映射数据 ====================
const categoryOptions = ref<{ label: string; value: string | number }[]>([]);
const catalogNameMap = ref<Record<string, string>>({});
/** 模型ID -> 定时任务（用于「开启/停止」与下架联动） */
const taskMap = ref<Record<string, TaskVO>>({});
/** 模型ID -> 最近一次执行记录（用于「最近运行」列） */
const lastRunMap = ref<Record<string, ExecLogVO>>({});

const tableColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'modelName', key: 'modelName', title: '模型名称', width: 240 },
  { key: 'category', title: '模型分类', width: 110 },
  { key: 'statPeriod', title: '统计周期（天）', width: 120 },
  { dataIndex: 'status', key: 'status', title: '模型状态', width: 100 },
  { dataIndex: 'registerTime', key: 'registerTime', title: '注册时间', width: 170 },
  { dataIndex: 'registerType', key: 'registerType', title: '注册方式', width: 110 },
  { key: 'lastRun', title: '最近运行', width: 200 },
  { fixed: 'right', key: 'action', title: '操作', width: 320 },
]);

const pagination = computed(() => ({
  current: pageParams.pageNum,
  pageSize: pageParams.pageSize,
  showTotal: (t: number) => `共 ${t} 条`,
  total: total.value,
}));

function getCategoryName(catalogId?: string | number) {
  if (catalogId === undefined || catalogId === null) {
    return '-';
  }
  return catalogNameMap.value[String(catalogId)] ?? '-';
}

function statusMetaOf(status?: string) {
  return modelStatusMeta[status ?? ''] ?? { label: status ?? '-', status: 'default' as const };
}

function lastRunOf(model: DefinitionVO) {
  return lastRunMap.value[String(model.id)];
}

function execMetaOf(status?: string) {
  return execStatusMeta[status ?? ''] ?? { label: status ?? '-', status: 'default' as const };
}

function isTaskEnabled(model: DefinitionVO) {
  return taskMap.value[String(model.id)]?.status === '运行中';
}

// ==================== 数据加载 ====================
async function loadCategories() {
  const res = await catalogList({ pageNum: 1, pageSize: 200 });
  const list: CatalogVO[] = res.rows ?? [];
  catalogNameMap.value = Object.fromEntries(list.map((item) => [String(item.id), item.catalogName]));
  categoryOptions.value = list
    .filter((item) => item.status === '启用')
    .map((item) => ({ label: item.catalogName, value: item.id }));
}

/** 拉取任务与执行记录，用于补充「开启/停止」与「最近运行」信息 */
async function loadEnrichData() {
  const [taskRes, logRes] = await Promise.all([
    taskList({ pageNum: 1, pageSize: 500 }),
    execLogList({ pageNum: 1, pageSize: 500 }),
  ]);

  const taskMapping: Record<string, TaskVO> = {};
  for (const task of taskRes.rows ?? []) {
    const key = String(task.modelId);
    // 一个模型对应一个自动任务，保留首个
    if (!taskMapping[key]) {
      taskMapping[key] = task;
    }
  }
  taskMap.value = taskMapping;

  const logMapping: Record<string, ExecLogVO> = {};
  for (const log of logRes.rows ?? []) {
    const key = String(log.modelId);
    const current = logMapping[key];
    if (!current || toTimestamp(log.startTime) > toTimestamp(current.startTime)) {
      logMapping[key] = log;
    }
  }
  lastRunMap.value = logMapping;
}

function toTimestamp(value?: string) {
  if (!value) {
    return 0;
  }
  const time = new Date(value.replace('T', ' ').replaceAll('-', '/')).getTime();
  return Number.isNaN(time) ? 0 : time;
}

async function loadList() {
  loading.value = true;
  try {
    const res = await definitionList({
      pageNum: pageParams.pageNum,
      pageSize: pageParams.pageSize,
      modelName: query.keyword.trim() || undefined,
      catalogId: query.catalogId,
      status: query.status || undefined,
    });
    rows.value = res.rows ?? [];
    total.value = res.total ?? 0;
    await loadEnrichData();
  } finally {
    loading.value = false;
  }
}

function handleTableChange(page: { current?: number; pageSize?: number }) {
  pageParams.pageNum = Number(page?.current ?? 1);
  pageParams.pageSize = Number(page?.pageSize ?? 10);
  loadList();
}

function handleReset() {
  query.catalogId = undefined;
  query.keyword = '';
  query.status = '';
  toolbarKeyword.value = '';
  pageParams.pageNum = 1;
  loadList();
}

let searchTimer: ReturnType<typeof setTimeout> | undefined;
watch(
  () => [query.catalogId, query.status, query.keyword],
  () => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
      pageParams.pageNum = 1;
      loadList();
    }, 300);
  },
);

// ==================== 请求载荷组装 ====================
function toTaskForm(task: TaskVO, patch: Partial<TaskForm> = {}): TaskForm {
  return {
    id: task.id,
    modelId: task.modelId,
    taskName: task.taskName,
    cronExpression: task.cronExpression,
    cronDescription: task.cronDescription,
    status: task.status,
    lastExecuteTime: task.lastExecuteTime,
    nextExecuteTime: task.nextExecuteTime,
    remark: task.remark,
    ...patch,
  };
}

// ==================== 注册 / 编辑模型弹窗 ====================
const registerModalRef = ref<InstanceType<typeof ModelRegisterModal>>();

function openRegister(model?: DefinitionVO) {
  registerModalRef.value?.open(model);
}

// ==================== 状态流转操作 ====================
/** 发布：按当前生效脚本创建并启动运行时容器，失败由后端返回中文提示 */
async function handlePublish(model: DefinitionVO) {
  await definitionPublish(model.id);
  await loadList();
}

/** 重新发布：已下架模型重新走发布流程 */
function handleRePublish(model: DefinitionVO) {
  return handlePublish(model);
}

/** 下架：停止并删除运行时容器，并联动暂停关联自动任务 */
async function handleOffline(model: DefinitionVO) {
  await definitionOffline(model.id);
  const task = taskMap.value[String(model.id)];
  if (task && task.status === '运行中') {
    await taskUpdate(toTaskForm(task, { status: '暂停' }));
  }
  await loadList();
}

async function handleDelete(model: DefinitionVO) {
  await definitionRemove(model.id);
  if (rows.value.length === 1 && pageParams.pageNum > 1) {
    pageParams.pageNum -= 1;
  }
  await loadList();
}

async function handleToggleEnabled(model: DefinitionVO) {
  const task = taskMap.value[String(model.id)];
  if (!task) {
    window.message.warning('该模型暂未配置自动任务，请先在模型任务中配置');
    return;
  }
  const nextStatus = task.status === '运行中' ? '暂停' : '运行中';
  await taskUpdate(toTaskForm(task, { status: nextStatus }));
  await loadList();
}

// ==================== 单次运行弹窗 ====================
const runModalRef = ref<InstanceType<typeof ModelRunModal>>();

function openRun(model: DefinitionVO) {
  runModalRef.value?.open(model);
}

// ==================== 模型详情抽屉 ====================
const detailDrawerRef = ref<InstanceType<typeof ModelDetailDrawer>>();

function openDetail(model: DefinitionVO) {
  detailDrawerRef.value?.open(model);
}

// ==================== 查看结果抽屉 ====================
const resultDrawerRef = ref<InstanceType<typeof ModelResultDrawer>>();

function openResult(model: DefinitionVO) {
  resultDrawerRef.value?.open(model);
}

onMounted(() => {
  const rawCatalogId = route.query.categoryId;
  if (rawCatalogId !== undefined && String(rawCatalogId).trim()) {
    query.catalogId = String(rawCatalogId);
  }

  const rawKeyword = route.query.keyword;
  if (typeof rawKeyword === 'string' && rawKeyword.trim()) {
    query.keyword = rawKeyword.trim();
    toolbarKeyword.value = rawKeyword.trim();
  }

  const rawStatus = route.query.status;
  if (typeof rawStatus === 'string' && rawStatus.trim()) {
    query.status = rawStatus.trim();
  }

  loadCategories();
  loadList();
});
</script>

<template>
  <Page>
    <div class="model-manage-page">
      <PlatformViewToolbar
        description="注册、发布、运行与下架模型全生命周期管理"
        title="模型管理"
      />

      <PlatformQueryPanel :columns="4" :show-query="false" @reset="handleReset">
        <PlatformSelect
          v-model:value="query.catalogId"
          :options="categoryOptions"
          allow-clear
          placeholder="请选择模型分类"
        />
        <PlatformSelect
          v-model:value="query.status"
          :options="statusOptions"
          allow-clear
          placeholder="请选择模型状态"
        />
        <PlatformInput
          v-model:value="query.keyword"
          allow-clear
          placeholder="搜索模型名称"
        />
      </PlatformQueryPanel>

      <section class="platform-surface model-manage-surface">
        <PlatformTableToolbar
          v-model:search-value="toolbarKeyword"
          :tools="['refresh', 'setting', 'fullscreen']"
          search-placeholder="搜索模型名称"
          @refresh="loadList"
          @search="() => (query.keyword = toolbarKeyword)"
        >
          <template #actions>
            <PlatformButton scene="toolbar" type="primary" @click="openRegister()">
              <template #icon>
                <PlatformIcon icon="icon-xinzeng" />
              </template>
              注册模型
            </PlatformButton>
          </template>
        </PlatformTableToolbar>

        <PlatformTable
          :action-column-width="320"
          :columns="tableColumns"
          :data-source="rows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelName'">
              <PlatformButton
                scene="action"
                size="small"
                type="link"
                @click="openDetail(record as DefinitionVO)"
              >
                {{ (record as DefinitionVO).modelName }}
              </PlatformButton>
            </template>
            <template v-else-if="column.key === 'category'">
              {{ getCategoryName((record as DefinitionVO).catalogId) }}
            </template>
            <template v-else-if="column.key === 'statPeriod'">
              {{ (record as DefinitionVO).statisticsCycleDays }} 天
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="statusMetaOf((record as DefinitionVO).status).label"
                :status="statusMetaOf((record as DefinitionVO).status).status"
              />
            </template>
            <template v-else-if="column.key === 'registerType'">
              {{ (record as DefinitionVO).registerType }}
            </template>
            <template v-else-if="column.key === 'lastRun'">
              <template v-if="lastRunOf(record as DefinitionVO)">
                <div>{{ lastRunOf(record as DefinitionVO)?.startTime }}</div>
                <PlatformStatusTag
                  :label="execMetaOf(lastRunOf(record as DefinitionVO)?.status).label"
                  :status="execMetaOf(lastRunOf(record as DefinitionVO)?.status).status"
                  variant="dot"
                />
              </template>
              <span v-else>暂无</span>
            </template>
            <template v-else-if="column.key === 'action'">
              <Space :size="0" wrap>
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="openDetail(record as DefinitionVO)"
                >
                  查看
                </PlatformButton>

                <template v-if="(record as DefinitionVO).status === '草稿'">
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="openRegister(record as DefinitionVO)"
                  >
                    编辑
                  </PlatformButton>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="handlePublish(record as DefinitionVO)"
                  >
                    发布
                  </PlatformButton>
                  <Popconfirm
                    placement="left"
                    title="确认删除该模型？"
                    @confirm="handleDelete(record as DefinitionVO)"
                  >
                    <PlatformButton danger scene="action" size="small" type="link">
                      删除
                    </PlatformButton>
                  </Popconfirm>
                </template>

                <template v-else-if="(record as DefinitionVO).status === '已发布'">
                  <Popconfirm
                    placement="left"
                    :title="isTaskEnabled(record as DefinitionVO) ? '确认停止自动任务？' : '确认开启自动任务？'"
                    @confirm="handleToggleEnabled(record as DefinitionVO)"
                  >
                    <PlatformButton scene="action" size="small" type="link">
                      {{ isTaskEnabled(record as DefinitionVO) ? '停止' : '开启' }}
                    </PlatformButton>
                  </Popconfirm>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="openRun(record as DefinitionVO)"
                  >
                    单次运行
                  </PlatformButton>
                  <Popconfirm
                    placement="left"
                    title="确认下架该模型？下架后关联任务自动暂停。"
                    @confirm="handleOffline(record as DefinitionVO)"
                  >
                    <PlatformButton scene="action" size="small" type="link">
                      下架
                    </PlatformButton>
                  </Popconfirm>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="openResult(record as DefinitionVO)"
                  >
                    查看结果
                  </PlatformButton>
                </template>

                <template v-else>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="handleRePublish(record as DefinitionVO)"
                  >
                    重新发布
                  </PlatformButton>
                  <Popconfirm
                    placement="left"
                    title="确认删除该模型？"
                    @confirm="handleDelete(record as DefinitionVO)"
                  >
                    <PlatformButton danger scene="action" size="small" type="link">
                      删除
                    </PlatformButton>
                  </Popconfirm>
                </template>
              </Space>
            </template>
          </template>
        </PlatformTable>
      </section>
    </div>

    <!-- 注册 / 编辑模型 -->
    <ModelRegisterModal ref="registerModalRef" @success="loadList" />

    <!-- 单次运行 -->
    <ModelRunModal ref="runModalRef" @success="loadList" />

    <!-- 模型详情 -->
    <ModelDetailDrawer ref="detailDrawerRef" />

    <!-- 查看结果 -->
    <ModelResultDrawer ref="resultDrawerRef" />
  </Page>
</template>

<style scoped>
.model-manage-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.model-manage-surface {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  padding: var(--st-module-content-padding);
}
</style>
