<script setup lang="ts">
import type { CatalogVO } from '#/api/model/model';

import { computed, reactive, ref } from 'vue';

import {
  PlatformEditForm,
  PlatformFormItem,
  PlatformInput,
  PlatformModal,
  PlatformSelect,
} from '@st/platform-ui';

import { catalogAdd, catalogUpdate } from '#/api/model';

import { deviceTypeOptions } from '../../data';

const emit = defineEmits<{ success: [] }>();

const formOpen = ref(false);
const saving = ref(false);
const editingId = ref<number | string | undefined>();
const formModel = reactive({
  catalogName: '',
  deviceType: undefined as string | undefined,
  description: '',
  status: '启用',
});

const formTitle = computed(() =>
  editingId.value === undefined ? '新增类别' : '编辑类别',
);

function resetForm() {
  formModel.catalogName = '';
  formModel.deviceType = undefined;
  formModel.description = '';
  formModel.status = '启用';
}

/** 打开新增弹窗 */
function openCreate() {
  editingId.value = undefined;
  resetForm();
  formOpen.value = true;
}

/** 打开编辑弹窗并回显行数据 */
function openEdit(catalog: CatalogVO) {
  editingId.value = catalog.id;
  formModel.catalogName = catalog.catalogName ?? '';
  formModel.deviceType = catalog.deviceType || undefined;
  formModel.description = catalog.description ?? '';
  formModel.status = catalog.status || '启用';
  formOpen.value = true;
}

async function handleSave() {
  if (!formModel.catalogName.trim()) {
    window.message.warning('请输入类别名称');
    return;
  }
  if (formModel.deviceType === undefined) {
    window.message.warning('请选择关联设备类型');
    return;
  }

  saving.value = true;
  try {
    const payload = {
      catalogName: formModel.catalogName.trim(),
      deviceType: formModel.deviceType,
      description: formModel.description,
      status: formModel.status,
    };
    if (editingId.value === undefined) {
      await catalogAdd(payload);
    } else {
      await catalogUpdate({ ...payload, id: editingId.value });
    }
    formOpen.value = false;
    emit('success');
  } finally {
    saving.value = false;
  }
}

defineExpose({ openCreate, openEdit });
</script>

<template>
  <PlatformModal
    v-model:open="formOpen"
    :confirm-loading="saving"
    :title="formTitle"
    destroy-on-close
    width="560px"
    @ok="handleSave"
  >
    <PlatformEditForm :model="formModel" layout="vertical">
      <PlatformFormItem label="类别名称" required>
        <PlatformInput
          v-model:value="formModel.catalogName"
          :maxlength="50"
          placeholder="请输入类别名称（≤50 字符）"
        />
      </PlatformFormItem>
      <PlatformFormItem label="关联设备类型" required>
        <PlatformSelect
          v-model:value="formModel.deviceType"
          :options="deviceTypeOptions"
          placeholder="请选择关联设备类型"
        />
      </PlatformFormItem>
      <PlatformFormItem label="状态">
        <PlatformSelect
          v-model:value="formModel.status"
          :options="[
            { label: '启用', value: '启用' },
            { label: '停用', value: '停用' },
          ]"
          placeholder="请选择状态"
        />
      </PlatformFormItem>
      <PlatformFormItem label="备注说明">
        <PlatformInput
          v-model:value="formModel.description"
          :maxlength="200"
          placeholder="请输入备注（≤200 字符）"
        />
      </PlatformFormItem>
    </PlatformEditForm>
  </PlatformModal>
</template>