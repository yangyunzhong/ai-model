<script setup lang="ts">
import { ref } from 'vue';

import { Form } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});

withDefaults(
  defineProps<{
    labelPreset?: 'default' | 'inline-compact';
    variant?: 'edit' | 'search';
  }>(),
  {
    labelPreset: 'default',
    variant: 'edit',
  },
);

const formRef = ref<any>();

defineExpose({
  clearValidate: (...args: any[]) => formRef.value?.clearValidate?.(...args),
  resetFields: (...args: any[]) => formRef.value?.resetFields?.(...args),
  scrollToField: (...args: any[]) => formRef.value?.scrollToField?.(...args),
  validate: (...args: any[]) => formRef.value?.validate?.(...args),
  validateFields: (...args: any[]) => formRef.value?.validateFields?.(...args),
});
</script>

<template>
  <Form
    ref="formRef"
    v-bind="$attrs"
    class="platform-form"
    :class="[`platform-form--${variant}`, `platform-form--label-${labelPreset}`]"
  >
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Form>
</template>

<style scoped>
.platform-form {
  width: 100%;
}

.platform-form--search {
  display: grid;
  grid-template-columns: var(--platform-search-form-columns, 1fr);
  gap: 16px 24px;
}

.platform-form--edit :deep(.ant-form-item) {
  margin-bottom: 18px;
}

.platform-form--label-inline-compact :deep(.ant-form-item-row) {
  display: flex;
  align-items: center;
}

.platform-form--label-inline-compact :deep(.ant-form-item-label) {
  flex: 0 0 96px;
  max-width: 96px;
  padding: 0 12px 0 0;
  text-align: right;
  white-space: nowrap;
}

.platform-form--label-inline-compact :deep(.ant-form-item-label > label) {
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  min-height: var(--st-control-height);
}

.platform-form--label-inline-compact
  :deep(.ant-form-item-label > label::after) {
  display: none;
  content: none;
}

.platform-form--label-inline-compact :deep(.ant-form-item-control) {
  flex: 1;
  min-width: 0;
}

.platform-form--label-inline-compact :deep(.ant-form-item-control-input) {
  min-height: var(--st-control-height);
}

.platform-form--label-inline-compact
  :deep(.ant-form-item-control-input-content) {
  width: 100%;
}
</style>
