<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type {
  DeviceRecord,
  DeviceTypeOption,
  ModelRecord,
  ModelResultRow,
} from '../data';

import { computed, ref, watch } from 'vue';

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
  byModel: Record<number, ModelResultRow | undefined>;
  deviceId: string;
  deviceName: string;
}

const matrixRows = ref<MatrixRow[]>([]);

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
  () => selectedDeviceIds.value.length > 0 && selectedModelIds.value.length > 0,
);

watch(selectedDeviceType, () => {
  selectedDeviceIds.value = [];
  selectedModelIds.value = [];
  hasResult.value = false;
  matrixRows.value = [];
});

function handleModelChange(value: unknown) {
  const ids = Array.isArray(value) ? (value as number[]) : [];

  if (ids.length > 5) {
    window.message.warning('最多选择 5 个模型');
    selectedModelIds.value = ids.slice(0, 5);
    return;
  }
  selectedModelIds.value = ids;
}

function handleReset() {
  selectedDeviceIds.value = [];
  selectedModelIds.value = [];
  hasResult.value = false;
  matrixRows.value = [];
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
}

function toggleAllDevices() {
  const all = devicesOfType(selectedDeviceType.value);

  if (selectedDeviceIds.value.length === all.length) {
    selectedDeviceIds.value = [];
    return;
  }
  selectedDeviceIds.value = all.map((device) => device.id);
}

// ==================== 比对执行 ====================
function startCompare() {
  if (!canCompare.value) {
    window.message.warning('请先选择设备与模型');
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

    rows.push({
      byModel,
      deviceId: device.id,
      deviceName: device.name,
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

const matrixColumns = computed<TableProps['columns']>(() => [
  { fixed: 'left', key: 'device', title: '设备', width: 220 },
  ...selectedModels.value.map((model) => ({
    children: [
      {
        customCell: (record: unknown) => ({
          class: isLevelDivergent(record as MatrixRow)
            ? 'compare-cell--divergent'
            : '',
        }),
        key: `m${model.id}:level`,
        title: '健康等级',
        width: 104,
      },
      { key: `m${model.id}:total`, title: '总分', width: 70 },
      { key: `m${model.id}:dimBody`, title: '本体/20', width: 84 },
      { key: `m${model.id}:dimCondition`, title: '工况/25', width: 84 },
      { key: `m${model.id}:dimFault`, title: '故障/25', width: 84 },
      { key: `m${model.id}:dimMaintain`, title: '维保/20', width: 84 },
      { key: `m${model.id}:dimEnv`, title: '环境/10', width: 84 },
    ],
    key: `m${model.id}`,
    title: model.modelName,
  })),
]);

function isLevelDivergent(row: MatrixRow) {
  const levels = Object.values(row.byModel)
    .map((item) => item?.healthLevel)
    .filter(Boolean);

  return new Set(levels).size > 1;
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
</script>

<template>
  <Page>
    <div class="model-compare-page">
      <PlatformViewToolbar
        description="同一批设备 × 多模型在线比对计算（最多 5 个模型）"
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
              placeholder="仅显示已发布且品种匹配的模型，最多 5 个"
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
              需先选择至少 1 台设备和 1 个模型
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
          <PlatformTable
            :action-column-width="0"
            :columns="matrixColumns"
            :data-source="matrixRows"
            :pagination="false"
            :scroll="{ x: 1000 }"
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
              <template v-else-if="String(column.key).includes(':')">
                <template v-if="parseColumnKey(column.key).field === 'level'">
                  <PlatformStatusTag
                    v-if="getCellRow(record, column.key)"
                    :label="healthLevelMeta[getCellRow(record, column.key)!.healthLevel].label"
                    :status="healthLevelMeta[getCellRow(record, column.key)!.healthLevel].status"
                  />
                  <span v-else>-</span>
                </template>
                <template v-else>
                  {{ getCellRow(record, column.key)?.[parseColumnKey(column.key).field as keyof ModelResultRow] ?? '-' }}
                </template>
              </template>
            </template>
          </PlatformTable>

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

:deep(.compare-cell--divergent) {
  background: hsl(var(--warning) / 18%) !important;
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
