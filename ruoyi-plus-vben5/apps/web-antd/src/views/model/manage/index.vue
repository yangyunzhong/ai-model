<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type {
  ModelParamType,
  ModelRecord,
  RegisterType,
} from '../data';

import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformDescriptions,
  PlatformDrawer,
  PlatformEditForm,
  PlatformFileList,
  PlatformFormItem,
  PlatformIcon,
  PlatformInput,
  PlatformModal,
  PlatformQueryPanel,
  PlatformSectionTitle,
  PlatformSegmented,
  PlatformSelect,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Checkbox, Popconfirm, Space } from 'antdv-next';

import {
  addExecRecord,
  categories,
  deleteModel,
  execStatusMeta,
  getDevicesByType,
  getCategoryDeviceType,
  getModelParams,
  getModelResultRows,
  getTaskByModelId,
  healthLevelMeta,
  modelStatusMeta,
  models,
  offlineModel,
  publishModel,
  registerTypeOptions,
  saveModelDraft,
  setModelParams,
  statPeriodOptions,
  toggleModelEnabled,
} from '../data';

interface ParamRow {
  paramKey: string;
  paramName: string;
  paramType: ModelParamType;
  required: boolean;
  defaultValue: string;
  description: string;
}

interface ScriptFileItem {
  date: string;
  description: string;
  id: string;
  name: string;
  size: string;
  type: 'other';
}

const route = useRoute();

const query = reactive({
  categoryId: undefined as undefined | number,
  keyword: '',
  status: '',
});
const loading = ref(false);
const toolbarKeyword = ref('');

const categoryOptions = computed(() =>
  categories.value
    .filter((item) => item.status === '启用')
    .map((item) => ({ label: item.categoryName, value: item.id })),
);
const statusOptions = (['草稿', '已发布', '已下架'] as const).map((item) => ({
  label: item,
  value: item,
}));
const paramTypeOptions: { label: ModelParamType; value: ModelParamType }[] = [
  { label: '数值', value: '数值' },
  { label: '文本', value: '文本' },
  { label: '布尔', value: '布尔' },
];

const tableColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'modelName', key: 'modelName', title: '模型名称', width: 240 },
  { dataIndex: 'categoryId', key: 'category', title: '模型分类', width: 110 },
  { dataIndex: 'statPeriod', key: 'statPeriod', title: '统计周期（天）', width: 120 },
  { dataIndex: 'status', key: 'status', title: '模型状态', width: 100 },
  { dataIndex: 'registerTime', key: 'registerTime', title: '注册时间', width: 170 },
  { dataIndex: 'registerType', key: 'registerType', title: '注册方式', width: 110 },
  { key: 'lastRun', title: '最近运行', width: 200 },
  { fixed: 'right', key: 'action', title: '操作', width: 320 },
]);

const filteredRows = computed(() => {
  const keyword = query.keyword.trim().toLowerCase();

  return models.value.filter((model) => {
    if (query.categoryId !== undefined && model.categoryId !== query.categoryId) {
      return false;
    }
    if (query.status && model.status !== query.status) {
      return false;
    }
    if (keyword && !model.modelName.toLowerCase().includes(keyword)) {
      return false;
    }
    return true;
  });
});

const pagination = computed(() => ({
  pageSize: 10,
  showTotal: (total: number) => `共 ${total} 条`,
  total: filteredRows.value.length,
}));

function getCategoryName(categoryId: number) {
  return categories.value.find((item) => item.id === categoryId)?.categoryName ?? '-';
}

function handleReset() {
  query.categoryId = undefined;
  query.keyword = '';
  query.status = '';
}

// ==================== 注册 / 编辑模型弹窗 ====================
const registerOpen = ref(false);
const registerSaving = ref(false);
const formModel = reactive<{
  categoryId: undefined | number;
  description: string;
  id: undefined | number;
  modelName: string;
  registerType: RegisterType;
  statPeriod: string;
}>({
  categoryId: undefined,
  description: '',
  id: undefined,
  modelName: '',
  registerType: '本地注册',
  statPeriod: '90',
});
const inputRows = ref<ParamRow[]>([]);
const outputRows = ref<ParamRow[]>([]);
const scriptFiles = ref<ScriptFileItem[]>([]);

const registerTitle = computed(() =>
  formModel.id ? '编辑模型' : '注册模型',
);

function emptyParamRow(): ParamRow {
  return {
    defaultValue: '',
    description: '',
    paramKey: '',
    paramName: '',
    paramType: '数值',
    required: true,
  };
}

function openRegister(model?: ModelRecord) {
  formModel.id = model?.id;
  formModel.modelName = model?.modelName ?? '';
  formModel.categoryId = model?.categoryId;
  formModel.registerType = model?.registerType ?? '本地注册';
  formModel.statPeriod = String(model?.statPeriod ?? 90);
  formModel.description = model?.description ?? '';
  const saved = model ? getModelParams(model.id) : { input: [], output: [] };
  inputRows.value = saved.input.map((item) => ({ ...item }));
  outputRows.value = saved.output.map((item) => ({ ...item, required: false }));
  scriptFiles.value = [
    {
      date: '2026-09-22 09:00',
      description: '入口函数 execute 校验通过',
      id: 'demo-py',
      name: 'demo.py',
      size: '12.6 KB',
      type: 'other',
    },
  ];
  registerOpen.value = true;
}

function handleUploadScript() {
  window.message.success('脚本上传成功（演示：已覆盖为 demo.py，校验通过）');
}

function handleUploadRequirements() {
  if (scriptFiles.value.length > 1) {
    window.message.info('requirements.txt 已上传');
    return;
  }
  scriptFiles.value.push({
    date: '2026-09-22 09:01',
    description: '第三方依赖声明（可选）',
    id: 'requirements-txt',
    name: 'requirements.txt',
    size: '1.2 KB',
    type: 'other',
  });
  window.message.success('requirements.txt 上传成功');
}

function validateRegisterForm() {
  if (!formModel.modelName.trim()) {
    window.message.warning('请输入模型名称');
    return false;
  }
  const duplicated = models.value.some(
    (item) =>
      item.id !== formModel.id &&
      item.modelName === formModel.modelName.trim(),
  );
  if (duplicated) {
    window.message.error('模型名称已存在，请修改');
    return false;
  }
  if (formModel.categoryId === undefined) {
    window.message.warning('请选择模型分类');
    return false;
  }
  return true;
}

function buildPayload() {
  return {
    categoryId: formModel.categoryId as number,
    description: formModel.description,
    id: formModel.id,
    inputParams: inputRows.value.filter((row) => row.paramKey.trim()),
    modelName: formModel.modelName.trim(),
    outputParams: outputRows.value.filter((row) => row.paramKey.trim()),
    registerType: formModel.registerType,
    statPeriod: Number(formModel.statPeriod),
  };
}

function persistModel() {
  const payload = buildPayload();
  const modelId = saveModelDraft(payload);
  setModelParams(modelId, {
    input: payload.inputParams,
    output: payload.outputParams,
  });
  return modelId;
}

async function handleSaveDraft() {
  if (!validateRegisterForm()) {
    return;
  }
  registerSaving.value = true;
  try {
    persistModel();
    window.message.success('已保存为草稿');
    registerOpen.value = false;
  } finally {
    registerSaving.value = false;
  }
}

async function handleSaveAndEnable() {
  if (!validateRegisterForm()) {
    return;
  }
  registerSaving.value = true;
  try {
    const modelId = persistModel();
    publishModel(modelId);
    window.message.success('模型已发布，定时任务已开启（每日 02:00）');
    registerOpen.value = false;
  } finally {
    registerSaving.value = false;
  }
}

// ==================== 状态流转操作 ====================
function handlePublish(model: ModelRecord) {
  publishModel(model.id);
  window.message.success('模型已发布');
}

function handleRePublish(model: ModelRecord) {
  publishModel(model.id);
  window.message.success('模型已重新发布');
}

function handleOffline(model: ModelRecord) {
  offlineModel(model.id);
  window.message.success('模型已下架，关联任务已暂停');
}

function handleDelete(model: ModelRecord) {
  deleteModel(model.id);
  window.message.success('模型已删除');
}

function handleToggleEnabled(model: ModelRecord) {
  const next = !model.enabled;
  toggleModelEnabled(model.id, next);
  window.message.success(next ? '已开启自动任务' : '已停止自动任务');
}

// ==================== 单次运行弹窗 ====================
const runOpen = ref(false);
const runModel = ref<null | ModelRecord>(null);
const runForm = reactive<Record<string, string>>({});

function openRun(model: ModelRecord) {
  runModel.value = model;
  Object.keys(runForm).forEach((key) => delete runForm[key]);
  for (const param of getModelParams(model.id).input) {
    runForm[param.paramKey] = param.defaultValue;
  }
  runOpen.value = true;
}

function handleRunSubmit() {
  const model = runModel.value;

  if (!model) {
    return;
  }

  for (const param of getModelParams(model.id).input) {
    if (param.required && !String(runForm[param.paramKey] ?? '').trim()) {
      window.message.warning(`请填写必填参数：${param.paramName}`);
      return;
    }
  }

  const deviceType = getCategoryDeviceType(model.categoryId);
  addExecRecord({
    deviceCount: deviceType ? getDevicesByType(deviceType).length : 0,
    modelId: model.id,
    status: '运行中',
    taskId: getTaskByModelId(model.id)?.id ?? null,
    triggerType: '单次运行',
  });
  window.message.success('已提交执行，可在执行记录中查看进度');
  runOpen.value = false;
}

// ==================== 模型详情抽屉 ====================
const detailOpen = ref(false);
const detailModel = ref<null | ModelRecord>(null);

function openDetail(model: ModelRecord) {
  detailModel.value = model;
  detailOpen.value = true;
}

const detailDescriptionItems = computed(() => {
  const model = detailModel.value;

  if (!model) {
    return [];
  }

  return [
    { content: getCategoryName(model.categoryId), label: '模型分类' },
    { content: model.registerType, label: '注册方式' },
    { content: `${model.statPeriod} 天`, label: '统计周期' },
    { content: model.status, label: '模型状态' },
    { content: model.registerTime, label: '注册时间' },
    {
      content: model.lastRunTime
        ? `${model.lastRunTime}（${model.lastRunStatus || '-'}）`
        : '暂无',
      label: '最近运行',
    },
    { content: model.description || '-', label: '模型简介' },
  ];
});

const detailInputParams = computed(() =>
  detailModel.value ? getModelParams(detailModel.value.id).input : [],
);
const detailOutputParams = computed(() =>
  detailModel.value ? getModelParams(detailModel.value.id).output : [],
);

// ==================== 查看结果抽屉 ====================
const resultOpen = ref(false);
const resultModel = ref<null | ModelRecord>(null);
const resultViewMode = ref<'line' | 'type'>('line');

function openResult(model: ModelRecord) {
  resultModel.value = model;
  resultViewMode.value = 'line';
  resultOpen.value = true;
}

const resultRows = computed(() => {
  if (!resultModel.value) {
    return [];
  }
  const rows = getModelResultRows(resultModel.value.id);

  return [...rows].sort((a, b) => {
    if (resultViewMode.value === 'type') {
      return (
        a.deviceType.localeCompare(b.deviceType) ||
        a.line.localeCompare(b.line) ||
        a.station.localeCompare(b.station)
      );
    }
    return (
      a.line.localeCompare(b.line) ||
      a.station.localeCompare(b.station) ||
      a.deviceName.localeCompare(b.deviceName)
    );
  });
});

const resultColumns = computed<TableProps['columns']>(() => {
  const groupColumns =
    resultViewMode.value === 'type'
      ? [
          { dataIndex: 'deviceType', key: 'deviceType', title: '设备品种', width: 100 },
          { dataIndex: 'line', key: 'line', title: '线路', width: 90 },
          { dataIndex: 'station', key: 'station', title: '车站', width: 120 },
          { dataIndex: 'deviceName', key: 'deviceName', title: '设备名称', width: 230 },
        ]
      : [
          { dataIndex: 'deviceName', key: 'deviceName', title: '设备名称', width: 230 },
          { dataIndex: 'line', key: 'line', title: '线路', width: 90 },
          { dataIndex: 'station', key: 'station', title: '车站', width: 120 },
        ];

  return [
    ...groupColumns,
    { dataIndex: 'healthLevel', key: 'healthLevel', title: '健康等级', width: 100 },
    { dataIndex: 'totalScore', key: 'totalScore', title: '总分', width: 80 },
    { dataIndex: 'dimBody', key: 'dimBody', title: '本体/20', width: 90 },
    { dataIndex: 'dimCondition', key: 'dimCondition', title: '工况/25', width: 90 },
    { dataIndex: 'dimFault', key: 'dimFault', title: '故障/25', width: 90 },
    { dataIndex: 'dimMaintain', key: 'dimMaintain', title: '维保/20', width: 90 },
    { dataIndex: 'dimEnv', key: 'dimEnv', title: '环境/10', width: 90 },
  ];
});

onMounted(() => {
  const rawCategoryId = Number(route.query.categoryId);

  if (Number.isFinite(rawCategoryId) && rawCategoryId > 0) {
    query.categoryId = rawCategoryId;
  }
});
</script>

<template>
  <Page>
    <div class="model-manage-page">
      <PlatformViewToolbar
        description="注册、发布、运行与下架模型全生命周期管理"
        title="模型管理"
      />

      <PlatformQueryPanel :columns="4">
        <PlatformSelect
          v-model:value="query.categoryId"
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
        <template #actions>
          <PlatformButton scene="toolbar" @click="handleReset">重置</PlatformButton>
        </template>
      </PlatformQueryPanel>

      <section class="platform-surface model-manage-surface">
        <PlatformTableToolbar
          v-model:search-value="toolbarKeyword"
          :tools="['refresh', 'setting', 'fullscreen']"
          search-placeholder="搜索模型名称"
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
          :data-source="filteredRows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelName'">
              <PlatformButton
                scene="action"
                size="small"
                type="link"
                @click="openDetail(record as ModelRecord)"
              >
                {{ (record as ModelRecord).modelName }}
              </PlatformButton>
            </template>
            <template v-else-if="column.key === 'category'">
              {{ getCategoryName((record as ModelRecord).categoryId) }}
            </template>
            <template v-else-if="column.key === 'statPeriod'">
              {{ (record as ModelRecord).statPeriod }} 天
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="modelStatusMeta[(record as ModelRecord).status].label"
                :status="modelStatusMeta[(record as ModelRecord).status].status"
              />
            </template>
            <template v-else-if="column.key === 'registerType'">
              {{ (record as ModelRecord).registerType }}
            </template>
            <template v-else-if="column.key === 'lastRun'">
              <template v-if="(record as ModelRecord).lastRunTime">
                <div>{{ (record as ModelRecord).lastRunTime }}</div>
                <PlatformStatusTag
                  :label="execStatusMeta[(record as ModelRecord).lastRunStatus as keyof typeof execStatusMeta].label"
                  :status="execStatusMeta[(record as ModelRecord).lastRunStatus as keyof typeof execStatusMeta].status"
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
                  @click="openDetail(record as ModelRecord)"
                >
                  查看
                </PlatformButton>

                <template v-if="(record as ModelRecord).status === '草稿'">
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="openRegister(record as ModelRecord)"
                  >
                    编辑
                  </PlatformButton>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="handlePublish(record as ModelRecord)"
                  >
                    发布
                  </PlatformButton>
                  <Popconfirm
                    placement="left"
                    title="确认删除该模型？"
                    @confirm="handleDelete(record as ModelRecord)"
                  >
                    <PlatformButton danger scene="action" size="small" type="link">
                      删除
                    </PlatformButton>
                  </Popconfirm>
                </template>

                <template v-else-if="(record as ModelRecord).status === '已发布'">
                  <Popconfirm
                    placement="left"
                    :title="(record as ModelRecord).enabled ? '确认停止自动任务？' : '确认开启自动任务？'"
                    @confirm="handleToggleEnabled(record as ModelRecord)"
                  >
                    <PlatformButton scene="action" size="small" type="link">
                      {{ (record as ModelRecord).enabled ? '停止' : '开启' }}
                    </PlatformButton>
                  </Popconfirm>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="openRun(record as ModelRecord)"
                  >
                    单次运行
                  </PlatformButton>
                  <Popconfirm
                    placement="left"
                    title="确认下架该模型？下架后关联任务自动暂停。"
                    @confirm="handleOffline(record as ModelRecord)"
                  >
                    <PlatformButton scene="action" size="small" type="link">
                      下架
                    </PlatformButton>
                  </Popconfirm>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="openResult(record as ModelRecord)"
                  >
                    查看结果
                  </PlatformButton>
                </template>

                <template v-else>
                  <PlatformButton
                    scene="action"
                    size="small"
                    type="link"
                    @click="handleRePublish(record as ModelRecord)"
                  >
                    重新发布
                  </PlatformButton>
                  <Popconfirm
                    placement="left"
                    title="确认删除该模型？"
                    @confirm="handleDelete(record as ModelRecord)"
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
    <PlatformModal
      v-model:open="registerOpen"
      :confirm-loading="registerSaving"
      :title="registerTitle"
      destroy-on-close
      width="960px"
    >
      <div class="model-register-body">
        <PlatformSectionTitle title="一、基本信息" />
        <PlatformEditForm :model="formModel" class="model-register-grid" layout="vertical">
          <PlatformFormItem label="模型名称" required>
            <PlatformInput
              v-model:value="formModel.modelName"
              :maxlength="100"
              placeholder="请输入模型名称（全局唯一）"
            />
          </PlatformFormItem>
          <PlatformFormItem label="模型分类" required>
            <PlatformSelect
              v-model:value="formModel.categoryId"
              :options="categoryOptions"
              placeholder="请选择模型分类"
            />
          </PlatformFormItem>
          <PlatformFormItem label="注册方式" required>
            <PlatformSelect
              v-model:value="formModel.registerType"
              :options="registerTypeOptions"
              placeholder="请选择注册方式"
            />
          </PlatformFormItem>
          <PlatformFormItem label="统计周期（天）" required>
            <PlatformSelect
              v-model:value="formModel.statPeriod"
              :options="statPeriodOptions"
              placeholder="请选择统计周期"
            />
          </PlatformFormItem>
          <PlatformFormItem class="model-register-grid__full" label="模型简介">
            <PlatformInput
              v-model:value="formModel.description"
              :maxlength="500"
              placeholder="请输入模型简介（≤500 字符）"
            />
          </PlatformFormItem>
        </PlatformEditForm>

        <PlatformSectionTitle title="二、参数配置" />
        <div class="model-param-block">
          <div class="model-param-block__head">
            <span>输入参数</span>
            <PlatformButton
              scene="action"
              size="small"
              type="link"
              @click="inputRows.push(emptyParamRow())"
            >
              + 添加输入参数
            </PlatformButton>
          </div>
          <div class="model-param-grid model-param-grid--head">
            <span>参数标识</span><span>显示名</span><span>类型</span><span>必填</span><span>默认值</span><span></span>
          </div>
          <div
            v-for="(row, index) in inputRows"
            :key="`input-${index}`"
            class="model-param-grid"
          >
            <PlatformInput v-model:value="row.paramKey" placeholder="如 window_days" />
            <PlatformInput v-model:value="row.paramName" placeholder="参数显示名" />
            <PlatformSelect v-model:value="row.paramType" :options="paramTypeOptions" />
            <Checkbox v-model:checked="row.required">必填</Checkbox>
            <PlatformInput v-model:value="row.defaultValue" placeholder="默认值" />
            <PlatformButton
              danger
              scene="action"
              size="small"
              type="link"
              @click="inputRows.splice(index, 1)"
            >
              删除
            </PlatformButton>
          </div>
        </div>

        <div class="model-param-block">
          <div class="model-param-block__head">
            <span>输出参数</span>
            <PlatformButton
              scene="action"
              size="small"
              type="link"
              @click="outputRows.push(emptyParamRow())"
            >
              + 添加输出参数
            </PlatformButton>
          </div>
          <div class="model-param-grid model-param-grid--output model-param-grid--head">
            <span>参数标识</span><span>显示名</span><span>类型</span><span>说明</span><span></span>
          </div>
          <div
            v-for="(row, index) in outputRows"
            :key="`output-${index}`"
            class="model-param-grid model-param-grid--output"
          >
            <PlatformInput v-model:value="row.paramKey" placeholder="如 health_level" />
            <PlatformInput v-model:value="row.paramName" placeholder="参数显示名" />
            <PlatformSelect v-model:value="row.paramType" :options="paramTypeOptions" />
            <PlatformInput v-model:value="row.description" placeholder="结果展示说明" />
            <PlatformButton
              danger
              scene="action"
              size="small"
              type="link"
              @click="outputRows.splice(index, 1)"
            >
              删除
            </PlatformButton>
          </div>
        </div>

        <PlatformSectionTitle title="三、算法脚本" />
        <div
          class="model-upload-zone"
          @click="handleUploadScript"
        >
          <PlatformIcon class="model-upload-zone__icon" icon="lucide:upload-cloud" />
          <p>点击或拖拽上传 Python 脚本（仅支持 .py，≤50MB）</p>
          <span>遵循平台入口函数约定：实现 execute(context) 函数</span>
        </div>
        <PlatformFileList :items="scriptFiles" class="model-script-files" />
        <div class="model-requirements-row">
          <span>requirements.txt（可选，声明第三方依赖）</span>
          <PlatformButton scene="action" size="small" type="link" @click="handleUploadRequirements">
            上传
          </PlatformButton>
        </div>
      </div>

      <template #footer>
        <Space>
          <PlatformButton scene="toolbar" @click="registerOpen = false">
            取消
          </PlatformButton>
          <PlatformButton
            :loading="registerSaving"
            scene="toolbar"
            type="primary"
            @click="handleSaveDraft"
          >
            确定（存草稿）
          </PlatformButton>
          <PlatformButton
            :loading="registerSaving"
            scene="toolbar"
            type="primary"
            @click="handleSaveAndEnable"
          >
            确定并开启
          </PlatformButton>
        </Space>
      </template>
    </PlatformModal>

    <!-- 单次运行 -->
    <PlatformModal
      v-model:open="runOpen"
      :title="`单次运行 - ${runModel?.modelName ?? ''}`"
      destroy-on-close
      width="640px"
      @ok="handleRunSubmit"
    >
      <PlatformEditForm :model="runForm" class="model-register-grid" layout="vertical">
        <template v-for="param in runModel ? getModelParams(runModel.id).input : []" :key="param.paramKey">
          <PlatformFormItem :label="param.paramName" :required="param.required">
            <PlatformSelect
              v-if="param.paramType === '布尔'"
              v-model:value="runForm[param.paramKey]"
              :options="[
                { label: '是', value: 'true' },
                { label: '否', value: 'false' },
              ]"
              placeholder="请选择"
            />
            <PlatformInput
              v-else
              v-model:value="runForm[param.paramKey]"
              :placeholder="param.defaultValue ? `默认值 ${param.defaultValue}` : '请输入'"
              :type="param.paramType === '数值' ? 'number' : 'text'"
            />
          </PlatformFormItem>
        </template>
      </PlatformEditForm>
      <p class="model-run-tip">
        设备范围默认为该类别关联设备类型下的全部设备，提交后进入执行队列。
      </p>
    </PlatformModal>

    <!-- 模型详情 -->
    <PlatformDrawer
      v-model:open="detailOpen"
      destroy-on-close
      size="large"
      title="模型详情"
    >
      <div v-if="detailModel" class="model-detail-body">
        <PlatformDescriptions :items="detailDescriptionItems" />

        <h4 class="model-detail-body__subtitle">输入参数</h4>
        <ul class="model-detail-param-group">
          <li v-for="param in detailInputParams" :key="param.paramKey">
            <span class="model-detail-param-key">{{ param.paramKey }}</span>
            <span>{{ param.paramName }}</span>
            <PlatformStatusTag :label="param.paramType" status="processing" />
            <PlatformStatusTag v-if="param.required" label="必填" status="warning" />
            <span>默认值：{{ param.defaultValue || '-' }}</span>
          </li>
        </ul>

        <h4 class="model-detail-body__subtitle">输出参数</h4>
        <ul class="model-detail-param-group">
          <li v-for="param in detailOutputParams" :key="param.paramKey">
            <span class="model-detail-param-key">{{ param.paramKey }}</span>
            <span>{{ param.paramName }}</span>
            <PlatformStatusTag :label="param.paramType" status="processing" />
          </li>
        </ul>
      </div>
    </PlatformDrawer>

    <!-- 查看结果 -->
    <PlatformDrawer
      v-model:open="resultOpen"
      destroy-on-close
      size="large"
      :title="`查看结果 - ${resultModel?.modelName ?? ''}`"
    >
      <div class="model-result-body">
        <PlatformSegmented
          v-model:value="resultViewMode"
          :options="[
            { label: '按线路层级', value: 'line' },
            { label: '按设备品种层级', value: 'type' },
          ]"
        />
        <PlatformTable
          :action-column-width="0"
          :columns="resultColumns"
          :data-source="resultRows"
          :pagination="false"
          :show-index="true"
          :scroll="{ x: 1100 }"
          row-key="deviceId"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'healthLevel'">
              <PlatformStatusTag
                :label="healthLevelMeta[(record as { healthLevel: keyof typeof healthLevelMeta }).healthLevel].label"
                :status="healthLevelMeta[(record as { healthLevel: keyof typeof healthLevelMeta }).healthLevel].status"
              />
            </template>
          </template>
        </PlatformTable>
      </div>
    </PlatformDrawer>
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

.model-register-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-register-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.model-register-grid__full {
  grid-column: 1 / -1;
}

.model-param-block {
  margin-bottom: 10px;
}

.model-param-block__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  font-weight: 600;
}

.model-param-grid {
  display: grid;
  grid-template-columns: 1.2fr 1.1fr 0.8fr 0.7fr 0.9fr auto;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.model-param-grid--output {
  grid-template-columns: 1.2fr 1.1fr 0.8fr 1.4fr auto;
}

.model-param-grid--head {
  margin-bottom: 6px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-upload-zone {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 20px;
  cursor: pointer;
  color: hsl(var(--muted-foreground));
  background: hsl(var(--accent) / 30%);
  border: 1px dashed hsl(var(--primary));
  border-radius: var(--st-radius-card);
  transition: background 0.16s ease;
}

.model-upload-zone:hover {
  background: hsl(var(--primary) / 8%);
}

.model-upload-zone__icon {
  font-size: 26px;
  color: hsl(var(--primary));
}

.model-upload-zone p {
  margin: 0;
  color: hsl(var(--foreground));
}

.model-upload-zone span {
  font-size: 12px;
}

.model-script-files {
  margin-top: 8px;
}

.model-requirements-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 6px;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}

.model-run-tip {
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-detail-body__subtitle {
  margin: 16px 0 8px;
  font-size: 14px;
  font-weight: 600;
}

.model-detail-param-group {
  display: grid;
  gap: 6px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.model-detail-param-group li {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  font-size: 13px;
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-control);
}

.model-detail-param-key {
  font-family: monospace;
  color: hsl(var(--primary));
}

.model-result-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

@media (max-width: 900px) {
  .model-register-grid {
    grid-template-columns: 1fr;
  }

  .model-param-grid,
  .model-param-grid--output {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
