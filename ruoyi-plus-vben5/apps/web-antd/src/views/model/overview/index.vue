<script setup lang="ts">
import type { ModelRecord } from '../data';

import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformInput,
  PlatformQueryPanel,
  PlatformSelect,
  PlatformStatusTag,
  PlatformStatCard,
  PlatformViewToolbar,
} from '@st/platform-ui';

import {
  categories,
  execRecords,
  execStatusMeta,
  getCategoryDeviceType,
  getModelResultRows,
  modelStatusMeta,
  models,
  registerTypeOptions,
} from '../data';

import ModelDetailDrawer from './modal/model-detail-drawer.vue';

interface OverviewFilter {
  categoryId: undefined | number;
  keyword: string;
  registerType: string;
  status: string;
}

interface StatCardItem {
  description: string;
  title: string;
  type: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  unit: string;
  value: number | string;
}

const router = useRouter();
const route = useRoute();
const filter = ref<OverviewFilter>({
  categoryId: undefined,
  keyword: '',
  registerType: '',
  status: '',
});
const deviceFilterIds = ref<string[]>([]);
const detailDrawerRef = ref<InstanceType<typeof ModelDetailDrawer>>();

const categoryOptions = computed(() =>
  categories.value.map((item) => ({ label: item.categoryName, value: item.id })),
);
const statusOptions = computed(() =>
  (['草稿', '已发布', '已下架'] as const).map((item) => ({ label: item, value: item })),
);

const statCards = computed<StatCardItem[]>(() => {
  const failedRecent = execRecords.value.filter((item) => {
    if (item.status !== '失败') {
      return false;
    }
    const startTime = new Date(item.startTime.replace(/-/g, '/')).getTime();
    return Date.now() - startTime <= 7 * 24 * 60 * 60 * 1000;
  }).length;

  const riskDeviceIds = new Set<string>();

  for (const model of models.value) {
    if (model.status !== '已发布') {
      continue;
    }
    for (const row of getModelResultRows(model.id)) {
      if (row.healthLevel === '高危') {
        riskDeviceIds.add(row.deviceId);
      }
    }
  }

  return [
    {
      description: '全部状态模型数',
      title: '模型总数',
      type: 'primary',
      unit: '个',
      value: models.value.length,
    },
    {
      description: '按设备类型划分',
      title: '模型分类数',
      type: 'info',
      unit: '个',
      value: categories.value.length,
    },
    {
      description: '脚本上传至本平台',
      title: '本地注册数',
      type: 'success',
      unit: '个',
      value: models.value.filter((item) => item.registerType === '本地注册').length,
    },
    {
      description: '外部系统登记',
      title: '第三方注册数',
      type: 'warning',
      unit: '个',
      value: models.value.filter((item) => item.registerType === '第三方注册').length,
    },
    {
      description: '执行失败需关注',
      title: '近7天运行失败',
      type: 'danger',
      unit: '次',
      value: failedRecent,
    },
    {
      description: '最近评估高危设备',
      title: '高危设备数',
      type: 'danger',
      unit: '台',
      value: riskDeviceIds.size,
    },
  ];
});

const filteredModels = computed(() => {
  const keyword = filter.value.keyword.trim().toLowerCase();

  return models.value.filter((model) => {
    if (filter.value.categoryId !== undefined && model.categoryId !== filter.value.categoryId) {
      return false;
    }
    if (filter.value.status && model.status !== filter.value.status) {
      return false;
    }
    if (filter.value.registerType && model.registerType !== filter.value.registerType) {
      return false;
    }
    if (keyword && !model.modelName.toLowerCase().includes(keyword)) {
      return false;
    }
    if (
      deviceFilterIds.value.length > 0 &&
      !getModelResultRows(model.id).some((row) =>
        deviceFilterIds.value.includes(row.deviceId),
      )
    ) {
      return false;
    }
    return true;
  });
});

const deviceFilterLabel = computed(() => deviceFilterIds.value.join('、'));

const deviceMatchCount = computed(() =>
  deviceFilterIds.value.length > 0 ? filteredModels.value.length : 0,
);

function getCategoryName(categoryId: number) {
  return categories.value.find((item) => item.id === categoryId)?.categoryName ?? '-';
}

function handleReset() {
  filter.value = {
    categoryId: undefined,
    keyword: '',
    registerType: '',
    status: '',
  };
  deviceFilterIds.value = [];
}

function openDetail(model: ModelRecord) {
  detailDrawerRef.value?.open(model);
}

function goCompare(model: ModelRecord) {
  router.push({
    path: '/model-comparison/task',
    query: {
      deviceType: getCategoryDeviceType(model.categoryId),
      modelId: String(model.id),
    },
  });
}

function goResult(model: ModelRecord) {
  router.push({
    path: '/model-manage/list',
    query: {
      categoryId: String(model.categoryId),
      keyword: model.modelName,
    },
  });
}

function clearDeviceFilter() {
  deviceFilterIds.value = [];
}

onMounted(() => {
  const rawDeviceId = route.query.deviceId;

  if (typeof rawDeviceId === 'string' && rawDeviceId.trim()) {
    deviceFilterIds.value = rawDeviceId
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean);
  }
});
</script>

<template>
  <Page>
    <div class="model-overview-page">
      <PlatformViewToolbar
        description="模型资产、运行健康与高危设备全局视图"
        title="模型概览"
      />

      <section class="model-overview-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          v-bind="card"
        />
      </section>

      <PlatformQueryPanel :columns="4" :show-query="false" @reset="handleReset">
        <PlatformSelect
          v-model:value="filter.categoryId"
          :options="categoryOptions"
          allow-clear
          placeholder="请选择模型分类"
        />
        <PlatformSelect
          v-model:value="filter.status"
          :options="statusOptions"
          allow-clear
          placeholder="请选择模型状态"
        />
        <PlatformSelect
          v-model:value="filter.registerType"
          :options="registerTypeOptions"
          allow-clear
          placeholder="请选择注册方式"
        />
        <PlatformInput
          v-model:value="filter.keyword"
          allow-clear
          placeholder="搜索模型名称"
        />
      </PlatformQueryPanel>

      <div v-if="deviceFilterIds.length > 0" class="model-overview-device-hint">
        <span class="model-overview-device-hint__text">
          已按设备
          <strong>{{ deviceFilterLabel }}</strong>
          过滤，命中
          <strong>{{ deviceMatchCount }}</strong>
          个模型
        </span>
        <PlatformButton
          scene="action"
          size="small"
          type="link"
          @click="clearDeviceFilter"
        >
          清除设备筛选
        </PlatformButton>
      </div>

      <section class="model-overview-card-grid">
        <article
          v-for="model in filteredModels"
          :key="model.id"
          class="model-overview-card"
          @click="openDetail(model)"
        >
          <header class="model-overview-card__header">
            <h3>{{ model.modelName }}</h3>
            <PlatformStatusTag
              :label="modelStatusMeta[model.status].label"
              :status="modelStatusMeta[model.status].status"
            />
          </header>

          <p class="model-overview-card__time">注册时间：{{ model.registerTime }}</p>

          <div class="model-overview-card__tags">
            <PlatformStatusTag
              :label="getCategoryName(model.categoryId)"
              status="processing"
            />
            <PlatformStatusTag
              :label="model.registerType"
              status="default"
            />
          </div>

          <div class="model-overview-card__run">
            <span>最近运行</span>
            <strong>{{ model.lastRunTime || '暂无' }}</strong>
            <PlatformStatusTag
              v-if="model.lastRunStatus"
              :label="execStatusMeta[model.lastRunStatus].label"
              :status="execStatusMeta[model.lastRunStatus].status"
              variant="dot"
            />
          </div>

          <footer class="model-overview-card__entries">
            <PlatformButton
              scene="action"
              size="small"
              type="link"
              @click.stop="goResult(model)"
            >
              查看结果
            </PlatformButton>
            <PlatformButton
              scene="action"
              size="small"
              type="link"
              @click.stop="goCompare(model)"
            >
              去比对
            </PlatformButton>
          </footer>
        </article>

        <div v-if="filteredModels.length === 0" class="model-overview-empty">
          没有符合条件的模型
        </div>
      </section>
    </div>

    <!-- 模型详情 -->
    <ModelDetailDrawer ref="detailDrawerRef" />
  </Page>
</template>

<style scoped>
.model-overview-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.model-overview-stat-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.model-overview-card-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.model-overview-device-hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 18px;
  background: hsl(var(--primary) / 10%);
  border-radius: 8px;
}

.model-overview-device-hint__text {
  color: hsl(var(--foreground));
  font-size: 14px;
  line-height: 22px;
}

.model-overview-device-hint__text strong {
  color: hsl(var(--primary));
  font-weight: 700;
}

.model-overview-card {
  padding: var(--st-module-content-padding);
  overflow: hidden;
  cursor: pointer;
  background: hsl(var(--st-color-card-bg));
  border-radius: var(--st-radius-card);
  box-shadow: var(--st-shadow-card);
  transition: transform 0.16s ease;
}

.model-overview-card:hover {
  transform: translateY(-2px);
}

.model-overview-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.model-overview-card__header h3 {
  margin: 0;
  font-size: 16px;
  line-height: 24px;
  color: hsl(var(--foreground));
  overflow-wrap: anywhere;
}

.model-overview-card__time {
  margin: 6px 0 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-overview-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.model-overview-card__run {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
  border-top: 1px dashed hsl(var(--st-color-border-control));
}

.model-overview-card__run strong {
  color: hsl(var(--foreground));
}

.model-overview-card__entries {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 6px;
}

.model-overview-empty {
  grid-column: 1 / -1;
  padding: 40px 0;
  text-align: center;
  color: hsl(var(--muted-foreground));
}

@media (max-width: 1440px) {
  .model-overview-stat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .model-overview-card-grid,
  .model-overview-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .model-overview-card-grid,
  .model-overview-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
