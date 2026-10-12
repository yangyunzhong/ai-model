<script setup lang="ts">
import type { TableProps } from 'antdv-next';

import type { DefinitionVO, LineVO, ResultVO } from '#/api/model/model';

import { computed, ref } from 'vue';

import {
  PlatformDrawer,
  PlatformSegmented,
  PlatformStatusTag,
  PlatformTable,
} from '@st/platform-ui';

import { lineList, resultList } from '#/api/model';

const resultOpen = ref(false);
const resultModel = ref<null | DefinitionVO>(null);
const viewMode = ref<'line' | 'type'>('line');
const resultRows = ref<ResultVO[]>([]);
/** 线路ID -> 线路名称 */
const lineNameMap = ref<Record<string, string>>({});

/** 健康等级：后端英文枚举 → 中文展示 */
const healthLevelMeta: Record<
  string,
  { label: string; status: 'error' | 'success' | 'warning' }
> = {
  danger: { label: '高危', status: 'error' },
  healthy: { label: '健康', status: 'success' },
  subhealthy: { label: '亚健康', status: 'warning' },
};

function healthMetaOf(level?: string) {
  return healthLevelMeta[level ?? ''] ?? { label: level ?? '-', status: 'default' as const };
}

function lineNameOf(lineId?: string | number) {
  if (lineId === undefined || lineId === null) {
    return '-';
  }
  return lineNameMap.value[String(lineId)] ?? String(lineId);
}

/** 输出参数：后端 outputs 为 JSON 字符串，按 key=value 展开展示 */
function outputsOf(record: ResultVO) {
  const raw = record.outputs;
  if (!raw) {
    return '-';
  }
  try {
    const obj = JSON.parse(raw) as Record<string, unknown>;
    const entries = Object.entries(obj);
    if (entries.length === 0) {
      return '-';
    }
    return entries.map(([key, value]) => `${key}=${value}`).join('；');
  } catch {
    return raw;
  }
}

let linesLoaded = false;

async function loadLines() {
  const res = await lineList({ pageNum: 1, pageSize: 200 });
  const list: LineVO[] = res.rows ?? [];
  lineNameMap.value = Object.fromEntries(
    list.map((item) => [String(item.id), item.name]),
  );
}

/** 打开查看结果抽屉 */
async function open(model: DefinitionVO) {
  resultModel.value = model;
  viewMode.value = 'line';
  resultOpen.value = true;
  resultRows.value = [];

  if (!linesLoaded) {
    await loadLines();
    linesLoaded = true;
  }

  const res = await resultList({
    modelId: model.id,
    pageNum: 1,
    pageSize: 500,
  });
  resultRows.value = res.rows ?? [];
}

const sortedRows = computed(() => {
  return [...resultRows.value].sort((a, b) => {
    if (viewMode.value === 'type') {
      return (
        String(a.deviceType).localeCompare(String(b.deviceType)) ||
        lineNameOf(a.lineId).localeCompare(lineNameOf(b.lineId)) ||
        String(a.stationId).localeCompare(String(b.stationId))
      );
    }
    return (
      lineNameOf(a.lineId).localeCompare(lineNameOf(b.lineId)) ||
      String(a.stationId).localeCompare(String(b.stationId)) ||
      String(a.deviceName).localeCompare(String(b.deviceName))
    );
  });
});

const resultColumns = computed<TableProps['columns']>(() => {
  const groupColumns =
    viewMode.value === 'type'
      ? [
          { dataIndex: 'deviceType', key: 'deviceType', title: '设备品种', width: 100 },
          { dataIndex: 'lineId', key: 'lineId', title: '线路', width: 90 },
          { dataIndex: 'stationId', key: 'stationId', title: '车站', width: 120 },
          { dataIndex: 'deviceName', key: 'deviceName', title: '设备名称', width: 230 },
        ]
      : [
          { dataIndex: 'deviceName', key: 'deviceName', title: '设备名称', width: 230 },
          { dataIndex: 'lineId', key: 'lineId', title: '线路', width: 90 },
          { dataIndex: 'stationId', key: 'stationId', title: '车站', width: 120 },
        ];

  return [
    ...groupColumns,
    { dataIndex: 'healthLevel', key: 'healthLevel', title: '健康等级', width: 100 },
    { dataIndex: 'healthScore', key: 'healthScore', title: '总分', width: 80 },
    { dataIndex: 'dimBody', key: 'dimBody', title: '本体/20', width: 90 },
    { dataIndex: 'dimCondition', key: 'dimCondition', title: '工况/25', width: 90 },
    { dataIndex: 'dimFault', key: 'dimFault', title: '故障/25', width: 90 },
    { dataIndex: 'dimMaintain', key: 'dimMaintain', title: '维保/20', width: 90 },
    { dataIndex: 'dimEnv', key: 'dimEnv', title: '环境/10', width: 90 },
    { dataIndex: 'outputs', key: 'outputs', title: '输出参数', width: 240 },
  ];
});

defineExpose({ open });
</script>

<template>
  <PlatformDrawer
    v-model:open="resultOpen"
    destroy-on-close
    size="large"
    :title="`查看结果 - ${resultModel?.modelName ?? ''}`"
  >
    <div class="model-result-drawer">
      <PlatformSegmented
        v-model:value="viewMode"
        :options="[
          { label: '按线路层级', value: 'line' },
          { label: '按设备品种层级', value: 'type' },
        ]"
      />
      <PlatformTable
        :action-column-width="0"
        :columns="resultColumns"
        :data-source="sortedRows"
        :pagination="false"
        :show-index="true"
        :scroll="{ x: 1340 }"
        row-key="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'lineId'">
            {{ lineNameOf((record as ResultVO).lineId) }}
          </template>
          <template v-else-if="column.key === 'healthLevel'">
            <PlatformStatusTag
              :label="healthMetaOf((record as ResultVO).healthLevel).label"
              :status="healthMetaOf((record as ResultVO).healthLevel).status"
            />
          </template>
          <template v-else-if="column.key === 'outputs'">
            {{ outputsOf(record as ResultVO) }}
          </template>
        </template>
      </PlatformTable>
    </div>
  </PlatformDrawer>
</template>

<style scoped>
.model-result-drawer {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
</style>
