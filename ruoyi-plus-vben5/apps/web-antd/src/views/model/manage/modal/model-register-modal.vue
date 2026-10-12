<script setup lang="ts">
import type {
  CatalogVO,
  DefinitionForm,
  DefinitionVO,
  ParamForm,
  ParamVO,
  ScriptVO,
} from '#/api/model/model';

import { computed, reactive, ref } from 'vue';

import {
  PlatformButton,
  PlatformEditForm,
  PlatformFileList,
  PlatformFormItem,
  PlatformIcon,
  PlatformInput,
  PlatformModal,
  PlatformSectionTitle,
  PlatformSelect,
} from '@st/platform-ui';
import { Checkbox, Space } from 'antdv-next';

import {
  catalogList,
  definitionAdd,
  definitionPublish,
  definitionUpdate,
  paramAdd,
  paramList,
  paramRemove,
  paramUpdate,
  scriptList,
  scriptUpload,
} from '#/api/model';

import { registerTypeOptions, statPeriodOptions } from '../../data';

const emit = defineEmits<{ success: [] }>();

interface ParamRow {
  id?: string | number;
  paramKey: string;
  paramName: string;
  paramType: string;
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

const formOpen = ref(false);
const saving = ref(false);
const uploading = ref(false);
const scriptInputRef = ref<HTMLInputElement>();
const requirementsInputRef = ref<HTMLInputElement>();

const formModel = reactive<{
  catalogId: undefined | string | number;
  description: string;
  deviceType: string;
  id: undefined | string | number;
  modelName: string;
  registerType: string;
  statisticsCycleDays: string;
}>({
  catalogId: undefined,
  description: '',
  deviceType: '',
  id: undefined,
  modelName: '',
  registerType: '本地注册',
  statisticsCycleDays: '90',
});

const inputRows = ref<ParamRow[]>([]);
const outputRows = ref<ParamRow[]>([]);
const scriptFiles = ref<ScriptFileItem[]>([]);
/** 待上传的脚本与依赖清单（选择后统一提交） */
const pendingScript = ref<File | null>(null);
const pendingRequirements = ref<File | null>(null);
/** 编辑时已落库的参数主键，保存时用于删除被移除的行 */
const loadedParamIds = ref<Array<string | number>>([]);
/** 编辑时保留的原始注册时间，避免更新时被覆盖 */
const originalRegisterTime = ref('');

const catalogOptions = ref<{ label: string; value: string | number }[]>([]);
const catalogDeviceTypeMap = ref<Record<string, string>>({});
let categoriesLoaded = false;

const title = computed(() => (formModel.id ? '编辑模型' : '注册模型'));

const paramTypeOptions = [
  { label: '数值', value: '数值' },
  { label: '文本', value: '文本' },
  { label: '布尔', value: '布尔' },
];

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

function formatNow() {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
}

function formatSize(bytes?: number) {
  if (bytes === undefined || bytes === null) {
    return '-';
  }
  if (bytes < 1024) {
    return `${bytes} B`;
  }
  if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(1)} KB`;
  }
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
}

async function loadCategories() {
  const res = await catalogList({ pageNum: 1, pageSize: 200 });
  const list: CatalogVO[] = res.rows ?? [];
  catalogOptions.value = list
    .filter((item) => item.status !== '停用')
    .map((item) => ({ label: item.catalogName, value: item.id }));
  catalogDeviceTypeMap.value = Object.fromEntries(
    list.map((item) => [String(item.id), item.deviceType]),
  );
}

function toParamRow(param: ParamVO): ParamRow {
  return {
    id: param.id,
    paramKey: param.paramKey,
    paramName: param.paramName,
    paramType: param.paramType,
    required: param.required === '1',
    defaultValue: param.defaultValue ?? '',
    description: param.description ?? '',
  };
}

async function loadParams(modelId: string | number) {
  const res = await paramList({ modelId, pageNum: 1, pageSize: 200 });
  const list: ParamVO[] = res.rows ?? [];
  inputRows.value = list
    .filter((item) => item.paramDirection === '输入')
    .map(toParamRow);
  outputRows.value = list
    .filter((item) => item.paramDirection === '输出')
    .map(toParamRow);
  loadedParamIds.value = list.map((item) => item.id);
}

function toScriptItem(script: ScriptVO): ScriptFileItem {
  return {
    id: String(script.id),
    name: script.originalName || script.fileName,
    size: formatSize(script.fileSize),
    date: script.scriptVersion || 'v1',
    description: `${script.checkStatus || '待校验'}${
      script.isCurrent === '1' ? ' · 当前生效版本' : ''
    }`,
    type: 'other',
  };
}

async function loadScripts() {
  if (formModel.id === undefined || formModel.id === null) {
    scriptFiles.value = [];
    return;
  }
  const res = await scriptList({
    modelId: formModel.id,
    pageNum: 1,
    pageSize: 50,
  });
  scriptFiles.value = (res.rows ?? []).map(toScriptItem);
}

/** 打开注册 / 编辑模型弹窗，传入模型则为编辑 */
async function open(model?: DefinitionVO) {
  formModel.id = model?.id;
  formModel.modelName = model?.modelName ?? '';
  formModel.catalogId = model?.catalogId;
  formModel.registerType = model?.registerType ?? '本地注册';
  formModel.statisticsCycleDays = String(model?.statisticsCycleDays ?? 90);
  formModel.deviceType = model?.deviceType ?? '';
  formModel.description = model?.description ?? '';
  originalRegisterTime.value = model?.registerTime ?? '';
  inputRows.value = [];
  outputRows.value = [];
  scriptFiles.value = [];
  loadedParamIds.value = [];
  pendingScript.value = null;
  pendingRequirements.value = null;
  formOpen.value = true;

  if (!categoriesLoaded) {
    await loadCategories();
    categoriesLoaded = true;
  }
  if (model?.id !== undefined && model?.id !== null) {
    await Promise.all([loadParams(model.id), loadScripts()]);
  }
}

function handleCatalogChange(value: string | number) {
  formModel.deviceType = catalogDeviceTypeMap.value[String(value)] ?? '';
}

/** 提取上传失败原因：业务异常携带后端中文提示，HTTP/网络异常统一兜底 */
function resolveUploadError(error: unknown) {
  if (error instanceof Error && error.message && /[\u4E00-\u9FA5]/.test(error.message)) {
    return error.message;
  }
  return '请检查文件内容、登录状态或网络后重试';
}

function ensureDraftSaved() {
  if (formModel.id === undefined || formModel.id === null) {
    window.message.info('请先保存草稿，再上传算法脚本');
    return false;
  }
  return true;
}

function handleUploadScript() {
  if (!ensureDraftSaved()) {
    return;
  }
  scriptInputRef.value?.click();
}

function handleUploadRequirements() {
  if (!ensureDraftSaved()) {
    return;
  }
  requirementsInputRef.value?.click();
}

function handleScriptSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file) {
    return;
  }
  if (!file.name.toLowerCase().endsWith('.py')) {
    window.message.warning('仅支持上传 .py 脚本文件');
    return;
  }
  pendingScript.value = file;
}

function handleRequirementsSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file) {
    return;
  }
  if (!file.name.toLowerCase().endsWith('.txt')) {
    window.message.warning('依赖清单仅支持 requirements.txt 文本文件');
    return;
  }
  pendingRequirements.value = file;
}

/** 统一提交脚本与可选的 requirements.txt */
async function submitScript() {
  if (!ensureDraftSaved()) {
    return;
  }
  const file = pendingScript.value;
  if (!file) {
    window.message.warning('请先选择 .py 脚本文件');
    return;
  }
  uploading.value = true;
  try {
    await scriptUpload(file, formModel.id, undefined, pendingRequirements.value ?? undefined);
    pendingScript.value = null;
    pendingRequirements.value = null;
    await loadScripts();
    window.message.success(`脚本「${file.name}」上传成功`);
  } catch (error) {
    window.message.error(`脚本「${file.name}」上传失败：${resolveUploadError(error)}`);
  } finally {
    uploading.value = false;
  }
}

function validateForm() {
  if (!formModel.modelName.trim()) {
    window.message.warning('请输入模型名称');
    return false;
  }
  if (
    formModel.catalogId === undefined ||
    formModel.catalogId === null ||
    formModel.catalogId === ''
  ) {
    window.message.warning('请选择模型分类');
    return false;
  }
  return true;
}

function buildDefinitionForm(status: string): DefinitionForm {
  return {
    id: formModel.id,
    modelName: formModel.modelName.trim(),
    catalogId: formModel.catalogId,
    deviceType:
      formModel.deviceType ||
      catalogDeviceTypeMap.value[String(formModel.catalogId)] ||
      '',
    statisticsCycleDays: Number(formModel.statisticsCycleDays) || 90,
    status,
    registerType: formModel.registerType,
    registerTime: originalRegisterTime.value || formatNow(),
    description: formModel.description,
  };
}

/** 按输入/输出方向落库参数：有主键则更新，无主键则新增，缺失的旧行删除 */
async function persistParams(modelId: string | number) {
  const rows: Array<ParamRow & { paramDirection: string }> = [
    ...inputRows.value.map((row) => ({ ...row, paramDirection: '输入' })),
    ...outputRows.value.map((row) => ({ ...row, paramDirection: '输出' })),
  ].filter((row) => row.paramKey.trim());

  const keptIds: Array<string | number> = [];

  for (const [index, row] of rows.entries()) {
    const payload: ParamForm = {
      modelId,
      paramDirection: row.paramDirection,
      paramKey: row.paramKey.trim(),
      paramName: row.paramName.trim() || row.paramKey.trim(),
      paramType: row.paramType,
      required: row.paramDirection === '输入' && row.required ? '1' : '0',
      defaultValue: row.defaultValue ?? '',
      description: row.description ?? '',
      sortOrder: index + 1,
    };
    if (row.id === undefined || row.id === null) {
      await paramAdd(payload);
    } else {
      await paramUpdate({ ...payload, id: row.id });
      keptIds.push(row.id);
    }
  }

  const removedIds = loadedParamIds.value.filter(
    (id) => !keptIds.some((kept) => String(kept) === String(id)),
  );
  if (removedIds.length > 0) {
    await paramRemove(removedIds);
  }
}

/** 保存模型定义与参数，返回模型ID；统一先存草稿，发布走 publish 流程 */
async function saveDefinition() {
  const payload = buildDefinitionForm('草稿');
  let modelId: string | number;
  if (formModel.id === undefined || formModel.id === null) {
    modelId = await definitionAdd(payload);
    formModel.id = modelId;
  } else {
    modelId = formModel.id;
    await definitionUpdate(payload);
  }
  await persistParams(modelId);
  return modelId;
}

async function handleSave(publish: boolean) {
  if (!validateForm()) {
    return;
  }
  saving.value = true;
  try {
    const modelId = await saveDefinition();
    if (publish) {
      // 发布：创建/启动运行时容器并安装依赖，失败时保留草稿状态
      await definitionPublish(modelId);
    }
    formOpen.value = false;
    emit('success');
  } catch {
    // 错误提示由请求层统一处理，失败时保持弹窗打开以便修正
  } finally {
    saving.value = false;
  }
}

function handleSaveDraft() {
  return handleSave(false);
}

function handleSaveAndEnable() {
  return handleSave(true);
}

defineExpose({ open });
</script>

<template>
  <PlatformModal
    v-model:open="formOpen"
    :confirm-loading="saving"
    :title="title"
    destroy-on-close
    width="960px"
  >
    <div class="model-register-modal__body">
      <PlatformSectionTitle title="一、基本信息" />
      <PlatformEditForm
        :model="formModel"
        class="model-register-modal__grid"
        layout="vertical"
      >
        <PlatformFormItem label="模型名称" required>
          <PlatformInput
            v-model:value="formModel.modelName"
            :maxlength="100"
            placeholder="请输入模型名称（全局唯一）"
          />
        </PlatformFormItem>
        <PlatformFormItem label="模型分类" required>
          <PlatformSelect
            v-model:value="formModel.catalogId"
            :options="catalogOptions"
            placeholder="请选择模型分类"
            @change="handleCatalogChange"
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
            v-model:value="formModel.statisticsCycleDays"
            :options="statPeriodOptions"
            placeholder="请选择统计周期"
          />
        </PlatformFormItem>
        <PlatformFormItem class="model-register-modal__full" label="模型简介">
          <PlatformInput
            v-model:value="formModel.description"
            :maxlength="500"
            placeholder="请输入模型简介（≤500 字符）"
          />
        </PlatformFormItem>
      </PlatformEditForm>

      <PlatformSectionTitle title="二、参数配置" />
      <div class="model-register-modal__param-block">
        <div class="model-register-modal__param-head">
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
        <div class="model-register-modal__param-grid model-register-modal__param-grid--head">
          <span>参数标识</span><span>显示名</span><span>类型</span><span>必填</span><span>默认值</span><span></span>
        </div>
        <div
          v-for="(row, index) in inputRows"
          :key="`input-${index}`"
          class="model-register-modal__param-grid"
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

      <div class="model-register-modal__param-block">
        <div class="model-register-modal__param-head">
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
        <div
          class="model-register-modal__param-grid model-register-modal__param-grid--output model-register-modal__param-grid--head"
        >
          <span>参数标识</span><span>显示名</span><span>类型</span><span>说明</span><span></span>
        </div>
        <div
          v-for="(row, index) in outputRows"
          :key="`output-${index}`"
          class="model-register-modal__param-grid model-register-modal__param-grid--output"
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
      <div class="model-register-modal__upload-grid">
        <div class="model-register-modal__upload-zone" @click="handleUploadScript">
          <PlatformIcon class="model-register-modal__upload-icon" icon="lucide:upload-cloud" />
          <p>{{ pendingScript ? pendingScript.name : '点击选择 Python 脚本（仅支持 .py）' }}</p>
          <span>遵循平台入口函数约定：实现 execute(params) 函数</span>
        </div>
        <div class="model-register-modal__upload-zone" @click="handleUploadRequirements">
          <PlatformIcon class="model-register-modal__upload-icon" icon="lucide:file-text" />
          <p>{{ pendingRequirements ? pendingRequirements.name : '点击选择依赖清单（可选）' }}</p>
          <span>requirements.txt，发布时容器自动安装</span>
        </div>
      </div>
      <input
        ref="scriptInputRef"
        accept=".py"
        class="model-register-modal__file-input"
        type="file"
        @change="handleScriptSelected"
      />
      <input
        ref="requirementsInputRef"
        accept=".txt"
        class="model-register-modal__file-input"
        type="file"
        @change="handleRequirementsSelected"
      />
      <div class="model-register-modal__upload-actions">
        <PlatformButton
          :loading="uploading"
          scene="toolbar"
          type="primary"
          @click="submitScript"
        >
          上传算法脚本
        </PlatformButton>
      </div>
      <PlatformFileList :items="scriptFiles" class="model-register-modal__files" />
    </div>

    <template #footer>
      <Space>
        <PlatformButton scene="toolbar" @click="formOpen = false">取消</PlatformButton>
        <PlatformButton
          :loading="saving"
          scene="toolbar"
          type="primary"
          @click="handleSaveDraft"
        >
          确定（存草稿）
        </PlatformButton>
        <PlatformButton
          :loading="saving"
          scene="toolbar"
          type="primary"
          @click="handleSaveAndEnable"
        >
          确定并开启
        </PlatformButton>
      </Space>
    </template>
  </PlatformModal>
</template>

<style scoped>
.model-register-modal__body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-register-modal__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.model-register-modal__full {
  grid-column: 1 / -1;
}

.model-register-modal__param-block {
  margin-bottom: 10px;
}

.model-register-modal__param-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  font-weight: 600;
}

.model-register-modal__param-grid {
  display: grid;
  grid-template-columns: 1.2fr 1.1fr 0.8fr 0.7fr 0.9fr auto;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.model-register-modal__param-grid--output {
  grid-template-columns: 1.2fr 1.1fr 0.8fr 1.4fr auto;
}

.model-register-modal__param-grid--head {
  margin-bottom: 6px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-register-modal__upload-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.model-register-modal__upload-zone {
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

.model-register-modal__upload-zone p {
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.model-register-modal__upload-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.model-register-modal__upload-zone:hover {
  background: hsl(var(--primary) / 8%);
}

.model-register-modal__upload-icon {
  font-size: 26px;
  color: hsl(var(--primary));
}

.model-register-modal__upload-zone p {
  margin: 0;
  color: hsl(var(--foreground));
}

.model-register-modal__upload-zone span {
  font-size: 12px;
}

.model-register-modal__file-input {
  display: none;
}

.model-register-modal__files {
  margin-top: 8px;
}

@media (max-width: 900px) {
  .model-register-modal__grid {
    grid-template-columns: 1fr;
  }

  .model-register-modal__upload-grid {
    grid-template-columns: 1fr;
  }

  .model-register-modal__param-grid,
  .model-register-modal__param-grid--output {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
