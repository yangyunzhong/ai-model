<script setup lang="ts">
import type { DefinitionVO, ParamVO } from '#/api/model/model';

import { computed, reactive, ref } from 'vue';

import {
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
} from '@st/platform-ui';

import { execLogRunOnce, paramList } from '#/api/model';

const emit = defineEmits<{ success: [] }>();

const runOpen = ref(false);
const submitting = ref(false);
const runModel = ref<null | DefinitionVO>(null);
const inputParams = ref<ParamVO[]>([]);
const runForm = reactive<Record<string, string>>({});

const runInputParams = computed(() => inputParams.value);

/** 按参数类型转换输入值：数值→number，布尔→boolean，其余保持字符串 */
function convertValue(paramType: string, raw: string | undefined) {
  const value = raw ?? '';
  if (paramType === '数值') {
    return value === '' ? null : Number(value);
  }
  if (paramType === '布尔') {
    return value === 'true';
  }
  return value;
}

/** 打开单次运行弹窗并回显模型输入参数默认值 */
async function open(model: DefinitionVO) {
  runModel.value = model;
  Object.keys(runForm).forEach((key) => delete runForm[key]);
  runOpen.value = true;

  const res = await paramList({
    modelId: model.id,
    paramDirection: '输入',
    pageNum: 1,
    pageSize: 200,
  });
  inputParams.value = res.rows ?? [];
  for (const param of inputParams.value) {
    runForm[param.paramKey] = param.defaultValue ?? '';
  }
}

async function handleSubmit() {
  const model = runModel.value;

  if (!model) {
    return;
  }

  for (const param of inputParams.value) {
    if (param.required === '1' && !String(runForm[param.paramKey] ?? '').trim()) {
      window.message.warning(`请填写必填参数：${param.paramName}`);
      return;
    }
  }

  const params: Record<string, any> = {};
  for (const param of inputParams.value) {
    params[param.paramKey] = convertValue(param.paramType, runForm[param.paramKey]);
  }

  submitting.value = true;
  try {
    // 由后端编排器真实调度执行并落库，无需前端拼装执行记录
    await execLogRunOnce({
      modelId: model.id,
      params,
      triggerType: '单次运行',
    });
    runOpen.value = false;
    emit('success');
  } catch {
    // 错误提示由请求层统一处理，保持弹窗打开便于修正后重试
  } finally {
    submitting.value = false;
  }
}

defineExpose({ open });
</script>

<template>
  <PlatformModal
    v-model:open="runOpen"
    :confirm-loading="submitting"
    :title="`单次运行 - ${runModel?.modelName ?? ''}`"
    destroy-on-close
    width="640px"
    @ok="handleSubmit"
  >
    <PlatformEditForm :model="runForm" class="model-run-form" layout="vertical">
      <PlatformFormItem
        v-for="param in runInputParams"
        :key="param.paramKey"
        :label="param.paramName"
        :required="param.required === '1'"
      >
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
    </PlatformEditForm>
    <p v-if="runInputParams.length === 0" class="model-run-empty">
      该模型未配置输入参数，可直接提交运行。
    </p>
    <p class="model-run-tip">
      设备范围默认为该类别关联设备类型下的全部设备，提交后进入执行队列。
    </p>
  </PlatformModal>
</template>

<style scoped>
.model-run-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.model-run-tip {
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-run-empty {
  margin: 0 0 6px;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}
</style>
