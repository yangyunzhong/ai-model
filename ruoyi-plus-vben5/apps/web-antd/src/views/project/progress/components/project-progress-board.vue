<script setup lang="ts">
import type {
  ProjectProgressRecord,
  ProjectProgressStatus,
} from '#/mock/modules/project/progress';

import { computed } from 'vue';

import {
  progressStatusMap,
  progressStatusOrder,
} from '#/mock/modules/project/progress';

const props = defineProps<{
  rows: ProjectProgressRecord[];
}>();

const boardColumns = computed(() =>
  progressStatusOrder.map((status) => ({
    records: props.rows.filter((record) => record.status === status),
    status,
    ...progressStatusMap[status],
  })),
);

function getProgressWidth(record: ProjectProgressRecord) {
  if (record.status === 'pending') {
    return '0%';
  }

  return `${Math.min(record.progress, 100)}%`;
}

function getProgressColor(status: ProjectProgressStatus) {
  return progressStatusMap[status].barColor;
}
</script>

<template>
  <div class="project-progress-board">
    <section
      v-for="column in boardColumns"
      :key="column.status"
      class="project-progress-board__column"
    >
      <header class="project-progress-board__column-header">
        <span
          class="project-progress-board__dot"
          :style="{ backgroundColor: column.tokenColor }"
        ></span>
        <strong>{{ column.label }}</strong>
        <span>{{ column.records.length }}</span>
      </header>

      <div class="project-progress-board__column-body">
        <article
          v-for="record in column.records"
          :key="record.id"
          class="project-progress-board__card"
        >
          <h2>{{ record.name }}</h2>
          <p>{{ record.department }} · {{ record.manager }}</p>
          <div class="project-progress-board__track">
            <span
              :style="{
                backgroundColor: getProgressColor(record.status),
                width: getProgressWidth(record),
              }"
            ></span>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<style scoped>
.project-progress-board {
  display: grid;
  align-items: start;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.project-progress-board__column {
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding: 16px;
  max-height: min(720px, calc(100dvh - 220px));
  overflow: hidden;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: var(--st-radius-card);
}

.project-progress-board__column-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: hsl(var(--foreground));
}

.project-progress-board__column-body {
  min-height: 0;
  overflow-y: auto;
}

.project-progress-board__column-header strong {
  font-size: 16px;
  font-weight: 700;
}

.project-progress-board__column-header span:last-child {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 18px;
  padding-inline: 6px;
  color: #64748b;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.project-progress-board__dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
}

.project-progress-board__card {
  padding: 16px;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: var(--st-radius-card);
}

.project-progress-board__card h2 {
  margin: 0;
  overflow-wrap: anywhere;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  font-weight: 700;
  line-height: var(--st-line-height-lg);
}

.project-progress-board__card p {
  margin: 12px 0 18px;
  color: #64748b;
  font-size: var(--st-font-size-sm);
}

.project-progress-board__track {
  height: 6px;
  overflow: hidden;
  background: #e2e8f0;
  border-radius: 999px;
}

.project-progress-board__track span {
  display: block;
  height: 100%;
  min-width: 0;
  border-radius: inherit;
}

@media (max-width: 1400px) {
  .project-progress-board {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .project-progress-board {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
