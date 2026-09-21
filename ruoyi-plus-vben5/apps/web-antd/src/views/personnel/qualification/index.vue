<script setup lang="ts">
import type { PlatformNoticeListItem } from '@st/platform-ui';

import type { AccessRuleItem } from '#/mock/modules/personnel/qualification';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { VbenIcon } from '@vben/icons';

import {
  PlatformNoticeList,
  PlatformSection,
  PlatformStatCard,
  PlatformViewToolbar,
} from '@st/platform-ui';

import {
  getAccessRuleList,
  getQualificationAccessStats,
  getQualificationWarningList,
  type QualificationAccessStatCard,
  sendQualificationReminder,
} from '#/mock/modules/personnel/qualification';

const loading = ref(false);
const ruleRows = ref<AccessRuleItem[]>([]);
const statCards = ref<QualificationAccessStatCard[]>([]);
const warningRows = ref<PlatformNoticeListItem[]>([]);

onMounted(loadQualificationAccess);

async function loadQualificationAccess() {
  loading.value = true;
  try {
    const [stats, warnings, rules] = await Promise.all([
      getQualificationAccessStats(),
      getQualificationWarningList(),
      getAccessRuleList(),
    ]);
    statCards.value = stats;
    warningRows.value = warnings;
    ruleRows.value = rules;
  } finally {
    loading.value = false;
  }
}

async function handleSendReminder(item: PlatformNoticeListItem) {
  await sendQualificationReminder(item.id);
  window.message.success(`${item.title} 的资质到期提醒已发送`);
  await loadQualificationAccess();
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="personnel-qualification-page">
      <PlatformViewToolbar
        description="资质到期预警、准入规则自动校验"
        title="资质与准入管控"
      />

      <section class="personnel-qualification-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :loading="loading"
          v-bind="card"
        />
      </section>

      <section class="personnel-qualification-module-grid">
        <PlatformSection title="资质到期预警">
          <PlatformNoticeList
            empty-text="暂无资质到期预警"
            :items="warningRows"
            :loading="loading"
            @action="handleSendReminder"
          />
        </PlatformSection>

        <PlatformSection title="准入规则配置">
          <div class="access-rule-grid">
            <article
              v-for="rule in ruleRows"
              :key="rule.id"
              class="access-rule-card"
              :class="`access-rule-card--${rule.type}`"
            >
              <div class="access-rule-card__header">
                <span class="access-rule-card__icon">
                  <VbenIcon :icon="rule.icon" />
                </span>
                <strong>{{ rule.title }}</strong>
              </div>
              <p>{{ rule.description }}</p>
              <span class="access-rule-card__status">
                <span></span>
                {{ rule.statusText }}
              </span>
            </article>
          </div>
        </PlatformSection>
      </section>
    </div>
  </Page>
</template>

<style scoped>
.personnel-qualification-page {
  display: flex;
  min-height: 100%;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
}

.personnel-qualification-stat-grid,
.personnel-qualification-module-grid {
  display: grid;
  gap: var(--st-layout-section-gap);
}

.personnel-qualification-stat-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.personnel-qualification-module-grid {
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  align-items: start;
}

.personnel-qualification-module-grid :deep(.platform-section) {
  min-height: 100%;
}

.access-rule-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.access-rule-card {
  --access-rule-color: hsl(var(--primary));

  min-width: 0;
  padding: 16px;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--st-color-table-outline));
  border-radius: var(--st-radius-control);
}

.access-rule-card--success {
  --access-rule-color: hsl(var(--success));
}

.access-rule-card--warning {
  --access-rule-color: hsl(var(--warning));
}

.access-rule-card--danger {
  --access-rule-color: hsl(var(--st-color-danger));
}

.access-rule-card--info {
  --access-rule-color: hsl(var(--st-color-stat-card-info));
}

.access-rule-card__header {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 8px;
}

.access-rule-card__icon {
  display: inline-flex;
  width: 22px;
  height: 22px;
  flex: 0 0 22px;
  align-items: center;
  justify-content: center;
  color: var(--access-rule-color);
}

.access-rule-card__icon :deep(svg) {
  width: 16px;
  height: 16px;
}

.access-rule-card strong {
  min-width: 0;
  overflow: hidden;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  font-weight: 700;
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.access-rule-card p {
  min-height: 40px;
  margin: 8px 0 14px;
  overflow: hidden;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 20px;
}

.access-rule-card__status {
  display: inline-flex;
  height: 24px;
  align-items: center;
  gap: 6px;
  padding-inline: 10px;
  color: hsl(var(--success));
  font-size: var(--st-font-size-sm);
  font-weight: 700;
  line-height: 24px;
  background: hsl(var(--success) / 10%);
  border-radius: var(--st-radius-control);
}

.access-rule-card__status span {
  width: 6px;
  height: 6px;
  background: currentColor;
  border-radius: 999px;
}

@media (max-width: 1200px) {
  .personnel-qualification-module-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .access-rule-grid,
  .personnel-qualification-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
