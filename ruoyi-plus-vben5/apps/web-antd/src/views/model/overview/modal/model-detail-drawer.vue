<script setup lang="ts">
import type { ModelRecord } from '../../data';

import { computed, ref } from 'vue';

import {
  PlatformDescriptions,
  PlatformDrawer,
  PlatformStatusTag,
} from '@st/platform-ui';

import {
  categories,
  execRecords,
  execStatusMeta,
  getModelParams,
} from '../../data';

const detailOpen = ref(false);
const detailModel = ref<null | ModelRecord>(null);

const descriptionItems = computed(() => {
  const model = detailModel.value;

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

const inputParams = computed(() =>
  detailModel.value ? getModelParams(detailModel.value.id).input : [],
);
const outputParams = computed(() =>
  detailModel.value ? getModelParams(detailModel.value.id).output : [],
);
const recentExecRecords = computed(() =>
  detailModel.value
    ? execRecords.value
        .filter((item) => item.modelId === detailModel.value?.id)
        .slice(0, 3)
    : [],
);

function getCategoryName(categoryId: number) {
  return (
    categories.value.find((item) => item.id === categoryId)?.categoryName ?? '-'
  );
}

/** 打开模型详情抽屉 */
function open(model: ModelRecord) {
  detailModel.value = model;
  detailOpen.value = true;
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
      <h4 class="model-detail-drawer__title">{{ detailModel.modelName }}</h4>
      <PlatformDescriptions :items="descriptionItems" />

      <h4 class="model-detail-drawer__subtitle">输入参数</h4>
      <ul class="model-detail-drawer__param-group">
        <li v-for="param in inputParams" :key="param.paramKey">
          <span class="model-detail-drawer__param-key">{{ param.paramKey }}</span>
          <span>{{ param.paramName }}</span>
          <PlatformStatusTag :label="param.paramType" status="processing" />
          <PlatformStatusTag v-if="param.required" label="必填" status="warning" />
          <span>默认值：{{ param.defaultValue || '-' }}</span>
          <p>{{ param.description }}</p>
        </li>
      </ul>

      <h4 class="model-detail-drawer__subtitle">输出参数</h4>
      <ul class="model-detail-drawer__param-group">
        <li v-for="param in outputParams" :key="param.paramKey">
          <span class="model-detail-drawer__param-key">{{ param.paramKey }}</span>
          <span>{{ param.paramName }}</span>
          <PlatformStatusTag :label="param.paramType" status="processing" />
          <p>{{ param.description }}</p>
        </li>
      </ul>

      <h4 class="model-detail-drawer__subtitle">最近执行</h4>
      <ul class="model-detail-drawer__exec-group">
        <li v-for="record in recentExecRecords" :key="record.execNo">
          <span class="model-detail-drawer__exec-no">{{ record.execNo }}</span>
          <span>{{ record.startTime }}</span>
          <PlatformStatusTag
            :label="execStatusMeta[record.status].label"
            :status="execStatusMeta[record.status].status"
          />
        </li>
        <li v-if="recentExecRecords.length === 0">暂无执行记录</li>
      </ul>
    </div>
  </PlatformDrawer>
</template>

<style scoped>
.model-detail-drawer__title {
  margin: 0 0 14px;
  font-size: 16px;
}

.model-detail-drawer__subtitle {
  margin: 18px 0 10px;
  font-size: 14px;
  font-weight: 600;
}

.model-detail-drawer__param-group,
.model-detail-drawer__exec-group {
  display: grid;
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.model-detail-drawer__param-group li {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  font-size: 13px;
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-control);
}

.model-detail-drawer__param-group li p {
  flex-basis: 100%;
  margin: 0;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-detail-drawer__param-key {
  font-family: monospace;
  color: hsl(var(--primary));
}

.model-detail-drawer__exec-group li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.model-detail-drawer__exec-no {
  font-family: monospace;
  color: hsl(var(--muted-foreground));
}
</style>
