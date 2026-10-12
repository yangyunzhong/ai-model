<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type {
  DeviceRecord,
  DeviceTypeOption,
  ModelRecord,
  ModelResultRow,
} from '../data';

import { computed, nextTick, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  PlatformButton,
  PlatformIcon,
  PlatformModal,
  PlatformSection,
  PlatformSelect,
  PlatformStatusTag,
  PlatformTable,
  PlatformViewToolbar,
} from '@st/platform-ui';
import { Checkbox, Empty } from 'antdv-next';

import {
  addExecRecord,
  devices,
  deviceTypeOptions,
  getCategoryDeviceType,
  getModelResultRows,
  healthLevelMeta,
  models,
} from '../data';

const selectedDeviceType = ref<DeviceTypeOption>('自动扶梯');
const selectedDeviceIds = ref<string[]>([]);
const selectedModelIds = ref<number[]>([]);
const deviceModalOpen = ref(false);
const comparing = ref(false);
const compareStep = ref(0);
const hasResult = ref(false);

const compareStepTexts = ['加载算法脚本…', '注入设备数据…', '计算评分…'];

interface MatrixRow {
  baseModelId: number;
  byModel: Record<number, ModelResultRow | undefined>;
  deviceId: string;
  deviceName: string;
  /** 各模型健康等级是否不一致 */
  divergent: boolean;
  /** 各数值列在该设备下的最优 / 最差模型（并列或无数据时为空） */
  extremes: Record<string, { best: null | number; worst: null | number }>;
  /** 各模型总分极差（最高分 - 最低分） */
  spread: number;
}

/** 参与数值对比的列字段，与列 key 的后半段一致 */
const SCORE_FIELDS = [
  'total',
  'dimBody',
  'dimCondition',
  'dimFault',
  'dimMaintain',
  'dimEnv',
] as const;

/** 读取某模型某数值列的得分 */
function scoreOf(result: ModelResultRow | undefined, field: string) {
  if (!result) {
    return undefined;
  }

  if (field === 'total') {
    return result.totalScore;
  }

  const value = result[field as keyof ModelResultRow];

  return typeof value === 'number' ? value : undefined;
}

const matrixRows = ref<MatrixRow[]>([]);

/** 配置变化后作废已有比对结果，避免矩阵与当前选择不一致 */
function invalidateResult() {
  hasResult.value = false;
  matrixRows.value = [];
}

function devicesOfType(type: DeviceTypeOption) {
  return devices.value.filter((item) => item.type === type);
}

const publishedModelOptions = computed(() =>
  models.value
    .filter(
      (model) =>
        model.status === '已发布' &&
        getCategoryDeviceType(model.categoryId) === selectedDeviceType.value,
    )
    .map((model) => ({ label: model.modelName, value: model.id })),
);

const selectedModels = computed<ModelRecord[]>(() =>
  models.value.filter((model) => selectedModelIds.value.includes(model.id)),
);

const canCompare = computed(
  () => selectedDeviceIds.value.length > 0 && selectedModelIds.value.length >= 2,
);

watch(selectedDeviceType, () => {
  selectedDeviceIds.value = [];
  selectedModelIds.value = [];
  invalidateResult();
});

// ==================== 概览页跳转回填 ====================
const route = useRoute();

watch(
  () => route.query,
  async (query) => {
    const rawDeviceType = String(query.deviceType ?? '');

    if (deviceTypeOptions.some((option) => option.value === rawDeviceType)) {
      selectedDeviceType.value = rawDeviceType as DeviceTypeOption;
    }

    // 等待设备品种切换触发的重置完成后再回填模型
    await nextTick();

    const rawModelId = Number(query.modelId);

    if (
      Number.isFinite(rawModelId) &&
      publishedModelOptions.value.some((option) => option.value === rawModelId)
    ) {
      selectedModelIds.value = [rawModelId];
    }
  },
  { immediate: true },
);

function handleModelChange(value: unknown) {
  const ids = Array.isArray(value) ? (value as number[]) : [];

  selectedModelIds.value = ids.length > 5 ? ids.slice(0, 5) : ids;
  invalidateResult();

  if (ids.length > 5) {
    window.message.warning('最多选择 5 个模型');
  }
}

function handleReset() {
  selectedDeviceIds.value = [];
  selectedModelIds.value = [];
  invalidateResult();
}

// ==================== 设备选择弹窗 ====================
const deviceGroups = computed(() => {
  const lines = new Map<string, Map<string, DeviceRecord[]>>();

  for (const device of devicesOfType(selectedDeviceType.value)) {
    if (!lines.has(device.line)) {
      lines.set(device.line, new Map());
    }
    const stations = lines.get(device.line)!;

    if (!stations.has(device.station)) {
      stations.set(device.station, []);
    }
    stations.get(device.station)!.push(device);
  }

  return [...lines.entries()].map(([line, stations]) => ({
    devices: [...stations.values()].flat(),
    line,
    stations: [...stations.entries()].map(([station, devices]) => ({
      devices,
      station,
    })),
  }));
});

function isDeviceChecked(device: DeviceRecord) {
  return selectedDeviceIds.value.includes(device.id);
}

function toggleDevice(device: DeviceRecord) {
  if (isDeviceChecked(device)) {
    selectedDeviceIds.value = selectedDeviceIds.value.filter(
      (id) => id !== device.id,
    );
  } else {
    selectedDeviceIds.value = [...selectedDeviceIds.value, device.id];
  }
  invalidateResult();
}

function isLineAllChecked(devices: DeviceRecord[]) {
  return devices.every((device) => isDeviceChecked(device));
}

function toggleLine(devices: DeviceRecord[]) {
  const allChecked = isLineAllChecked(devices);

  if (allChecked) {
    const ids = new Set(devices.map((device) => device.id));
    selectedDeviceIds.value = selectedDeviceIds.value.filter(
      (id) => !ids.has(id),
    );
  } else {
    const merged = new Set(selectedDeviceIds.value);
    devices.forEach((device) => merged.add(device.id));
    selectedDeviceIds.value = [...merged];
  }
  invalidateResult();
}

function toggleAllDevices() {
  const all = devicesOfType(selectedDeviceType.value);

  selectedDeviceIds.value =
    selectedDeviceIds.value.length === all.length
      ? []
      : all.map((device) => device.id);
  invalidateResult();
}

// ==================== 比对执行 ====================
function startCompare() {
  if (!canCompare.value) {
    window.message.warning('请先选择至少 1 台设备和 2 个模型');
    return;
  }

  comparing.value = true;
  compareStep.value = 0;

  window.setTimeout(() => {
    compareStep.value = 1;
  }, 400);
  window.setTimeout(() => {
    compareStep.value = 2;
  }, 900);
  window.setTimeout(() => {
    buildMatrix();
    comparing.value = false;
    hasResult.value = true;
    window.message.success('比对完成');
  }, 1500);
}

function buildMatrix() {
  const rows: MatrixRow[] = [];
  /** 以第一个模型作为差值基准 */
  const baseModelId = selectedModels.value[0]?.id ?? 0;

  for (const deviceId of selectedDeviceIds.value) {
    const device = devicesOfType(selectedDeviceType.value).find(
      (item) => item.id === deviceId,
    );

    if (!device) {
      continue;
    }

    const byModel: Record<number, ModelResultRow | undefined> = {};

    for (const model of selectedModels.value) {
      byModel[model.id] = getModelResultRows(model.id).find(
        (row) => row.deviceId === deviceId,
      );
    }

    const scored = selectedModels.value
      .map((model) => ({ modelId: model.id, total: byModel[model.id]?.totalScore }))
      .filter(
        (item): item is { modelId: number; total: number } =>
          typeof item.total === 'number',
      );
    const totals = scored.map((item) => item.total);
    const maxTotal = totals.length > 0 ? Math.max(...totals) : 0;
    const minTotal = totals.length > 0 ? Math.min(...totals) : 0;
    const hasSpread = maxTotal > minTotal;
    const levels = selectedModels.value
      .map((model) => byModel[model.id]?.healthLevel)
      .filter(Boolean);

    // 逐列计算最优 / 最差模型，供单元格高低分标记使用
    const extremes: MatrixRow['extremes'] = {};

    for (const field of SCORE_FIELDS) {
      const points = selectedModels.value
        .map((model) => ({
          modelId: model.id,
          value: scoreOf(byModel[model.id], field),
        }))
        .filter(
          (item): item is { modelId: number; value: number } =>
            typeof item.value === 'number',
        );
      const values = points.map((item) => item.value);
      const max = values.length > 0 ? Math.max(...values) : 0;
      const min = values.length > 0 ? Math.min(...values) : 0;
      const comparable = max > min;

      extremes[field] = {
        best: comparable
          ? (points.find((item) => item.value === max)?.modelId ?? null)
          : null,
        worst: comparable
          ? (points.find((item) => item.value === min)?.modelId ?? null)
          : null,
      };
    }

    rows.push({
      baseModelId,
      byModel,
      deviceId: device.id,
      deviceName: device.name,
      divergent: new Set(levels).size > 1,
      extremes,
      spread: hasSpread ? maxTotal - minTotal : 0,
    });
  }

  matrixRows.value = rows;

  for (const model of selectedModels.value) {
    addExecRecord({
      deviceCount: selectedDeviceIds.value.length,
      modelId: model.id,
      status: '成功',
      taskId: null,
      triggerType: '数据比对',
    });
  }
}

/** 判定数值单元格背景：该列在该设备下的最高 / 最低 */
function scoreCellClass(record: unknown, modelId: number, field: string) {
  const extreme = (record as MatrixRow).extremes[field];
  const classes: string[] = [];

  if (extreme?.best === modelId) {
    classes.push('compare-cell--best');
  }

  if (extreme?.worst === modelId) {
    classes.push('compare-cell--worst');
  }

  return classes.join(' ');
}

const matrixColumns = computed<TableProps['columns']>(() => [
  { fixed: 'left', key: 'device', title: '设备', width: 220 },
  ...selectedModels.value.map((model) => ({
    children: [
      {
        customCell: (record: unknown) => ({
          class: (record as MatrixRow).divergent
            ? 'compare-cell--divergent'
            : '',
        }),
        key: `m${model.id}:level`,
        title: '健康等级',
        width: 104,
      },
      {
        customCell: (record: unknown) => ({
          class: scoreCellClass(record, model.id, 'total'),
        }),
        key: `m${model.id}:total`,
        title: '总分',
        width: 110,
      },
      ...(
        [
          ['dimBody', '本体/20'],
          ['dimCondition', '工况/25'],
          ['dimFault', '故障/25'],
          ['dimMaintain', '维保/20'],
          ['dimEnv', '环境/10'],
        ] as const
      ).map(([field, title]) => ({
        customCell: (record: unknown) => ({
          class: scoreCellClass(record, model.id, field),
        }),
        key: `m${model.id}:${field}`,
        title,
        width: 84,
      })),
    ],
    key: `m${model.id}`,
    title: model.modelName,
  })),
  { fixed: 'right', key: 'spread', title: '极差', width: 84 },
]);

function rowClassName(record: unknown) {
  return (record as MatrixRow).divergent ? 'compare-row--divergent' : '';
}

function parseColumnKey(key: unknown) {
  const raw = String(key);
  const [modelPart = '', field = ''] = raw.split(':');

  return { field, modelId: Number(modelPart.replace('m', '')) };
}

function getCellRow(record: unknown, key: unknown) {
  const { modelId } = parseColumnKey(key);

  return (record as MatrixRow).byModel[modelId];
}

/** 单元格总分相对基准模型的差值，基准模型自身返回 null */
function totalDiffText(record: unknown, key: unknown) {
  const row = record as MatrixRow;
  const { modelId } = parseColumnKey(key);

  if (modelId === row.baseModelId) {
    return '';
  }

  const base = row.byModel[row.baseModelId]?.totalScore;
  const current = row.byModel[modelId]?.totalScore;

  if (typeof base !== 'number' || typeof current !== 'number') {
    return '';
  }

  const diff = current - base;

  if (diff === 0) {
    return '±0';
  }

  return diff > 0 ? `↑${diff}` : `↓${Math.abs(diff)}`;
}

function totalDiffClass(record: unknown, key: unknown) {
  const text = totalDiffText(record, key);

  if (text.startsWith('↑')) {
    return 'compare-score__diff--up';
  }

  if (text.startsWith('↓')) {
    return 'compare-score__diff--down';
  }

  return '';
}

const distributionStats = computed(() =>
  selectedModels.value.map((model) => {
    const counts = { 亚健康: 0, 健康: 0, 高危: 0 };

    for (const row of matrixRows.value) {
      const level = row.byModel[model.id]?.healthLevel;

      if (level) {
        counts[level] += 1;
      }
    }

    return { counts, model };
  }),
);

/** 结论一致性汇总：各模型对同一台设备给出相同健康等级的台数 */
const consistencyStat = computed(() => {
  const total = matrixRows.value.length;
  const divergent = matrixRows.value.filter((row) => row.divergent).length;

  return { consistent: total - divergent, divergent, total };
});
</script>

<template>
  <Page>
    <div class="model-compare-page">
      <PlatformViewToolbar
        description="同一批设备 × 多模型在线比对计算（至少 2 个模型，最多 5 个）"
        title="数据比对"
      />

      <PlatformSection title="比对配置">
        <div class="model-compare-config">
          <div class="model-compare-config__item">
            <span class="model-compare-config__label">设备品种</span>
            <PlatformSelect
              v-model:value="selectedDeviceType"
              :options="deviceTypeOptions"
              placeholder="请选择设备品种"
            />
          </div>
          <div class="model-compare-config__item">
            <span class="model-compare-config__label">比对设备</span>
            <PlatformButton scene="toolbar" @click="deviceModalOpen = true">
              <template #icon>
                <PlatformIcon icon="lucide:list-checks" />
              </template>
              选择设备
            </PlatformButton>
            <span class="model-compare-config__hint">
              已选 {{ selectedDeviceIds.length }} 台
            </span>
          </div>
          <div class="model-compare-config__item model-compare-config__item--wide">
            <span class="model-compare-config__label">比对模型</span>
            <PlatformSelect
              :value="selectedModelIds"
              :options="publishedModelOptions"
              mode="multiple"
              :max-tag-count="3"
              allow-clear
              placeholder="仅显示已发布且品种匹配的模型，至少选 2 个、最多 5 个"
              @change="handleModelChange"
            />
          </div>
          <div class="model-compare-config__item">
            <PlatformButton
              :disabled="!canCompare"
              scene="toolbar"
              type="primary"
              @click="startCompare"
            >
              比对
            </PlatformButton>
            <PlatformButton scene="toolbar" @click="handleReset">重置</PlatformButton>
            <span v-if="!canCompare" class="model-compare-config__hint">
              需先选择至少 1 台设备和 2 个模型
            </span>
          </div>
        </div>
      </PlatformSection>

      <PlatformSection title="比对结果">
        <div v-if="comparing" class="model-compare-loading">
          <span class="model-compare-loading__spinner"></span>
          <ul class="model-compare-loading__steps">
            <li
              v-for="(text, index) in compareStepTexts"
              :key="text"
              :class="{ 'model-compare-loading__step--active': index <= compareStep }"
            >
              {{ text }}
            </li>
          </ul>
        </div>

        <template v-else-if="hasResult">
          <div class="model-compare-legend">
            <span class="model-compare-legend__item">
              <i class="model-compare-legend__swatch model-compare-legend__swatch--divergent"></i>
              健康等级不一致
            </span>
            <span class="model-compare-legend__item">
              <i class="model-compare-legend__swatch model-compare-legend__swatch--best"></i>
              该项得分最高
            </span>
            <span class="model-compare-legend__item">
              <i class="model-compare-legend__swatch model-compare-legend__swatch--worst"></i>
              该项得分最低
            </span>
            <span class="model-compare-legend__item">
              <span class="compare-score__diff compare-score__diff--up">↑</span>
              <span class="compare-score__diff compare-score__diff--down">↓</span>
              相对第一个模型的分数增减
            </span>
          </div>

          <PlatformTable
            :action-column-width="0"
            :columns="matrixColumns"
            :data-source="matrixRows"
            :pagination="false"
            :row-class-name="rowClassName"
            :scroll="{ x: 1200 }"
            row-key="deviceId"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'device'">
                <div class="model-compare-device-cell">
                  <strong>{{ record.deviceName }}</strong>
                  <span>{{ record.deviceId }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'spread'">
                <span
                  class="compare-spread"
                  :class="{ 'compare-spread--active': record.spread > 0 }"
                >
                  {{ record.spread }}
                </span>
              </template>
              <template v-else-if="String(column.key).includes(':')">
                <template v-if="parseColumnKey(column.key).field === 'level'">
                  <PlatformStatusTag
                    v-if="getCellRow(record, column.key)"
                    :label="healthLevelMeta[getCellRow(record, column.key)!.healthLevel].label"
                    :status="healthLevelMeta[getCellRow(record, column.key)!.healthLevel].status"
                  />
                  <span v-else>-</span>
                </template>
                <template v-else-if="parseColumnKey(column.key).field === 'total'">
                  <div class="compare-score">
                    <span class="compare-score__value">
                      {{ getCellRow(record, column.key)?.totalScore ?? '-' }}
                    </span>
                    <span
                      v-if="totalDiffText(record, column.key)"
                      class="compare-score__diff"
                      :class="totalDiffClass(record, column.key)"
                    >
                      {{ totalDiffText(record, column.key) }}
                    </span>
                  </div>
                </template>
                <template v-else>
                  {{ getCellRow(record, column.key)?.[parseColumnKey(column.key).field as keyof ModelResultRow] ?? '-' }}
                </template>
              </template>
            </template>
          </PlatformTable>

          <div class="model-compare-conclusion">
            <div class="model-compare-conclusion__head">
              <h4>比对结论</h4>
              <span class="model-compare-conclusion__overview">
                共比对 {{ consistencyStat.total }} 台设备，结论一致
                <strong>{{ consistencyStat.consistent }}</strong>
                台
                <template v-if="consistencyStat.divergent > 0">
                  ，不一致
                  <strong class="model-compare-conclusion__divergent">
                    {{ consistencyStat.divergent }}
                  </strong>
                  台
                </template>
              </span>
            </div>

            <ul class="model-compare-conclusion__list">
              <li
                v-for="row in matrixRows"
                :key="row.deviceId"
                class="model-compare-conclusion__item"
                :class="{
                  'model-compare-conclusion__item--divergent': row.divergent,
                }"
              >
                <span class="model-compare-conclusion__device">
                  {{ row.deviceName }}
                </span>

                <span
                  v-for="model in selectedModels"
                  :key="model.id"
                  class="model-compare-conclusion__model"
                >
                  <em>{{ model.modelName }}</em>
                  <template v-if="row.byModel[model.id]">
                    <PlatformStatusTag
                      :label="healthLevelMeta[row.byModel[model.id]!.healthLevel].label"
                      :status="healthLevelMeta[row.byModel[model.id]!.healthLevel].status"
                      variant="dot"
                    />
                    <b>{{ row.byModel[model.id]!.totalScore }} 分</b>
                  </template>
                  <span v-else>-</span>
                </span>

                <PlatformStatusTag
                  :label="row.divergent ? '结论不一致' : '结论一致'"
                  :status="row.divergent ? 'warning' : 'success'"
                />
              </li>
            </ul>
          </div>

          <div class="model-compare-distribution">
            <div
              v-for="stat in distributionStats"
              :key="stat.model.id"
              class="model-compare-distribution__item"
            >
              <h4>{{ stat.model.modelName }}</h4>
              <p>
                <PlatformStatusTag
                  label="健康"
                  status="success"
                  variant="dot"
                />
                {{ stat.counts['健康'] }} 台
              </p>
              <p>
                <PlatformStatusTag
                  label="亚健康"
                  status="warning"
                  variant="dot"
                />
                {{ stat.counts['亚健康'] }} 台
              </p>
              <p>
                <PlatformStatusTag
                  label="高危"
                  status="error"
                  variant="dot"
                />
                {{ stat.counts['高危'] }} 台
              </p>
            </div>
          </div>
        </template>

        <Empty
          v-else
          description="选择设备与模型后点击「比对」查看结果矩阵"
        />
      </PlatformSection>
    </div>

    <!-- 选择设备弹窗 -->
    <PlatformModal
      v-model:open="deviceModalOpen"
      :title="`选择设备 - ${selectedDeviceType}`"
      :footer="null"
      destroy-on-close
      width="720px"
    >
      <div class="model-compare-device-picker">
        <div class="model-compare-device-picker__bar">
          <span>已选 {{ selectedDeviceIds.length }} 台</span>
          <PlatformButton scene="toolbar" size="small" @click="toggleAllDevices">
            {{ selectedDeviceIds.length === devicesOfType(selectedDeviceType).length && selectedDeviceIds.length > 0 ? '全不选' : '全选' }}
          </PlatformButton>
        </div>

        <div
          v-for="group in deviceGroups"
          :key="group.line"
          class="model-compare-device-picker__line"
        >
          <Checkbox
            :checked="isLineAllChecked(group.devices)"
            :indeterminate="!isLineAllChecked(group.devices) && group.devices.some((device) => isDeviceChecked(device))"
            @change="() => toggleLine(group.devices)"
          >
            {{ group.line }}（{{ group.devices.length }} 台）
          </Checkbox>

          <div
            v-for="station in group.stations"
            :key="station.station"
            class="model-compare-device-picker__station"
          >
            <span class="model-compare-device-picker__station-name">
              {{ station.station }}
            </span>
            <Checkbox
              v-for="device in station.devices"
              :key="device.id"
              :checked="isDeviceChecked(device)"
              @change="() => toggleDevice(device)"
            >
              {{ device.name }}
            </Checkbox>
          </div>
        </div>

        <Empty
          v-if="deviceGroups.length === 0"
          description="该品种暂无状态正常的设备"
        />
      </div>
    </PlatformModal>
  </Page>
</template>

<style scoped>
.model-compare-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.model-compare-config {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 24px;
}

.model-compare-config__item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.model-compare-config__item--wide {
  flex: 1 1 320px;
}

.model-compare-config__label {
  flex: none;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}

.model-compare-config__item--wide :deep(.platform-select) {
  width: 100%;
}

.model-compare-config__hint {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-compare-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 40px 0;
}

.model-compare-loading__spinner {
  width: 26px;
  height: 26px;
  border: 3px solid hsl(var(--primary) / 25%);
  border-top-color: hsl(var(--primary));
  border-radius: 50%;
  animation: model-compare-spin 0.8s linear infinite;
}

@keyframes model-compare-spin {
  to {
    transform: rotate(360deg);
  }
}

.model-compare-loading__steps {
  display: flex;
  gap: 20px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.model-compare-loading__steps li {
  color: hsl(var(--muted-foreground));
}

.model-compare-loading__step--active {
  color: hsl(var(--primary));
  font-weight: 600;
}

.model-compare-device-cell {
  display: flex;
  flex-direction: column;
}

.model-compare-device-cell span {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-compare-legend {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px 18px;
  margin-bottom: 12px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.model-compare-legend__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.model-compare-legend__swatch {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.model-compare-legend__swatch--divergent {
  background: hsl(var(--warning) / 45%);
}

.model-compare-legend__swatch--best {
  background: hsl(var(--success) / 45%);
}

.model-compare-legend__swatch--worst {
  background: hsl(var(--destructive) / 35%);
}

:deep(.compare-row--divergent > td) {
  background: hsl(var(--warning) / 8%);
}

:deep(.compare-cell--divergent) {
  background: hsl(var(--warning) / 18%) !important;
}

:deep(.compare-cell--best) {
  background: hsl(var(--success) / 16%) !important;
}

:deep(.compare-cell--worst) {
  background: hsl(var(--destructive) / 14%) !important;
}

.compare-score {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.25;
}

.compare-score__diff {
  font-size: 12px;
}

.compare-score__diff--up {
  color: hsl(var(--success));
}

.compare-score__diff--down {
  color: hsl(var(--destructive));
}

.compare-spread--active {
  color: hsl(var(--warning));
  font-weight: 600;
}

.model-compare-conclusion {
  margin-top: var(--st-layout-section-gap);
  padding: 14px 16px;
  background: hsl(var(--accent) / 30%);
  border-radius: var(--st-radius-card);
}

.model-compare-conclusion__head {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 6px 14px;
  margin-bottom: 10px;
}

.model-compare-conclusion__head h4 {
  margin: 0;
  font-size: 13px;
}

.model-compare-conclusion__overview {
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}

.model-compare-conclusion__overview strong {
  font-size: 15px;
  color: hsl(var(--foreground));
}

.model-compare-conclusion__divergent {
  color: hsl(var(--warning)) !important;
}

.model-compare-conclusion__list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.model-compare-conclusion__item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 18px;
  padding: 8px 12px;
  font-size: 13px;
  background: hsl(var(--background));
  border-radius: var(--st-radius-card);
}

.model-compare-conclusion__item--divergent {
  background: hsl(var(--warning) / 12%);
}

.model-compare-conclusion__device {
  flex: none;
  min-width: 200px;
  font-weight: 600;
}

.model-compare-conclusion__model {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.model-compare-conclusion__model em {
  font-style: normal;
  color: hsl(var(--muted-foreground));
}

.model-compare-distribution {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--st-layout-section-gap);
  margin-top: var(--st-layout-section-gap);
}

.model-compare-distribution__item {
  padding: 14px 16px;
  background: hsl(var(--accent) / 40%);
  border-radius: var(--st-radius-card);
}

.model-compare-distribution__item h4 {
  margin: 0 0 8px;
  font-size: 13px;
}

.model-compare-distribution__item p {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 4px 0;
  font-size: 13px;
}

.model-compare-device-picker {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 60vh;
  overflow: auto;
}

.model-compare-device-picker__bar {
  position: sticky;
  top: 0;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  background: hsl(var(--background));
}

.model-compare-device-picker__line {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px 12px;
  background: hsl(var(--accent) / 30%);
  border-radius: var(--st-radius-card);
}

.model-compare-device-picker__station {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px 14px;
  padding-left: 22px;
}

.model-compare-device-picker__station-name {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}
</style>
