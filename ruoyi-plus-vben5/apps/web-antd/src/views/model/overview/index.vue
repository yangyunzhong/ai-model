<script setup lang="ts">
import type {
  CatalogVO,
  DefinitionVO,
  ExecLogVO,
  ResultVO,
} from '#/api/model/model';

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
  catalogList,
  definitionList,
  execLogList,
  resultList,
} from '#/api/model';

import ModelDetailDrawer from './modal/model-detail-drawer.vue';

interface OverviewFilter {
  categoryId: undefined | number | string;
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

/** 真实数据源 */
const definitions = ref<DefinitionVO[]>([]);
const catalogs = ref<CatalogVO[]>([]);
const execLogs = ref<ExecLogVO[]>([]);
const results = ref<ResultVO[]>([]);

const registerTypeOptions = [
  { label: '本地注册', value: '本地注册' },
  { label: '第三方注册', value: '第三方注册' },
];
const statusOptions = (['草稿', '已发布', '已下架'] as const).map((item) => ({
  label: item,
  value: item,
}));

/** 模型状态展示元数据（与后端 status 取值一致） */
const modelStatusMeta: Record<
  string,
  { label: string; status: 'default' | 'success' | 'warning' }
> = {
  已下架: { label: '已下架', status: 'default' },
  已归档: { label: '已归档', status: 'default' },
  已发布: { label: '已发布', status: 'success' },
  草稿: { label: '草稿', status: 'warning' },
};

/** 执行状态展示元数据（对应 model_exec_log.status） */
const execStatusMeta: Record<
  string,
  { label: string; status: 'default' | 'error' | 'processing' | 'success' | 'warning' }
> = {
  成功: { label: '成功', status: 'success' },
  失败: { label: '失败', status: 'error' },
  运行中: { label: '运行中', status: 'processing' },
  部分成功: { label: '部分成功', status: 'warning' },
  超时: { label: '超时', status: 'error' },
};

function statusMetaOf(status?: string) {
  return modelStatusMeta[status ?? ''] ?? { label: status ?? '-', status: 'default' as const };
}

function execMetaOf(status?: string) {
  return execStatusMeta[status ?? ''] ?? { label: status ?? '-', status: 'default' as const };
}

const categoryOptions = computed(() =>
  catalogs.value.map((item) => ({ label: item.catalogName, value: item.id })),
);
const catalogNameMap = computed<Record<string, string>>(() =>
  Object.fromEntries(catalogs.value.map((item) => [String(item.id), item.catalogName])),
);

function getCategoryName(catalogId?: string | number) {
  if (catalogId === undefined || catalogId === null) {
    return '-';
  }
  return catalogNameMap.value[String(catalogId)] ?? '-';
}

function toTimestamp(value?: string) {
  if (!value) {
    return 0;
  }
  const time = new Date(value.replace('T', ' ').replaceAll('-', '/')).getTime();
  return Number.isNaN(time) ? 0 : time;
}

/** 模型ID -> 最近一次执行记录（用于卡片「最近运行」） */
const lastRunMap = computed<Record<string, ExecLogVO>>(() => {
  const map: Record<string, ExecLogVO> = {};
  for (const log of execLogs.value) {
    const key = String(log.modelId);
    const current = map[key];
    if (!current || toTimestamp(log.startTime) > toTimestamp(current.startTime)) {
      map[key] = log;
    }
  }
  return map;
});

/** 模型ID -> 执行结果明细（用于设备维度过滤与高危统计） */
const resultsByModel = computed<Record<string, ResultVO[]>>(() => {
  const map: Record<string, ResultVO[]> = {};
  for (const row of results.value) {
    const key = String(row.modelId);
    (map[key] ??= []).push(row);
  }
  return map;
});

function isDangerLevel(level?: string) {
  return level === 'danger' || level === '高危';
}

const statCards = computed<StatCardItem[]>(() => {
  const failedRecent = execLogs.value.filter((item) => {
    if (item.status !== '失败') {
      return false;
    }
    return Date.now() - toTimestamp(item.startTime) <= 7 * 24 * 60 * 60 * 1000;
  }).length;

  const riskDeviceIds = new Set<string>();
  for (const row of results.value) {
    if (isDangerLevel(row.healthLevel)) {
      riskDeviceIds.add(String(row.deviceId));
    }
  }

  return [
    {
      description: '全部状态模型数',
      title: '模型总数',
      type: 'primary',
      unit: '个',
      value: definitions.value.length,
    },
    {
      description: '按设备类型划分',
      title: '模型分类数',
      type: 'info',
      unit: '个',
      value: catalogs.value.length,
    },
    {
      description: '脚本上传至本平台',
      title: '本地注册数',
      type: 'success',
      unit: '个',
      value: definitions.value.filter((item) => item.registerType === '本地注册').length,
    },
    {
      description: '外部系统登记',
      title: '第三方注册数',
      type: 'warning',
      unit: '个',
      value: definitions.value.filter((item) => item.registerType === '第三方注册').length,
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

  return definitions.value.filter((model) => {
    if (
      filter.value.categoryId !== undefined &&
      String(model.catalogId) !== String(filter.value.categoryId)
    ) {
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
    if (deviceFilterIds.value.length > 0) {
      const rows = resultsByModel.value[String(model.id)] ?? [];
      if (!rows.some((row) => deviceFilterIds.value.includes(String(row.deviceId)))) {
        return false;
      }
    }
    return true;
  });
});

const deviceFilterLabel = computed(() => deviceFilterIds.value.join('、'));

const deviceMatchCount = computed(() =>
  deviceFilterIds.value.length > 0 ? filteredModels.value.length : 0,
);

function lastRunOf(model: DefinitionVO) {
  return lastRunMap.value[String(model.id)];
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

function openDetail(model: DefinitionVO) {
  detailDrawerRef.value?.open(model);
}

function goCompare(model: DefinitionVO) {
  router.push({
    path: '/model-comparison/task',
    query: {
      deviceType: model.deviceType,
      modelId: String(model.id),
    },
  });
}

function goResult(model: DefinitionVO) {
  router.push({
    path: '/model-manage/list',
    query: {
      categoryId: String(model.catalogId),
      keyword: model.modelName,
    },
  });
}

function clearDeviceFilter() {
  deviceFilterIds.value = [];
}

async function loadData() {
  const [defRes, catRes, logRes, resultRes] = await Promise.all([
    definitionList({ pageNum: 1, pageSize: 500 }),
    catalogList({ pageNum: 1, pageSize: 200 }),
    execLogList({ pageNum: 1, pageSize: 500 }),
    resultList({ pageNum: 1, pageSize: 1000 }),
  ]);
  definitions.value = defRes.rows ?? [];
  catalogs.value = catRes.rows ?? [];
  execLogs.value = logRes.rows ?? [];
  results.value = resultRes.rows ?? [];
}

onMounted(() => {
  const rawDeviceId = route.query.deviceId;

  if (typeof rawDeviceId === 'string' && rawDeviceId.trim()) {
    deviceFilterIds.value = rawDeviceId
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean);
  }

  loadData();
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
              :label="statusMetaOf(model.status).label"
              :status="statusMetaOf(model.status).status"
            />
          </header>

          <p class="model-overview-card__time">注册时间：{{ model.registerTime || '暂无' }}</p>

          <div class="model-overview-card__tags">
            <PlatformStatusTag
              :label="getCategoryName(model.catalogId)"
              status="processing"
            />
            <PlatformStatusTag
              :label="model.registerType || '-'"
              status="default"
            />
          </div>

          <div class="model-overview-card__run">
            <span>最近运行</span>
            <template v-if="lastRunOf(model)">
              <strong>{{ lastRunOf(model)?.startTime || '暂无' }}</strong>
              <PlatformStatusTag
                :label="execMetaOf(lastRunOf(model)?.status).label"
                :status="execMetaOf(lastRunOf(model)?.status).status"
                variant="dot"
              />
            </template>
            <strong v-else>暂无</strong>
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
