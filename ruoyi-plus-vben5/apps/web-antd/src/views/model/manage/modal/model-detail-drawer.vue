<script setup lang="ts">
import type {
  CatalogVO,
  DefinitionVO,
  ExecLogVO,
  ParamVO,
} from '#/api/model/model';

import { computed, ref } from 'vue';

import {
  PlatformDescriptions,
  PlatformDrawer,
  PlatformStatusTag,
} from '@st/platform-ui';

import { catalogList, execLogList, paramList } from '#/api/model';

const detailOpen = ref(false);
const detailModel = ref<null | DefinitionVO>(null);
const inputParams = ref<ParamVO[]>([]);
const outputParams = ref<ParamVO[]>([]);
const latestExec = ref<null | ExecLogVO>(null);
const catalogNameMap = ref<Record<string, string>>({});

let catalogsLoaded = false;

async function loadCategories() {
  const res = await catalogList({ pageNum: 1, pageSize: 200 });
  const list: CatalogVO[] = res.rows ?? [];
  catalogNameMap.value = Object.fromEntries(
    list.map((item) => [String(item.id), item.catalogName]),
  );
}

function catalogNameOf(catalogId?: string | number) {
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

/** 运行时容器状态：后端英文枚举 → 中文展示 */
const containerStatusLabels: Record<string, string> = {
  running: '运行中',
  missing: '容器缺失',
  disabled: '运行时未启用',
};

function containerStatusLabel(status?: string) {
  if (!status) {
    return '未发布';
  }
  return containerStatusLabels[status] ?? status;
}

const descriptionItems = computed(() => {
  const model = detailModel.value;

  if (!model) {
    return [];
  }

  const run = latestExec.value;

  return [
    { content: catalogNameOf(model.catalogId), label: '模型分类' },
    { content: model.registerType || '-', label: '注册方式' },
    { content: `${model.statisticsCycleDays ?? '-'} 天`, label: '统计周期' },
    { content: model.status || '-', label: '模型状态' },
    { content: containerStatusLabel(model.containerStatus), label: '容器状态' },
    { content: model.registerTime || '-', label: '注册时间' },
    { content: model.lastPublishTime || '-', label: '最近发布' },
    {
      content: run ? `${run.startTime ?? '-'}（${run.status ?? '-'}）` : '暂无',
      label: '最近运行',
    },
    { content: model.description || '-', label: '模型简介' },
  ];
});

/** 打开模型详情抽屉 */
async function open(model: DefinitionVO) {
  detailModel.value = model;
  detailOpen.value = true;
  inputParams.value = [];
  outputParams.value = [];
  latestExec.value = null;

  if (!catalogsLoaded) {
    await loadCategories();
    catalogsLoaded = true;
  }

  const [paramRes, execRes] = await Promise.all([
    paramList({ modelId: model.id, pageNum: 1, pageSize: 200 }),
    execLogList({ modelId: model.id, pageNum: 1, pageSize: 50 }),
  ]);

  const list: ParamVO[] = paramRes.rows ?? [];
  inputParams.value = list.filter((item) => item.paramDirection === '输入');
  outputParams.value = list.filter((item) => item.paramDirection === '输出');

  let latest: null | ExecLogVO = null;
  for (const log of execRes.rows ?? []) {
    if (!latest || toTimestamp(log.startTime) > toTimestamp(latest.startTime)) {
      latest = log;
    }
  }
  latestExec.value = latest;
}

defineExpose({ open });
</script>

<template>
  <PlatformDrawer
    v-model:open="detailOpen"
    destroy-on-close
    size="large"
    title="模型详情"
  >
    <div v-if="detailModel" class="model-detail-drawer">
      <PlatformDescriptions :items="descriptionItems" />

      <h4 class="model-detail-drawer__subtitle">输入参数</h4>
      <ul class="model-detail-drawer__param-group">
        <li v-for="param in inputParams" :key="param.id">
          <span class="model-detail-drawer__param-key">{{ param.paramKey }}</span>
          <span>{{ param.paramName }}</span>
          <PlatformStatusTag :label="param.paramType" status="processing" />
          <PlatformStatusTag v-if="param.required === '1'" label="必填" status="warning" />
          <span>默认值：{{ param.defaultValue || '-' }}</span>
        </li>
        <li v-if="inputParams.length === 0" class="model-detail-drawer__empty">暂无输入参数</li>
      </ul>

      <h4 class="model-detail-drawer__subtitle">输出参数</h4>
      <ul class="model-detail-drawer__param-group">
        <li v-for="param in outputParams" :key="param.id">
          <span class="model-detail-drawer__param-key">{{ param.paramKey }}</span>
          <span>{{ param.paramName }}</span>
          <PlatformStatusTag :label="param.paramType" status="processing" />
        </li>
        <li v-if="outputParams.length === 0" class="model-detail-drawer__empty">暂无输出参数</li>
      </ul>
    </div>
  </PlatformDrawer>
</template>

<style scoped>
.model-detail-drawer__subtitle {
  margin: 16px 0 8px;
  font-size: 14px;
  font-weight: 600;
}

.model-detail-drawer__param-group {
  display: grid;
  gap: 6px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.model-detail-drawer__param-group li {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  font-size: 13px;
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-control);
}

.model-detail-drawer__empty {
  color: hsl(var(--muted-foreground));
}

.model-detail-drawer__param-key {
  font-family: monospace;
  color: hsl(var(--primary));
}
</style>
