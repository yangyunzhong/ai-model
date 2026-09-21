<script setup lang="ts">
import type { PlatformStatusBoardColumn } from './types';

import { PlatformProgress } from '../progress';

defineProps<{
  columns: PlatformStatusBoardColumn[];
}>();
</script>

<template>
  <div class="platform-status-board">
    <section
      v-for="column in columns"
      :key="column.key"
      class="platform-status-board__column"
    >
      <header class="platform-status-board__column-header">
        <span
          class="platform-status-board__dot"
          :style="{ backgroundColor: column.color || 'hsl(var(--primary))' }"
        ></span>
        <strong>{{ column.label }}</strong>
        <span>{{ column.count ?? column.items.length }}</span>
      </header>

      <article
        v-for="item in column.items"
        :key="item.id"
        class="platform-status-board__card"
      >
        <h3>{{ item.title }}</h3>
        <p v-if="item.description">{{ item.description }}</p>
        <p v-if="item.meta">{{ item.meta }}</p>
        <PlatformProgress
          :color="column.progressColor || column.color"
          :label="item.title"
          :value="item.progress || 0"
        />
      </article>
    </section>
  </div>
</template>

<style scoped>
.platform-status-board {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.platform-status-board__column {
  min-width: 0;
  min-height: 520px;
  padding: 16px;
  background: hsl(var(--st-color-border-subtle));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: var(--st-radius-card);
}

.platform-status-board__column-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: hsl(var(--foreground));
}

.platform-status-board__column-header strong {
  font-size: var(--st-font-size-base);
  font-weight: 700;
}

.platform-status-board__column-header span:last-child {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 18px;
  padding-inline: 6px;
  color: hsl(var(--st-color-text-tertiary));
  background: hsl(var(--background));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.platform-status-board__dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
}

.platform-status-board__card {
  padding: 16px;
  margin-top: 12px;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--st-color-border-control));
  border-radius: var(--st-radius-card);
}

.platform-status-board__card h3 {
  margin: 0;
  overflow-wrap: anywhere;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  font-weight: 700;
  line-height: var(--st-line-height-lg);
}

.platform-status-board__card p {
  margin: 10px 0 0;
  color: hsl(var(--st-color-text-tertiary));
  font-size: var(--st-font-size-sm);
}

.platform-status-board__card .platform-progress {
  margin-top: 18px;
}

@media (max-width: 1400px) {
  .platform-status-board {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .platform-status-board {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
