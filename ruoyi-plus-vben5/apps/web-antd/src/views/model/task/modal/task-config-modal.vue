<script setup lang="ts">
import type { ModelTaskRecord } from '../../data';

import { computed, reactive, ref } from 'vue';

import {
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
} from '@st/platform-ui';

import { cronPresetOptions, getModelParams, updateTask } from '../../data';

const formOpen = ref(false);
const saving = ref(false);
const configModel = ref<null | ModelTaskRecord>(null);
const formModel = reactive({
  cronDesc: '每日 02:00',
  customCron: '',
  id: 0,
  remark: '',
  runParams: {} as Record<string, string>,
  taskName: '',
});

const configInputParams = computed(() =>
  configModel.value ? getModelParams(configModel.value.modelId).input : [],
);

/** 打开配置弹窗并回显行数据 */
function open(task: ModelTaskRecord) {
  configModel.value = task;
  formModel.id = task.id;
  formModel.taskName = task.taskName;

  const isPreset = cronPresetOptions.some(
    (option) => option.value === task.cronDesc,
  );
  formModel.cronDesc = isPreset ? task.cronDesc : '自定义 Cron';
  formModel.customCron = isPreset ? '' : task.cronDesc;
  formModel.remark = task.remark;

  formModel.runParams = {};
  for (const param of configInputParams.value) {
    formModel.runParams[param.paramKey] = '';
  }

  formOpen.value = true;
}

function validateCron(expr: string) {
  const parts = expr.trim().split(/\s+/);

  if (parts.length !== 5) {
    return false;
  }

  const pattern = /^(\*|\d+(?:-\d+)?(?:\/\d+)?(?:,\d+(?:-\d+)?(?:\/\d+)?)*)$/;

  return parts.every((part) => pattern.test(part));
}

function handleSave() {
  if (!formModel.taskName.trim()) {
    window.message.warning('请输入任务名称');
    return;
  }

  let cronDesc = formModel.cronDesc;

  if (formModel.cronDesc === '自定义 Cron') {
    if (!validateCron(formModel.customCron)) {
      window.message.warning(
        'Cron 表达式格式不正确，需为 5 位（分 时 日 月 周），如 0 2 * * *',
      );
      return;
    }
    cronDesc = formModel.customCron.trim();
  }

  saving.value = true;
  try {
    updateTask(formModel.id, {
      cronDesc,
      remark: formModel.remark,
      taskName: formModel.taskName.trim(),
    });
    window.message.success('任务配置已保存');
    formOpen.value = false;
  } finally {
    saving.value = false;
  }
}

defineExpose({ open });
</script>

<template>
  <PlatformModal
    v-model:open="formOpen"
    :confirm-loading="saving"
    destroy-on-close
    title="配置任务"
    width="640px"
    @ok="handleSave"
  >
    <PlatformEditForm :model="formModel" class="task-config-form" layout="vertical">
      <PlatformFormItem label="任务名称" required>
        <PlatformInput
          v-model:value="formModel.taskName"
          :maxlength="100"
          placeholder="请输入任务名称"
        />
      </PlatformFormItem>
      <PlatformFormItem label="执行周期" required>
        <PlatformSelect
          v-model:value="formModel.cronDesc"
          :options="cronPresetOptions"
          placeholder="请选择执行周期"
        />
      </PlatformFormItem>
      <PlatformFormItem
        v-if="formModel.cronDesc === '自定义 Cron'"
        label="自定义 Cron 表达式"
        required
      >
        <PlatformInput
          v-model:value="formModel.customCron"
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
            v-model:value="formModel.runParams[param.paramKey]"
            :placeholder="param.defaultValue ? `留空使用默认值 ${param.defaultValue}` : '请输入运行参数'"
          />
        </PlatformFormItem>
      </template>
      <PlatformFormItem label="备注说明">
        <PlatformInput
          v-model:value="formModel.remark"
          :maxlength="200"
          placeholder="请输入备注（≤200 字符）"
        />
      </PlatformFormItem>
    </PlatformEditForm>
  </PlatformModal>
</template>

<style scoped>
.task-config-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.task-config-form > :first-child {
  grid-column: 1 / -1;
}

@media (max-width: 720px) {
  .task-config-form {
    grid-template-columns: 1fr;
  }
}
</style>
