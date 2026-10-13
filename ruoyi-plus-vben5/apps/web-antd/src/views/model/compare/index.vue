<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type {
  CatalogVO,
  ComparisonExecuteCell,
  ComparisonExecuteVO,
  DefinitionVO,
  DeviceVO,
} from '#/api/model';

import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  catalogList,
  comparisonRecordExecute,
  definitionList,
  deviceList,
} from '#/api/model';
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

type TagStatus = 'default' | 'error' | 'processing' | 'success' | 'warning';

interface Option {
  label: string;
  value: string;
}

interface MatrixRow {
  byModel: Record<string, ComparisonExecuteCell | undefined>;
  deviceId: string;
  deviceName: string;
  /** 各模型健康等级是否不一致 */
  divergent: boolean;
  /** 各数值列在该设备下的最优 / 最差模型（并列或无数据时为空） */
  extremes: Record<string, { best: null | string; worst: null | string }>;
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

const LEVEL_META: Record<string, { label: string; status: TagStatus }> = {
  healthy: { label: '健康', status: 'success' },
  subhealthy: { label: '亚健康', status: 'warning' },
  danger: { label: '高危', status: 'error' },
  健康: { label: '健康', status: 'success' },
  亚健康: { label: '亚健康', status: 'warning' },
  高危: { label: '高危', status: 'error' },
};

function levelMetaOf(level?: string) {
  return LEVEL_META[level ?? ''] ?? { label: level ?? '-', status: 'default' as TagStatus };
}

// ==================== 状态 ====================
const deviceTypeOptions = ref<Option[]>([]);
const selectedDeviceType = ref<string>('');
const allDevices = ref<DeviceVO[]>([]);
const selectedDeviceIds = ref<string[]>([]);
const modelOptions = ref<Option[]>([]);
const selectedModelIds = ref<string[]>([]);
const loadingDevices = ref(false);
const deviceModalOpen = ref(false);
const comparing = ref(false);
const hasResult = ref(false);
const result = ref<ComparisonExecuteVO | null>(null);
const matrixRows = ref<MatrixRow[]>([]);

const resultModels = computed(() => result.value?.models ?? []);

const selectedModelOptions = computed(() =>
  resultModels.value.map((model) => ({
    id: String(model.modelId),
    modelName: model.modelName,
  })),
);

const selectedDeviceCount = computed(() => selectedDeviceIds.value.length);

const canCompare = computed(
  () =>
    selectedDeviceIds.value.length > 0 &&
    selectedModelIds.value.length >= 2 &&
    selectedModelIds.value.length <= 5,
);

/** 配置变化后作废已有比对结果，避免矩阵与当前选择不一致 */
function invalidateResult() {
  hasResult.value = false;
  result.value = null;
  matrixRows.value = [];
}

// ==================== 数据加载 ====================
async function loadDeviceTypes() {
  const res = await catalogList({ pageNum: 1, pageSize: 200 });
  const list: CatalogVO[] = res.rows ?? [];
  const types = [...new Set(list.map((item) => item.deviceType).filter(Boolean))];
  deviceTypeOptions.value = types.map((type) => ({ label: type, value: type }));
}

async function loadDevices() {
  if (!selectedDeviceType.value) {
    allDevices.value = [];
    return;
  }
  loadingDevices.value = true;
  try {
    const res = await deviceList({
      pageNum: 1,
      pageSize: 1000,
      deviceType: selectedDeviceType.value,
    });
    allDevices.value = res.rows ?? [];
  } finally {
    loadingDevices.value = false;
  }
}

async function loadModels() {
  if (!selectedDeviceType.value) {
    modelOptions.value = [];
    return;
  }
  const res = await definitionList({ pageNum: 1, pageSize: 500 });
  const list: DefinitionVO[] = res.rows ?? [];
  modelOptions.value = list
    .filter(
      (model) =>
        model.status === '已发布' &&
        model.deviceType === selectedDeviceType.value,
    )
    .map((model) => ({ label: model.modelName, value: String(model.id) }));
}

watch(selectedDeviceType, async () => {
  selectedDeviceIds.value = [];
  selectedModelIds.value = [];
  invalidateResult();
  await Promise.all([loadDevices(), loadModels()]);
});

// ==================== 概览页跳转回填 ====================
const route = useRoute();

watch(
  () => route.query,
  async (query) => {
    const rawDeviceType = String(query.deviceType ?? '');

    if (deviceTypeOptions.value.some((option) => option.value === rawDeviceType)) {
      selectedDeviceType.value = rawDeviceType;
    }

    // 等待设备品种切换触发的模型加载完成后再回填模型
    await nextTick();
    await loadModels();

    const rawModelId = String(query.modelId ?? '');
    if (modelOptions.value.some((option) => option.value === rawModelId)) {
      selectedModelIds.value = [rawModelId];
    }
  },
  { immediate: true },
);

function handleModelChange(value: unknown) {
  const ids = Array.isArray(value) ? value.map((item) => String(item)) : [];

  if (ids.length > 5) {
    selectedModelIds.value = ids.slice(0, 5);
    window.message.warning('最多选择 5 个模型');
  } else {
    selectedModelIds.value = ids;
  }
  invalidateResult();
}

function handleReset() {
  selectedDeviceIds.value = [];
  selectedModelIds.value = [];
  invalidateResult();
}

// ==================== 设备选择弹窗 ====================
const deviceGroups = computed(() => {
  const lines = new Map<string, DeviceVO[]>();

  for (const device of allDevices.value) {
    const key =
      device.lineId === undefined || device.lineId === null || device.lineId === ''
        ? '未分组'
        : String(device.lineId);

    if (!lines.has(key)) {
      lines.set(key, []);
    }
    lines.get(key)!.push(device);
  }

  return [...lines.entries()].map(([line, devices]) => ({ devices, line }));
});

function isDeviceChecked(device: DeviceVO) {
  return selectedDeviceIds.value.includes(String(device.id));
}

function toggleDevice(device: DeviceVO) {
  const id = String(device.id);

  if (isDeviceChecked(device)) {
    selectedDeviceIds.value = selectedDeviceIds.value.filter((item) => item !== id);
  } else {
    selectedDeviceIds.value = [...selectedDeviceIds.value, id];
  }
  invalidateResult();
}

function isLineAllChecked(devices: DeviceVO[]) {
  return devices.every((device) => isDeviceChecked(device));
}

function toggleLine(devices: DeviceVO[]) {
  const allChecked = isLineAllChecked(devices);
  const ids = new Set(devices.map((device) => String(device.id)));

  if (allChecked) {
    selectedDeviceIds.value = selectedDeviceIds.value.filter((id) => !ids.has(id));
  } else {
    const merged = new Set(selectedDeviceIds.value);
    ids.forEach((id) => merged.add(id));
    selectedDeviceIds.value = [...merged];
  }
  invalidateResult();
}

function toggleAllDevices() {
  selectedDeviceIds.value =
    selectedDeviceIds.value.length === allDevices.value.length
      ? []
      : allDevices.value.map((device) => String(device.id));
  invalidateResult();
}

// ==================== 比对执行 ====================
async function startCompare() {
  if (!canCompare.value) {
    window.message.warning('请先选择至少 1 台设备和 2 个模型');
    return;
  }

  comparing.value = true;
  try {
    const vo = await comparisonRecordExecute({
      deviceType: selectedDeviceType.value,
      deviceIds: selectedDeviceIds.value,
      modelIds: selectedModelIds.value,
    });
    result.value = vo;
    buildMatrix(vo);
    hasResult.value = true;
    window.message.success('比对完成');
  } finally {
    comparing.value = false;
  }
}

function cellValue(cell: ComparisonExecuteCell | undefined, field: string) {
  if (!cell) {
    return undefined;
  }
  if (field === 'total') {
    return cell.healthScore;
  }
  const value = cell[field as keyof ComparisonExecuteCell];
  return typeof value === 'number' ? value : undefined;
}

function buildMatrix(vo: ComparisonExecuteVO) {
  const modelIds = vo.models.map((model) => String(model.modelId));
  const detailMap = new Map<string, ComparisonExecuteCell>();

  for (const cell of vo.details) {
    detailMap.set(`${String(cell.deviceId)}::${String(cell.modelId)}`, cell);
  }

  const rows: MatrixRow[] = [];

  for (const device of vo.devices) {
    const deviceId = String(device.deviceId);
    const byModel: Record<string, ComparisonExecuteCell | undefined> = {};

    for (const modelId of modelIds) {
      byModel[modelId] = detailMap.get(`${deviceId}::${modelId}`);
    }

    const levels = modelIds
      .map((modelId) => byModel[modelId]?.healthLevel)
      .filter((level): level is string => Boolean(level));

    const totals = modelIds
      .map((modelId) => byModel[modelId]?.healthScore)
      .filter((score): score is number => typeof score === 'number');
    const maxTotal = totals.length > 0 ? Math.max(...totals) : 0;
    const minTotal = totals.length > 0 ? Math.min(...totals) : 0;

    // 逐列计算最优 / 最差模型，供单元格高低分标记使用
    const extremes: MatrixRow['extremes'] = {};

    for (const field of SCORE_FIELDS) {
      const points = modelIds
        .map((modelId) => ({ modelId, value: cellValue(byModel[modelId], field) }))
        .filter(
          (item): item is { modelId: string; value: number } =>
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
      byModel,
      deviceId,
      deviceName: device.deviceName,
      divergent: new Set(levels).size > 1,
      extremes,
      spread: maxTotal > minTotal ? maxTotal - minTotal : 0,
    });
  }

  matrixRows.value = rows;
}

/** 判定数值单元格背景：该列在该设备下的最高 / 最低 */
function scoreCellClass(record: unknown, modelId: string, field: string) {
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
  { fixed: 'left', key: 'device', title: '设备', width: 200 },
  ...resultModels.value.map((model) => {
    const modelId = String(model.modelId);

    return {
      children: [
        {
          customCell: (record: unknown) => ({
            class: (record as MatrixRow).divergent
              ? 'compare-cell--divergent'
              : '',
          }),
          key: `m${modelId}:level`,
          title: '健康等级',
          width: 104,
        },
        {
          customCell: (record: unknown) => ({
            class: scoreCellClass(record, modelId, 'total'),
          }),
          key: `m${modelId}:total`,
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
            class: scoreCellClass(record, modelId, field),
          }),
          key: `m${modelId}:${field}`,
          title,
          width: 90,
        })),
      ],
      key: `m${modelId}`,
      title: model.modelName,
    };
  }),
  { fixed: 'right', key: 'spread', title: '极差', width: 84 },
]);

const baseModelId = computed(() =>
  resultModels.value.length > 0 ? String(resultModels.value[0]!.modelId) : '',
);

function rowClassName(record: unknown) {
  return (record as MatrixRow).divergent ? 'compare-row--divergent' : '';
}

function parseColumnKey(key: unknown) {
  const raw = String(key);
  const [modelPart = '', field = ''] = raw.split(':');

  return { field, modelId: modelPart.replace('m', '') };
}

function getCellRow(record: unknown, key: unknown) {
  const { modelId } = parseColumnKey(key);

  return (record as MatrixRow).byModel[modelId];
}

/** 单元格总分相对基准模型的差值，基准模型自身返回空 */
function totalDiffText(record: unknown, key: unknown) {
  const row = record as MatrixRow;
  const { modelId } = parseColumnKey(key);

  if (modelId === baseModelId.value) {
    return '';
  }

  const base = row.byModel[baseModelId.value]?.healthScore;
  const current = row.byModel[modelId]?.healthScore;

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
  resultModels.value.map((model) => {
    const modelId = String(model.modelId);
    const counts = { danger: 0, healthy: 0, subhealthy: 0 };

    for (const row of matrixRows.value) {
      const level = row.byModel[modelId]?.healthLevel;

      if (level && level in counts) {
        counts[level as keyof typeof counts] += 1;
      }
    }

    return { counts, model: { id: modelId, modelName: model.modelName } };
  }),
);

/** 结论一致性汇总：各模型对同一台设备给出相同健康等级的台数 */
const consistencyStat = computed(() => {
  const summary = result.value?.summary;

  if (summary) {
    return {
      consistent: summary.consistent,
      divergent: summary.divergent,
      total: summary.total,
    };
  }

  const total = matrixRows.value.length;
  const divergent = matrixRows.value.filter((row) => row.divergent).length;

  return { consistent: total - divergent, divergent, total };
});

onMounted(async () => {
  await loadDeviceTypes();

  const rawDeviceType = String(route.query.deviceType ?? '');
  if (deviceTypeOptions.value.some((option) => option.value === rawDeviceType)) {
    selectedDeviceType.value = rawDeviceType;
  } else if (deviceTypeOptions.value.length === 1) {
    selectedDeviceType.value = deviceTypeOptions.value[0]!.value;
  }
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
            <PlatformButton
              :disabled="!selectedDeviceType"
              scene="toolbar"
              @click="deviceModalOpen = true"
            >
              <template #icon>
                <PlatformIcon icon="lucide:list-checks" />
              </template>
              选择设备
            </PlatformButton>
            <span class="model-compare-config__hint">
              已选 {{ selectedDeviceCount }} 台
            </span>
          </div>
          <div class="model-compare-config__item model-compare-config__item--wide">
            <span class="model-compare-config__label">比对模型</span>
            <PlatformSelect
              :value="selectedModelIds"
              :options="modelOptions"
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
          <p>正在比对各模型对所选设备的计算结果…</p>
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
                  <strong>{{ record.deviceName || record.deviceId }}</strong>
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
                    :label="levelMetaOf(getCellRow(record, column.key)!.healthLevel).label"
                    :status="levelMetaOf(getCellRow(record, column.key)!.healthLevel).status"
                  />
                  <span v-else>-</span>
                </template>
                <template v-else-if="parseColumnKey(column.key).field === 'total'">
                  <div class="compare-score">
                    <span class="compare-score__value">
                      {{ getCellRow(record, column.key)?.healthScore ?? '-' }}
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
                  {{
                    getCellRow(record, column.key)?.[
                      parseColumnKey(column.key).field as keyof ComparisonExecuteCell
                    ] ?? '-'
                  }}
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
                  {{ row.deviceName || row.deviceId }}
                </span>

                <span
                  v-for="model in selectedModelOptions"
                  :key="model.id"
                  class="model-compare-conclusion__model"
                >
                  <em>{{ model.modelName }}</em>
                  <template v-if="row.byModel[model.id]">
                    <PlatformStatusTag
                      :label="levelMetaOf(row.byModel[model.id]!.healthLevel).label"
                      :status="levelMetaOf(row.byModel[model.id]!.healthLevel).status"
                      variant="dot"
                    />
                    <b>{{ row.byModel[model.id]!.healthScore ?? '-' }} 分</b>
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
                <PlatformStatusTag label="健康" status="success" variant="dot" />
                {{ stat.counts.healthy }} 台
              </p>
              <p>
                <PlatformStatusTag label="亚健康" status="warning" variant="dot" />
                {{ stat.counts.subhealthy }} 台
              </p>
              <p>
                <PlatformStatusTag label="高危" status="error" variant="dot" />
                {{ stat.counts.danger }} 台
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
      :title="`选择设备 - ${selectedDeviceType || '未选择品种'}`"
      :footer="null"
      destroy-on-close
      width="720px"
    >
      <div class="model-compare-device-picker">
        <div class="model-compare-device-picker__bar">
          <span>已选 {{ selectedDeviceCount }} 台</span>
          <PlatformButton scene="toolbar" size="small" @click="toggleAllDevices">
            {{
              selectedDeviceCount === allDevices.length && selectedDeviceCount > 0
                ? '全不选'
                : '全选'
            }}
          </PlatformButton>
        </div>

        <div
          v-for="group in deviceGroups"
          :key="group.line"
          class="model-compare-device-picker__line"
        >
          <Checkbox
            :checked="isLineAllChecked(group.devices)"
            :indeterminate="
              !isLineAllChecked(group.devices) &&
              group.devices.some((device) => isDeviceChecked(device))
            "
            @change="() => toggleLine(group.devices)"
          >
            {{ group.line }}（{{ group.devices.length }} 台）
          </Checkbox>

          <div class="model-compare-device-picker__station">
            <Checkbox
              v-for="device in group.devices"
              :key="device.id"
              :checked="isDeviceChecked(device)"
              @change="() => toggleDevice(device)"
            >
              {{ device.deviceName || device.id }}
            </Checkbox>
          </div>
        </div>

        <Empty
          v-if="deviceGroups.length === 0"
          :description="
            selectedDeviceType ? '该品种暂无状态启用的设备' : '请先选择设备品种'
          "
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
  font-size: 13px;
  color: hsl(var(--muted-foreground));
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
</style>
