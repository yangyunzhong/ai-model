<script setup lang="ts">
import type { ModelRecord } from '../data';

import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformDescriptions,
  PlatformDrawer,
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
  getModelParams,
  getModelResultRows,
  modelStatusMeta,
  models,
  registerTypeOptions,
} from '../data';

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
const filter = ref<OverviewFilter>({
  categoryId: undefined,
  keyword: '',
  registerType: '',
  status: '',
});
const detailOpen = ref(false);
const currentModel = ref<null | ModelRecord>(null);

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
    return true;
  });
});

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
}

function openDetail(model: ModelRecord) {
  currentModel.value = model;
  detailOpen.value = true;
}

function goCompare(model: ModelRecord) {
  router.push({ path: '/model-comparison/task', query: { categoryId: model.categoryId } });
}

function goResult() {
  router.push('/model-manage/list');
}

const detailDescriptionItems = computed(() => {
  const model = currentModel.value;

  if (!model) {
    return [];
  }

  return [
    { content: getCategoryName(model.categoryId), label: '模型分类' },
    { content: model.registerType, label: '注册方式' },
    { content: `${model.statPeriod} 天`, label: '统计周期' },
    { content: model.status, label: '模型状态' },
    { content: model.enabled ? '已开启' : '未开启', label: '自动任务' },
    { content: model.registerTime, label: '注册时间' },
    { content: model.description || '-', label: '模型简介' },
  ];
});

const detailInputParams = computed(() =>
  currentModel.value ? getModelParams(currentModel.value.id).input : [],
);
const detailOutputParams = computed(() =>
  currentModel.value ? getModelParams(currentModel.value.id).output : [],
);
const detailExecRecords = computed(() =>
  currentModel.value
    ? execRecords.value.filter((item) => item.modelId === currentModel.value?.id).slice(0, 3)
    : [],
);
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
              @click.stop="goResult"
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

    <PlatformDrawer
      v-model:open="detailOpen"
      destroy-on-close
      size="large"
      title="模型详情"
    >
      <div v-if="currentModel" class="model-overview-detail">
        <h4 class="model-overview-detail__title">{{ currentModel.modelName }}</h4>
        <PlatformDescriptions :items="detailDescriptionItems" />

        <h4 class="model-overview-detail__subtitle">输入参数</h4>
        <ul class="model-overview-param-group">
          <li v-for="param in detailInputParams" :key="param.paramKey">
            <span class="model-overview-param-key">{{ param.paramKey }}</span>
            <span>{{ param.paramName }}</span>
            <PlatformStatusTag :label="param.paramType" status="processing" />
            <PlatformStatusTag
              v-if="param.required"
              label="必填"
              status="warning"
            />
            <span>默认值：{{ param.defaultValue || '-' }}</span>
            <p>{{ param.description }}</p>
          </li>
        </ul>

        <h4 class="model-overview-detail__subtitle">输出参数</h4>
        <ul class="model-overview-param-group">
          <li v-for="param in detailOutputParams" :key="param.paramKey">
            <span class="model-overview-param-key">{{ param.paramKey }}</span>
            <span>{{ param.paramName }}</span>
            <PlatformStatusTag :label="param.paramType" status="processing" />
            <p>{{ param.description }}</p>
          </li>
        </ul>

        <h4 class="model-overview-detail__subtitle">最近执行</h4>
        <ul class="model-overview-exec-group">
          <li v-for="record in detailExecRecords" :key="record.execNo">
            <span class="model-overview-exec-no">{{ record.execNo }}</span>
            <span>{{ record.startTime }}</span>
            <PlatformStatusTag
              :label="execStatusMeta[record.status].label"
              :status="execStatusMeta[record.status].status"
            />
          </li>
          <li v-if="detailExecRecords.length === 0">暂无执行记录</li>
        </ul>
      </div>
    </PlatformDrawer>
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

.model-overview-detail__title {
  margin: 0 0 14px;
  font-size: 16px;
}

.model-overview-detail__subtitle {
  margin: 18px 0 10px;
  font-size: 14px;
  font-weight: 600;
}

.model-overview-param-group,
.model-overview-exec-group {
  display: grid;
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.model-overview-param-group li {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  font-size: 13px;
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-control);
}

.model-overview-param-group li p {
  flex-basis: 100%;
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-overview-param-key {
  font-family: monospace;
  color: hsl(var(--primary));
}

.model-overview-exec-group li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.model-overview-exec-no {
  font-family: monospace;
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
