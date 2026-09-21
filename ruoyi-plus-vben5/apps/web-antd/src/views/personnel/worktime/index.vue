<script setup lang="ts">
import type { WorktimeAlert, WorktimeStatCard } from '#/mock/modules/personnel/worktime';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { VbenIcon } from '@vben/icons';

import {
  PlatformButton,
  PlatformSectionTitle,
  PlatformStatCard,
  PlatformStatusTag,
  PlatformViewToolbar,
} from '@st/platform-ui';

import {
  getWorktimeAlerts,
  getWorktimeStats,
  notifyWorktimeRectification,
  worktimeAlertLevelMap,
  worktimeThresholdText,
} from '#/mock/modules/personnel/worktime';

const loading = ref(false);
const alertRows = ref<WorktimeAlert[]>([]);
const statCards = ref<WorktimeStatCard[]>([]);

onMounted(loadWorktimePage);

async function loadWorktimePage() {
  loading.value = true;
  try {
    const [alerts, stats] = await Promise.all([
      getWorktimeAlerts(),
      getWorktimeStats(),
    ]);
    alertRows.value = alerts;
    statCards.value = stats;
  } finally {
    loading.value = false;
  }
}

async function handleNotify(alert: WorktimeAlert) {
  await notifyWorktimeRectification(alert.id);
  window.message.success(`${alert.name} 的整改通知已发送`);
  await loadWorktimePage();
}

function getLevelMeta(level: WorktimeAlert['level']) {
  return worktimeAlertLevelMap[level];
}

function getNameInitial(name: string) {
  return name.slice(0, 1);
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="personnel-worktime-page">
      <PlatformViewToolbar
        description="工时预警、整改通知与兼职风险闭环管理"
        title="工时与兼职管控"
      />

      <section class="personnel-worktime-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :loading="loading"
          v-bind="card"
        />
      </section>

      <section class="platform-surface worktime-alert-panel">
        <PlatformSectionTitle
          class="worktime-alert-panel__header"
          title="超工时人员预警"
        >
          <template #extra>
            <span class="worktime-alert-panel__threshold">
              阈值：{{ worktimeThresholdText }}
            </span>
          </template>
        </PlatformSectionTitle>

        <div class="worktime-alert-grid">
          <article
            v-for="alert in alertRows"
            :key="alert.id"
            class="worktime-alert-card"
          >
            <div class="worktime-alert-card__header">
              <div class="worktime-alert-card__person">
                <span class="worktime-alert-card__avatar">
                  {{ getNameInitial(alert.name) }}
                </span>
                <div>
                  <h3>{{ alert.name }}</h3>
                  <p>{{ alert.position }} · {{ alert.contractor }}</p>
                </div>
              </div>
              <PlatformStatusTag
                :label="getLevelMeta(alert.level).label"
                :status="getLevelMeta(alert.level).status"
              />
            </div>

            <p class="worktime-alert-card__project">
              <VbenIcon icon="lucide:building-2" />
              {{ alert.project }}
            </p>

            <div class="worktime-alert-card__metrics">
              <div class="worktime-alert-card__metric">
                <span>本月工时</span>
                <strong>{{ alert.monthlyHours }}h</strong>
              </div>
              <div
                class="worktime-alert-card__metric worktime-alert-card__metric--danger"
              >
                <span>超出时长</span>
                <strong>{{ alert.overHours }}h</strong>
              </div>
            </div>

            <div class="worktime-alert-card__footer">
              <span>
                {{
                  alert.notifiedAt
                    ? `已通知 ${alert.notifiedAt}`
                    : '待通知整改'
                }}
              </span>
              <PlatformButton
                class="worktime-alert-card__action"
                @click="handleNotify(alert)"
              >
                通知整改
              </PlatformButton>
            </div>
          </article>

          <div v-if="alertRows.length === 0" class="worktime-alert-empty">
            暂无超工时人员预警
          </div>
        </div>
      </section>
    </div>
  </Page>
</template>

<style scoped>
.personnel-worktime-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.worktime-alert-card h3 {
  margin: 0;
  color: hsl(var(--foreground));
  font-weight: 700;
}

.worktime-alert-card p,
.worktime-alert-card__footer {
  margin: 0;
  color: hsl(var(--muted-foreground));
}

.personnel-worktime-stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--st-layout-section-gap);
}

.worktime-alert-panel {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.worktime-alert-panel__header {
  margin-bottom: 20px;
}

.worktime-alert-panel__threshold {
  flex: 0 0 auto;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 24px;
}

.worktime-alert-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.worktime-alert-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
  padding: 18px;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-card);
  box-shadow: 0 8px 24px hsl(220 32% 42% / 6%);
  transition: transform 0.18s ease;
}

.worktime-alert-card:hover {
  transform: translateY(-4px);
}

.worktime-alert-card__header,
.worktime-alert-card__person,
.worktime-alert-card__project,
.worktime-alert-card__footer {
  display: flex;
  align-items: center;
}

.worktime-alert-card__header,
.worktime-alert-card__footer {
  justify-content: space-between;
  gap: 12px;
}

.worktime-alert-card__person {
  gap: 12px;
  min-width: 0;
}

.worktime-alert-card__avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  flex: 0 0 56px;
  color: hsl(var(--primary-foreground));
  font-size: 28px;
  font-weight: 700;
  background: hsl(var(--primary));
  border-radius: 14px;
}

.worktime-alert-card h3 {
  font-size: var(--st-font-size-base);
  line-height: 22px;
}

.worktime-alert-card__person p,
.worktime-alert-card__project {
  font-size: var(--st-font-size-sm);
}

.worktime-alert-card__project {
  gap: 6px;
  min-width: 0;
}

.worktime-alert-card__project :deep(svg) {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
}

.worktime-alert-card__metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.worktime-alert-card__metric {
  padding: 14px;
  background: hsl(var(--muted) / 45%);
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-control);
}

.worktime-alert-card__metric span {
  display: block;
  margin-bottom: 6px;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

.worktime-alert-card__metric strong {
  color: hsl(var(--foreground));
  font-size: 24px;
  line-height: 30px;
}

.worktime-alert-card__metric--danger strong {
  color: hsl(var(--destructive));
}

.worktime-alert-card__footer {
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

:deep(.worktime-alert-card__action.ant-btn) {
  min-width: 88px;
  color: hsl(var(--primary));
  background: transparent;
  border-color: hsl(var(--primary));
}

:deep(.worktime-alert-card__action.ant-btn:not(:disabled):not(.ant-btn-disabled):hover),
:deep(.worktime-alert-card__action.ant-btn:not(:disabled):not(.ant-btn-disabled):focus-visible) {
  color: hsl(var(--primary-foreground));
  background: hsl(var(--primary));
  border-color: hsl(var(--primary));
}

.worktime-alert-empty {
  display: flex;
  grid-column: 1 / -1;
  align-items: center;
  justify-content: center;
  min-height: 160px;
  color: hsl(var(--muted-foreground));
  border: 1px dashed hsl(var(--border));
  border-radius: var(--st-radius-card);
}

@media (max-width: 1280px) {
  .worktime-alert-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 960px) {
  .worktime-alert-panel__header {
    align-items: stretch;
    flex-direction: column;
  }

  .personnel-worktime-stat-grid,
  .worktime-alert-grid {
    grid-template-columns: 1fr;
  }

  .worktime-alert-panel__threshold {
    align-self: flex-start;
  }
}

@media (max-width: 640px) {
  .worktime-alert-card__avatar {
    width: 48px;
    height: 48px;
    flex-basis: 48px;
    font-size: 24px;
  }
}
</style>
