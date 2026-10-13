<script setup lang="ts">
import type { TaskForm, TaskVO } from '#/api/model/model';

import { reactive, ref } from 'vue';

import {
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
} from '@st/platform-ui';
import { DatePicker, Select, TimePicker } from 'antdv-next';

import { taskUpdate } from '#/api/model';

const emit = defineEmits<{ success: [] }>();

/** 调度类型：周期执行（cron）/ 一次性执行（指定日期时间） */
const scheduleTypeOptions = [
  { label: '周期执行', value: 'cron' },
  { label: '一次性执行', value: 'once' },
];

/** 周期执行频率 */
const frequencyOptions = [
  { label: '每天', value: 'daily' },
  { label: '每周', value: 'weekly' },
  { label: '每月', value: 'monthly' },
];

/** 周几选项（unix cron：1=周一 … 7=周日） */
const weekdayOptions = [
  { label: '周一', value: '1' },
  { label: '周二', value: '2' },
  { label: '周三', value: '3' },
  { label: '周四', value: '4' },
  { label: '周五', value: '5' },
  { label: '周六', value: '6' },
  { label: '周日', value: '7' },
];

/** 每月几号选项 */
const monthDayOptions = Array.from({ length: 31 }, (_, index) => ({
  label: `${index + 1} 日`,
  value: String(index + 1),
}));

const weekdayLabelMap = Object.fromEntries(
  weekdayOptions.map((item) => [item.value, item.label]),
);

const formOpen = ref(false);
const saving = ref(false);
const modelName = ref('-');

const formModel = reactive({
  /** 执行时间（HH:mm），周期与一次性共用 */
  execTime: '',
  /** 周期执行频率：daily / weekly / monthly */
  frequency: 'daily',
  id: '' as number | string,
  modelId: '' as number | string,
  /** 每月几号（1-31） */
  monthDay: '1',
  remark: '',
  /** 调度类型：cron / once */
  scheduleType: 'cron',
  taskName: '',
  /** 一次性执行日期（YYYY-MM-DD） */
  onceDate: '',
  /** 每周执行日（多选） */
  weekdays: [] as string[],
});

/** 打开配置弹窗并回显行数据 */
function open(task: TaskVO, name?: string) {
  modelName.value = name || '-';
  formModel.id = task.id;
  formModel.modelId = task.modelId;
  formModel.taskName = task.taskName ?? '';
  formModel.remark = task.remark ?? '';
  formModel.execTime = '';
  formModel.frequency = 'daily';
  formModel.monthDay = '1';
  formModel.onceDate = '';
  formModel.weekdays = [];

  const isOnce = task.scheduleType === 'once';
  formModel.scheduleType = isOnce ? 'once' : 'cron';
  if (isOnce) {
    // nextExecuteTime 格式：yyyy-MM-dd HH:mm:ss
    const [date, time] = (task.nextExecuteTime ?? '').split(' ');
    formModel.onceDate = date ?? '';
    formModel.execTime = (time ?? '').slice(0, 5);
  } else {
    restoreCron(task.cronExpression ?? '');
  }

  formOpen.value = true;
}

/** 将 5 位 cron（分 时 日 月 周）解析回表单 */
function restoreCron(expression: string) {
  const parts = expression.trim().split(/\s+/);
  if (parts.length !== 5) {
    return;
  }
  const [minute = '', hour = '', dayOfMonth = '', month = '', dayOfWeek = ''] =
    parts;
  formModel.execTime = `${padStartTwo(hour)}:${padStartTwo(minute)}`;
  if (month === '*' && dayOfMonth !== '*' && dayOfWeek === '*') {
    formModel.frequency = 'monthly';
    formModel.monthDay = dayOfMonth;
  } else if (dayOfMonth === '*' && dayOfWeek !== '*') {
    formModel.frequency = 'weekly';
    formModel.weekdays = dayOfWeek.split(',');
  } else {
    formModel.frequency = 'daily';
  }
}

function padStartTwo(value: string) {
  return value.padStart(2, '0');
}

function isValidTime(time: string) {
  return /^([01]\d|2[0-3]):([0-5]\d)$/.test(time);
}

/** 由表单生成 5 位 cron 与人类可读描述 */
function buildCron(): { cronDescription: string; cronExpression: string } | null {
  const time = formModel.execTime.trim();
  if (!isValidTime(time)) {
    window.message.warning('请选择有效的执行时间（时:分）');
    return null;
  }
  const [minute = '0', hour = '0'] = time
    .split(':')
    .map((item) => String(Number(item)));

  if (formModel.frequency === 'weekly') {
    if (formModel.weekdays.length === 0) {
      window.message.warning('请选择每周执行日');
      return null;
    }
    const days = [...new Set(formModel.weekdays)]
      .map((item) => String(Number(item)))
      .sort((a, b) => Number(a) - Number(b));
    const labels = days.map((item) => weekdayLabelMap[String(Number(item))]);
    return {
      cronDescription: `每周${labels.join('、')} ${time}`,
      cronExpression: `0 ${minute} ${hour} * ${days.join(',')}`,
    };
  }

  if (formModel.frequency === 'monthly') {
    const day = String(Number(formModel.monthDay));
    if (!/^[1-9]$|^[12]\d$|^3[01]$/.test(day)) {
      window.message.warning('请选择每月执行日期');
      return null;
    }
    return {
      cronDescription: `每月 ${day} 日 ${time}`,
      cronExpression: `0 ${minute} ${hour} ${day} *`,
    };
  }

  return {
    cronDescription: `每天 ${time}`,
    cronExpression: `0 ${minute} ${hour} * *`,
  };
}

async function handleSave() {
  if (!formModel.taskName.trim()) {
    window.message.warning('请输入任务名称');
    return;
  }
  if (!formModel.modelId) {
    window.message.warning('任务未关联模型，无法保存');
    return;
  }

  const payload: TaskForm = {
    id: formModel.id,
    modelId: formModel.modelId,
    remark: formModel.remark,
    scheduleType: formModel.scheduleType,
    taskName: formModel.taskName.trim(),
  };

  if (formModel.scheduleType === 'once') {
    const date = formModel.onceDate?.trim() ?? '';
    const time = formModel.execTime?.trim() ?? '';
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
      window.message.warning('请选择一次性执行的日期');
      return;
    }
    if (!isValidTime(time)) {
      window.message.warning('请选择有效的一次性执行时间（时:分）');
      return;
    }
    payload.cronDescription = `${date} ${time}（一次性）`;
    payload.nextExecuteTime = `${date} ${time}:00`;
  } else {
    const cron = buildCron();
    if (!cron) {
      return;
    }
    payload.cronDescription = cron.cronDescription;
    payload.cronExpression = cron.cronExpression;
  }

  saving.value = true;
  try {
    await taskUpdate(payload);
    formOpen.value = false;
    emit('success');
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
      <PlatformFormItem label="关联模型">
        <PlatformInput :value="modelName" disabled />
      </PlatformFormItem>
      <PlatformFormItem label="调度类型" required>
        <PlatformSelect
          v-model:value="formModel.scheduleType"
          :options="scheduleTypeOptions"
        />
      </PlatformFormItem>

      <template v-if="formModel.scheduleType === 'once'">
        <PlatformFormItem label="执行日期" required>
          <DatePicker
            v-model:value="formModel.onceDate"
            class="w-full"
            placeholder="请选择执行日期"
            value-format="YYYY-MM-DD"
          />
        </PlatformFormItem>
        <PlatformFormItem label="执行时间" required>
          <TimePicker
            v-model:value="formModel.execTime"
            class="w-full"
            format="HH:mm"
            placeholder="时:分"
            value-format="HH:mm"
          />
        </PlatformFormItem>
      </template>

      <template v-else>
        <PlatformFormItem label="执行频率" required>
          <PlatformSelect
            v-model:value="formModel.frequency"
            :options="frequencyOptions"
          />
        </PlatformFormItem>
        <PlatformFormItem
          v-if="formModel.frequency === 'weekly'"
          label="每周执行日"
          required
        >
          <Select
            v-model:value="formModel.weekdays"
            :options="weekdayOptions"
            mode="multiple"
            placeholder="请选择周几（可多选）"
          />
        </PlatformFormItem>
        <PlatformFormItem
          v-if="formModel.frequency === 'monthly'"
          label="每月执行日"
          required
        >
          <PlatformSelect
            v-model:value="formModel.monthDay"
            :options="monthDayOptions"
          />
        </PlatformFormItem>
        <PlatformFormItem label="执行时间" required>
          <TimePicker
            v-model:value="formModel.execTime"
            class="w-full"
            format="HH:mm"
            placeholder="时:分"
            value-format="HH:mm"
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
  display: flex;
  flex-direction: column;
  gap: 0;
}

.task-config-form :deep(.w-full) {
  width: 100%;
}
</style>
