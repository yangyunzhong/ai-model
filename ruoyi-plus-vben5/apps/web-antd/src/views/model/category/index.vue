<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { CatalogVO } from '#/api/model/model';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformIcon,
  PlatformStatusTag,
  PlatformTable,
  PlatformTableToolbar,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Popconfirm, Space } from 'antdv-next';

import { catalogList, catalogRemove } from '#/api/model';

import CategoryModal from './modal/category-modal.vue';

const loading = ref(false);
const rows = ref<CatalogVO[]>([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});

const tableColumns = computed<TableProps['columns']>(() => [
  { dataIndex: 'catalogName', key: 'catalogName', title: '类别名称', width: 160 },
  { dataIndex: 'deviceType', key: 'deviceType', title: '关联设备类型', width: 140 },
  { key: 'modelCount', title: '模型数量', width: 110 },
  { dataIndex: 'createTime', key: 'createTime', title: '创建时间', width: 180 },
  { dataIndex: 'status', key: 'status', title: '状态', width: 96 },
  { fixed: 'right', key: 'action', title: '操作', width: 140 },
]);

const pagination = computed(() => ({
  current: queryParams.pageNum,
  pageSize: queryParams.pageSize,
  showTotal: (t: number) => `共 ${t} 条`,
  total: total.value,
}));

function statusMetaOf(status?: string) {
  return status === '停用'
    ? { label: '停用', status: 'default' as const }
    : { label: '启用', status: 'success' as const };
}

async function loadList() {
  loading.value = true;
  try {
    const res = await catalogList({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
    });
    rows.value = res.rows ?? [];
    total.value = res.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handleTableChange(page: { current?: number; pageSize?: number }) {
  queryParams.pageNum = Number(page?.current ?? 1);
  queryParams.pageSize = Number(page?.pageSize ?? 10);
  loadList();
}

// ==================== 新增 / 编辑类别 ====================
const categoryModalRef = ref<InstanceType<typeof CategoryModal>>();

function handleAdd() {
  categoryModalRef.value?.openCreate();
}

function handleEdit(category: CatalogVO) {
  categoryModalRef.value?.openEdit(category);
}

async function handleDelete(category: CatalogVO) {
  // deleteWithMsg 已自动弹成功提示
  await catalogRemove(category.id);
  if (rows.value.length === 1 && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await loadList();
}

onMounted(loadList);
</script>

<template>
  <Page>
    <div class="model-category-page">
      <PlatformViewToolbar
        description="按设备类型维护模型分类基础数据"
        title="模型类别"
      />

      <section class="platform-surface model-category-surface">
        <PlatformTableToolbar
          :tools="['refresh', 'setting', 'fullscreen']"
          @refresh="loadList"
        >
          <template #actions>
            <PlatformButton scene="toolbar" type="primary" @click="handleAdd">
              <template #icon>
                <PlatformIcon icon="icon-xinzeng" />
              </template>
              新增类别
            </PlatformButton>
          </template>
        </PlatformTableToolbar>

        <PlatformTable
          :columns="tableColumns"
          :data-source="rows"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          size="middle"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'modelCount'">
              <span class="model-category-count">{{ (record as CatalogVO).modelCount ?? 0 }}</span>
            </template>
            <template v-else-if="column.key === 'status'">
              <PlatformStatusTag
                :label="statusMetaOf((record as CatalogVO).status).label"
                :status="statusMetaOf((record as CatalogVO).status).status"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <Space :size="0">
                <PlatformButton
                  scene="action"
                  size="small"
                  type="link"
                  @click="handleEdit(record as CatalogVO)"
                >
                  编辑
                </PlatformButton>
                <Popconfirm
                  placement="left"
                  title="确认删除该类别？"
                  @confirm="handleDelete(record as CatalogVO)"
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

    <CategoryModal ref="categoryModalRef" @success="loadList" />
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

.model-category-count {
  font-weight: 600;
  color: hsl(var(--foreground));
}
</style>