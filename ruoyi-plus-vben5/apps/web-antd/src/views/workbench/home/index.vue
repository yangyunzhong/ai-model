<script setup lang="ts">
import type { PlatformNoticeListItem } from '@st/platform-ui';
import type { EChartsOption } from 'echarts';

import { computed } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';

import { Avatar } from 'antdv-next';

import {
  PlatformEchartsPanel,
  PlatformEntryCard,
  PlatformNoticeList,
  PlatformSection,
  PlatformSectionAction,
  PlatformStatCard,
} from '@st/platform-ui';
import { preferences } from '@vben/preferences';

import { execRecords, getModelResultRows, models, tasks } from '../../model/data';

type ModuleEntry = {
  color: string;
  icon: string;
  key: string;
  label: string;
  path: string;
};

type PendingItem = PlatformNoticeListItem & {
  path: string;
};

type StatCardItem = {
  description: string;
  title: string;
  type: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  unit: string;
  value: number | string;
};

const router = useRouter();
const userStore = useUserStore();

const currentUserName = '系统管理员';
const avatarSrc = computed(
  () => userStore.userInfo?.avatar || preferences.app.defaultAvatar,
);

const moduleEntries: ModuleEntry[] = [
  { color: '#3b82f6', icon: 'lucide:chart-pie', key: 'overview', label: '模型概览', path: '/model-overview/dashboard' },
  { color: '#16a34a', icon: 'lucide:boxes', key: 'manage', label: '模型管理', path: '/model-manage/list' },
  { color: '#8b5cf6', icon: 'lucide:git-compare-arrows', key: 'compare', label: '数据比对', path: '/model-comparison/task' },
  { color: '#f59e0b', icon: 'lucide:list-checks', key: 'task', label: '模型任务', path: '/model-task/list' },
  { color: '#0ea5e9', icon: 'lucide:folder-tree', key: 'category', label: '模型类别', path: '/model-category/manage' },
];

const publishedModels = computed(() =>
  models.value.filter((item) => item.status === '已发布'),
);
const draftModels = computed(() =>
  models.value.filter((item) => item.status === '草稿'),
);
const offlineModels = computed(() =>
  models.value.filter((item) => item.status === '已下架'),
);
const runningTasks = computed(() =>
  tasks.value.filter((item) => item.status === '运行中'),
);
const abnormalExecs = computed(() =>
  execRecords.value.filter((item) => item.status === '失败' || item.status === '超时'),
);
const riskDevices = computed(() => {
  const deviceIds = new Set<string>();

  for (const model of publishedModels.value) {
    for (const row of getModelResultRows(model.id)) {
      if (row.healthLevel === '高危') {
        deviceIds.add(row.deviceId);
      }
    }
  }

  return [...deviceIds];
});

const failedRecentCount = computed(() =>
  execRecords.value.filter((item) => {
    if (item.status !== '失败') {
      return false;
    }
    const startTime = new Date(item.startTime.replace(/-/g, '/')).getTime();
    return Date.now() - startTime <= 7 * 24 * 60 * 60 * 1000;
  }).length,
);

const statCards = computed<StatCardItem[]>(() => [
  { description: '全部状态模型数', title: '模型总数', type: 'primary', unit: '个', value: models.value.length },
  { description: '按设备类型划分', title: '模型分类数', type: 'info', unit: '个', value: models.value.length ? new Set(models.value.map((item) => item.categoryId)).size : 0 },
  { description: '脚本上传至本平台', title: '本地注册数', type: 'success', unit: '个', value: models.value.filter((item) => item.registerType === '本地注册').length },
  { description: '外部系统登记', title: '第三方注册数', type: 'warning', unit: '个', value: models.value.filter((item) => item.registerType === '第三方注册').length },
  { description: '执行失败需关注', title: '近7天运行失败', type: 'danger', unit: '次', value: failedRecentCount.value },
  { description: '最近评估高危设备', title: '高危设备数', type: 'danger', unit: '台', value: riskDevices.value.length },
]);

const statusStrip = computed(() => [
  { count: publishedModels.value.length, key: 'published', label: '已发布', path: '/model-overview/dashboard' },
  { count: draftModels.value.length, key: 'draft', label: '草稿', path: '/model-manage/list' },
  { count: offlineModels.value.length, key: 'offline', label: '已下架', path: '/model-manage/list' },
  { count: runningTasks.value.length, key: 'task', label: '运行中任务', path: '/model-task/list' },
]);

const execStatusPalette: Record<string, { color: string; label: string }> = {
  success: { color: '#16a34a', label: '成功' },
  partial: { color: '#f59e0b', label: '部分成功' },
  running: { color: '#3b82f6', label: '运行中' },
  failed: { color: '#ef4444', label: '失败' },
  timeout: { color: '#f97316', label: '超时' },
};

const execStatusDistribution = computed(() => {
  const buckets = [
    { count: 0, key: 'success', label: '成功', status: '成功' },
    { count: 0, key: 'partial', label: '部分成功', status: '部分成功' },
    { count: 0, key: 'running', label: '运行中', status: '运行中' },
    { count: 0, key: 'failed', label: '失败', status: '失败' },
    { count: 0, key: 'timeout', label: '超时', status: '超时' },
  ];

  for (const record of execRecords.value) {
    const bucket = buckets.find((item) => item.status === record.status);

    if (bucket) {
      bucket.count += 1;
    }
  }

  return buckets.filter((item) => item.count > 0);
});

const execTotal = computed(() =>
  execStatusDistribution.value.reduce((sum, item) => sum + item.count, 0),
);

const execSuccessRate = computed(() => {
  if (execTotal.value === 0) {
    return 0;
  }

  const successCount =
    execStatusDistribution.value.find((item) => item.key === 'success')?.count ?? 0;
  return Math.round((successCount / execTotal.value) * 100);
});

const execStatusChartOption = computed<EChartsOption>(() => ({
  tooltip: { formatter: '{b}：{c} 次（{d}%）', trigger: 'item' },
  legend: {
    bottom: 0,
    icon: 'circle',
    itemGap: 16,
    itemHeight: 8,
    itemWidth: 8,
    textStyle: { color: '#6b7280', fontSize: 12 },
  },
  series: [
    {
      type: 'pie',
      center: ['50%', '44%'],
      radius: ['52%', '74%'],
      avoidLabelOverlap: true,
      itemStyle: { borderColor: '#fff', borderRadius: 4, borderWidth: 2 },
      label: { show: false },
      labelLine: { show: false },
      data: execStatusDistribution.value.map((item) => ({
        name: item.label,
        value: item.count,
        itemStyle: { color: execStatusPalette[item.key]?.color },
      })),
    },
  ],
}));

const execNoticeStatusMap: Record<string, PlatformNoticeListItem['status']> = {
  成功: 'success',
  部分成功: 'warning',
  失败: 'danger',
  超时: 'danger',
  运行中: 'info',
};

const pendingItems = computed<PendingItem[]>(() => {
  const items: PendingItem[] = [];

  if (draftModels.value.length > 0) {
    items.push({
      id: 'pending-draft',
      actionText: '去处理',
      description: draftModels.value.map((item) => item.modelName).join('、'),
      meta: '模型管理',
      path: '/model-manage/list',
      status: 'warning',
      tag: '待发布',
      title: `${draftModels.value.length} 个草稿模型待发布`,
    });
  }

  if (abnormalExecs.value.length > 0) {
    items.push({
      id: 'pending-exec',
      actionText: '看记录',
      description: abnormalExecs.value
        .map((item) => item.errorMsg || `${item.execNo} ${item.status}`)
        .join('；'),
      meta: '模型任务',
      path: '/model-task/list',
      status: 'danger',
      tag: '执行异常',
      title: `${abnormalExecs.value.length} 条执行记录异常`,
    });
  }

  if (riskDevices.value.length > 0) {
    items.push({
      id: 'pending-risk',
      actionText: '去复核',
      description: `设备编号：${riskDevices.value.join('、')}`,
      meta: '模型概览',
      path: '/model-overview/dashboard',
      status: 'danger',
      tag: '高危设备',
      title: `${riskDevices.value.length} 台高危设备待复核`,
    });
  }

  if (offlineModels.value.length > 0) {
    items.push({
      id: 'pending-offline',
      actionText: '去看看',
      description: offlineModels.value.map((item) => item.modelName).join('、'),
      meta: '模型管理',
      path: '/model-manage/list',
      status: 'default',
      tag: '已下架',
      title: `${offlineModels.value.length} 个模型处于下架状态`,
    });
  }

  return items;
});

const taskListItems = computed<PlatformNoticeListItem[]>(() =>
  tasks.value.map((task) => {
    const model = models.value.find((item) => item.id === task.modelId);
    const running = task.status === '运行中';

    return {
      id: `task-${task.id}`,
      actionText: running ? '立即执行' : '已暂停',
      description: `模型：${model?.modelName ?? '-'} · 上次执行 ${task.lastRunTime || '暂无'}`,
      disabled: !running,
      meta: `下次执行 ${task.nextExecTime}`,
      status: running ? 'success' : 'default',
      tag: task.cronDesc,
      title: task.taskName,
    };
  }),
);

const recentExecItems = computed<PlatformNoticeListItem[]>(() =>
  [...execRecords.value]
    .sort((a, b) => b.startTime.localeCompare(a.startTime))
    .slice(0, 6)
    .map((record) => {
      const model = models.value.find((item) => item.id === record.modelId);

      return {
        id: record.execNo,
        description: `${record.status} · 耗时 ${(record.costMs / 1000).toFixed(0)}s · 设备 ${record.deviceCount} 台`,
        meta: record.startTime,
        status: execNoticeStatusMap[record.status],
        tag: record.triggerType,
        title: model?.modelName ?? record.execNo,
      };
    }),
);

function openPath(path: string) {
  router.push(path);
}

function handlePendingAction(item: PlatformNoticeListItem) {
  openPath((item as PendingItem).path);
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="workbench-home">
      <section class="workbench-home__hero">
        <Avatar :size="68" :src="avatarSrc">
          系
        </Avatar>

        <div class="workbench-home__hero-copy">
          <div class="workbench-home__hero-row">
            <div class="workbench-home__hero-text">
              <div class="workbench-home__greeting-row">
                <p class="workbench-home__greeting">下午好，{{ currentUserName }}</p>
              </div>
              <p class="workbench-home__subtitle">
                欢迎使用模型管理平台，当前共
                <span>{{ models.length }}</span>
                个模型，
                <span>{{ runningTasks.length }}</span>
                个定时任务运行中
              </p>
            </div>
          </div>
        </div>
      </section>

      <section class="workbench-home__stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          v-bind="card"
        />
      </section>

      <div class="workbench-home__grid">
        <div class="workbench-home__grid-row workbench-home__grid-row--top">
          <PlatformSection
            class="workbench-home__surface workbench-home__surface--module"
            title="模型模块入口"
          >
            <template #extra>
              <PlatformSectionAction
                icon="lucide:chevron-right"
                label="模型概览"
                @click="openPath('/model-overview/dashboard')"
              />
            </template>

            <div class="workbench-home__shortcut-grid">
              <PlatformEntryCard
                v-for="item in moduleEntries"
                :key="item.key"
                :icon="item.icon"
                :icon-background="item.color"
                :label="item.label"
                variant="shortcut"
                @click="openPath(item.path)"
              />
            </div>

            <div class="workbench-home__access-strip">
              <span class="workbench-home__access-title">模型状态：</span>
              <div class="workbench-home__access-links">
                <button
                  v-for="item in statusStrip"
                  :key="item.key"
                  class="workbench-home__access-link"
                  type="button"
                  @click="openPath(item.path)"
                >
                  {{ item.label }} {{ item.count }}
                </button>
              </div>
            </div>
          </PlatformSection>

          <PlatformEchartsPanel
            class="workbench-home__surface workbench-home__surface--chart"
            :height="'300px'"
            :option="execStatusChartOption"
            :description="`累计 ${execTotal} 次执行 · 成功率 ${execSuccessRate}%`"
            title="执行状态分布"
          />
        </div>

        <div class="workbench-home__grid-row workbench-home__grid-row--bottom">
          <PlatformSection
            class="workbench-home__surface workbench-home__surface--pending"
            title="待处理事项"
          >
            <template #extra>
              <PlatformSectionAction
                kind="refresh"
                label="刷新"
                @click="() => {}"
              />
            </template>

            <PlatformNoticeList
              class="workbench-home__pending-list"
              :items="pendingItems"
              empty-text="当前没有待处理事项"
              variant="workbench"
              @action="handlePendingAction"
            />
          </PlatformSection>

          <div class="workbench-home__right-stack">
            <PlatformSection
              class="workbench-home__surface workbench-home__surface--task"
              title="模型运行任务"
            >
              <template #extra>
                <PlatformSectionAction
                  icon="lucide:chevron-right"
                  label="全部任务"
                  @click="openPath('/model-task/list')"
                />
              </template>

              <PlatformNoticeList
                class="workbench-home__task-list"
                :items="taskListItems"
                empty-text="暂无定时任务"
                variant="workbench"
                @action="openPath('/model-task/list')"
              />
            </PlatformSection>

            <PlatformSection
              class="workbench-home__surface workbench-home__surface--exec"
              title="最近执行记录"
            >
              <template #extra>
                <PlatformSectionAction
                  kind="more"
                  label="更多记录"
                  @click="openPath('/model-task/list')"
                />
              </template>

              <PlatformNoticeList
                class="workbench-home__exec-list"
                :items="recentExecItems"
                empty-text="暂无执行记录"
                variant="workbench"
              />
            </PlatformSection>
          </div>
        </div>
      </div>
    </div>
  </Page>
</template>

<style scoped>
.workbench-home {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-width: 0;
}

.workbench-home__hero {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.workbench-home__hero-copy {
  flex: 1;
  min-width: 0;
}

.workbench-home__hero-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-width: 0;
}

.workbench-home__hero-text {
  min-width: 0;
}

.workbench-home__greeting-row {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.workbench-home__greeting {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 24px;
  font-weight: 700;
  line-height: 32px;
}

.workbench-home__subtitle {
  margin: 4px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: 14px;
  line-height: 22px;
}

.workbench-home__subtitle span {
  color: hsl(var(--primary));
  font-weight: 700;
}

.workbench-home__stat-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.workbench-home__grid {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
}

.workbench-home__grid-row {
  display: grid;
  grid-template-columns: minmax(320px, 0.92fr) minmax(0, 1.4fr);
  gap: var(--st-layout-section-gap);
  align-items: stretch;
  min-width: 0;
}

.workbench-home__grid-row > * {
  min-width: 0;
}

.workbench-home__surface {
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

.workbench-home__surface :deep(.platform-section__header) {
  margin-bottom: 0;
}

.workbench-home__surface--module {
  min-height: 300px;
  height: 100%;
}

.workbench-home__surface--chart :deep(.platform-section__header) {
  margin-bottom: 12px;
}

.workbench-home__right-stack {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 0;
}

.workbench-home__shortcut-grid {
  display: grid;
  flex: 1;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  align-items: start;
  margin-top: 16px;
}

.workbench-home__access-strip {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  min-height: 52px;
  padding: 10px 18px;
  margin-top: 8px;
  background: hsl(var(--primary) / 10%);
  border-radius: 8px;
}

.workbench-home__access-title {
  flex: 0 0 auto;
  color: hsl(var(--foreground));
  font-size: 15px;
  font-weight: 700;
  line-height: 22px;
}

.workbench-home__access-links {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.workbench-home__access-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  min-height: 32px;
  padding: 0 12px;
  color: hsl(var(--foreground));
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  white-space: nowrap;
  background: hsl(var(--background));
  border: 0;
  border-radius: 8px;
  transition:
    color 0.16s ease,
    background 0.16s ease,
    box-shadow 0.16s ease;
}

.workbench-home__access-link:hover {
  color: hsl(var(--primary));
  box-shadow: inset 0 0 0 1px hsl(var(--primary) / 20%);
}

.workbench-home__pending-list,
.workbench-home__task-list {
  margin-top: 16px;
}

.workbench-home__exec-list {
  flex: 1;
  margin-top: 16px;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;
}

@media (max-width: 1440px) {
  .workbench-home__stat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1280px) {
  .workbench-home__grid-row {
    grid-template-columns: 1fr;
  }

  .workbench-home__shortcut-grid {
    grid-template-columns: repeat(5, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .workbench-home__stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .workbench-home__hero,
  .workbench-home__hero-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .workbench-home__access-strip {
    align-items: flex-start;
    flex-direction: column;
  }

  .workbench-home__access-links {
    width: 100%;
  }

  .workbench-home__shortcut-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
