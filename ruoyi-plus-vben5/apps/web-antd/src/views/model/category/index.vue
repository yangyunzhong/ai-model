<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { DeviceTypeOption, ModelCategory } from '../data';

import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformEditForm,
  PlatformFormItem,
  PlatformIcon,
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
  addCategory,
  categories,
  countModelsByCategory,
  deviceTypeOptions,
  isCategoryNameDuplicated,
  removeCategory,
  updateCategory,
} from '../data';

const router = useRouter();
const loading = ref(false);

const tableColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'categoryName', key: 'categoryName', title: '类别名称', width: 160 },
  { dataIndex: 'deviceType', key: 'deviceType', title: '关联设备类型', width: 140 },
  { key: 'modelCount', title: '模型数量', width: 110 },
  { dataIndex: 'createTime', key: 'createTime', title: '创建时间', width: 180 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 96 },
  { fixed: 'right', key: 'action', title: '操作', width: 140 },
]);

const pagination = computed(() => ({
  pageSize: 10,
  showTotal: (total: number) => `共 ${total} 条`,
  total: categories.value.length,
}));

function statusMetaOf(status: ModelCategory['status']) {
  return status === '启用'
    ? { label: '启用', status: 'success' as const }
    : { label: '停用', status: 'default' as const };
}

function goModelManage(category: ModelCategory) {
  window.message.info(`已跳转模型管理并按「${category.categoryName}」过滤`);
  router.push({
    path: '/model-manage/list',
    query: { categoryId: category.id },
  });
}

// ==================== 新增 / 编辑类别 ====================
const formOpen = ref(false);
const saving = ref(false);
const editingId = ref<undefined | number>();
const editingHasModels = ref(false);
const formModel = reactive({
  categoryName: '',
  deviceType: undefined as undefined | DeviceTypeOption,
  remark: '',
  status: '启用' as ModelCategory['status'],
});

const formTitle = computed(() => (editingId.value ? '编辑类别' : '新增类别'));

function openCreate() {
  editingId.value = undefined;
  editingHasModels.value = false;
  formModel.categoryName = '';
  formModel.deviceType = undefined;
  formModel.remark = '';
  formModel.status = '启用';
  formOpen.value = true;
}

function openEdit(category: ModelCategory) {
  editingId.value = category.id;
  editingHasModels.value = countModelsByCategory(category.id) > 0;
  formModel.categoryName = category.categoryName;
  formModel.deviceType = category.deviceType;
  formModel.remark = category.remark;
  formModel.status = category.status;
  formOpen.value = true;
}

async function handleSave() {
  if (!formModel.categoryName.trim()) {
    window.message.warning('请输入类别名称');
    return;
  }
  if (isCategoryNameDuplicated(formModel.categoryName, editingId.value)) {
    window.message.error('类别名称已存在，请修改');
    return;
  }
  if (formModel.deviceType === undefined) {
    window.message.warning('请选择关联设备类型');
    return;
  }

  saving.value = true;
  try {
    if (editingId.value) {
      updateCategory(editingId.value, {
        categoryName: formModel.categoryName.trim(),
        deviceType: formModel.deviceType,
        remark: formModel.remark,
        status: formModel.status,
      });
      window.message.success('类别已更新');
    } else {
      addCategory({
        categoryName: formModel.categoryName.trim(),
        deviceType: formModel.deviceType,
        remark: formModel.remark,
        status: formModel.status,
      });
      window.message.success('类别已新增');
    }
    formOpen.value = false;
  } finally {
    saving.value = false;
  }
}

function handleDelete(category: ModelCategory) {
  if (countModelsByCategory(category.id) > 0) {
    window.message.error('该类别下存在模型，请先移除或迁移该类别下的模型');
    return;
  }
  removeCategory(category.id);
  window.message.success('类别已删除');
}
</script>

<template>
  <Page>
    <div class="model-category-page">
      <PlatformViewToolbar
        description="按设备类型维护模型分类基础数据"
        title="模型类别"
      />

      <section class="platform-surface model-category-surface">
        <PlatformTableToolbar :tools="['refresh', 'setting', 'fullscreen']">
          <template #actions>
            <PlatformButton scene="toolbar" type="primary" @click="openCreate">
              <template #icon>
                <PlatformIcon icon="icon-xinzeng" />
              </template>
              新增类别
            </PlatformButton>
          </template>
        </PlatformTableToolbar>

        <PlatformTable
          :columns="tableColumns"
          :data-source="categories"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelCount'">
              <PlatformButton
                scene="action"
                size="small"
                type="link"
                @click="goModelManage(record as ModelCategory)"
              >
                {{ countModelsByCategory((record as ModelCategory).id) }} 个
              </PlatformButton>
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="statusMetaOf((record as ModelCategory).status).label"
                :status="statusMetaOf((record as ModelCategory).status).status"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <Space :size="0">
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="openEdit(record as ModelCategory)"
                >
                  编辑
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认删除该类别？"
                  @confirm="handleDelete(record as ModelCategory)"
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
            v-model:value="formModel.categoryName"
            :maxlength="50"
            placeholder="请输入类别名称（全局唯一，≤50 字符）"
          />
        </PlatformFormItem>
        <PlatformFormItem
          label="关联设备类型"
          required
        >
          <PlatformSelect
            v-model:value="formModel.deviceType"
            :disabled="editingHasModels"
            :options="deviceTypeOptions"
            :placeholder="editingHasModels ? '已被模型引用，不允许修改' : '请选择关联设备类型'"
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
            v-model:value="formModel.remark"
            :maxlength="200"
            placeholder="请输入备注（≤200 字符）"
          />
        </PlatformFormItem>
      </PlatformEditForm>
    </PlatformModal>
  </Page>
</template>

<style scoped>
.model-category-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.model-category-surface {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  padding: var(--st-module-content-padding);
}
</style>
