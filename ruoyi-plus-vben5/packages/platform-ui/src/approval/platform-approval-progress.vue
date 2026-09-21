<script setup lang="ts">
import type { PlatformApprovalProgressItem } from './types';

import { computed, h } from 'vue';

import { VbenIcon } from '@vben/icons';

import { Empty, Timeline, TimelineItem } from 'antdv-next';

const props = withDefaults(
  defineProps<{
    items?: PlatformApprovalProgressItem[];
    title?: string;
  }>(),
  {
    items: () => [],
    title: '审批进度',
  },
);

const hasItems = computed(() => props.items.length > 0);

function getDotClass(status: PlatformApprovalProgressItem['status']) {
  return `platform-approval-progress__dot--${status}`;
}

function getDotIcon(item: PlatformApprovalProgressItem) {
  if (item.dotIcon) {
    return item.dotIcon;
  }

  switch (item.status) {
    case 'current': {
      return 'lucide:clock-3';
    }
    case 'finished': {
      return 'lucide:check';
    }
    default: {
      return 'lucide:user-round';
    }
  }
}

function renderDot(item: PlatformApprovalProgressItem) {
  return h(
    'div',
    {
      class: ['platform-approval-progress__dot', getDotClass(item.status)],
    },
    [h(VbenIcon, { icon: getDotIcon(item) })],
  );
}

</script>

<template>
  <aside class="platform-approval-progress">
    <header class="platform-approval-progress__header">
      <h3>{{ title }}</h3>
    </header>

    <div class="platform-approval-progress__body">
      <Timeline v-if="hasItems" class="platform-approval-progress__timeline">
        <TimelineItem
          v-for="item in items"
          :key="item.id"
          class="platform-approval-progress__item"
          :dot="renderDot(item)"
        >
          <article class="platform-approval-progress__card">
            <div class="platform-approval-progress__card-head">
              <span class="platform-approval-progress__title">
                {{ item.title }}
              </span>
              <span v-if="item.time" class="platform-approval-progress__time">
                {{ item.time }}
              </span>
            </div>

            <div class="platform-approval-progress__assignee">
              <div class="platform-approval-progress__meta">
                <strong>{{ item.assignee }}</strong>
                <span v-if="item.department">{{ item.department }}</span>
              </div>
            </div>
          </article>
        </TimelineItem>
      </Timeline>

      <Empty v-else description="暂无审批进度" />
    </div>
  </aside>
</template>

<style scoped>
.platform-approval-progress {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: hsl(var(--background));
  border-left: 1px solid hsl(var(--border));
}

.platform-approval-progress__header {
  padding: 18px 24px 16px;
}

.platform-approval-progress__header h3 {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 18px;
  font-weight: 700;
  line-height: 28px;
}

.platform-approval-progress__body {
  flex: 1;
  min-height: 0;
  padding: 8px 24px 24px 24px;
  overflow: auto;
}

.platform-approval-progress__timeline {
  margin-left: 6px;
}

.platform-approval-progress__item :deep(.ant-timeline-item-head) {
  width: auto;
  height: auto;
  margin-top: 0;
  line-height: 1;
  background: transparent;
  border: 0;
}

.platform-approval-progress__item :deep(.ant-timeline-item-tail) {
  inset-inline-start: 18px;
  top: 38px;
  height: calc(100% - 12px);
  border-inline-start-width: 2px;
  border-inline-start-color: hsl(var(--border));
}

.platform-approval-progress__item :deep(.ant-timeline-item-content) {
  inset-block-start: -4px;
  margin-inline-start: 24px;
}

.platform-approval-progress__dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: #fff;
  border-radius: 999px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.12);
}

.platform-approval-progress__dot--finished {
  background: #4096ff;
}

.platform-approval-progress__dot--current {
  background: #faad14;
}

.platform-approval-progress__dot--pending {
  color: hsl(var(--muted-foreground));
  background: hsl(var(--muted));
  box-shadow: none;
}

.platform-approval-progress__card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 20px;
}

.platform-approval-progress__card-head {
  display: flex;
  gap: 12px;
  align-items: baseline;
  justify-content: space-between;
}

.platform-approval-progress__title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  color: hsl(var(--muted-foreground));
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 15px;
  line-height: 24px;
}

.platform-approval-progress__time {
  flex-shrink: 0;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
  line-height: 22px;
}

.platform-approval-progress__assignee {
  display: flex;
  align-items: center;
}

.platform-approval-progress__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 2px 12px;
  align-items: center;
  min-width: 0;
}

.platform-approval-progress__meta strong,
.platform-approval-progress__meta span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-approval-progress__meta strong {
  color: hsl(var(--foreground));
  font-size: 16px;
  line-height: 24px;
}

.platform-approval-progress__meta span {
  color: hsl(var(--muted-foreground));
  font-size: 14px;
  line-height: 22px;
}
</style>
