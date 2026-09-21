<script setup lang="ts">
import type { PlatformTaskCardTag } from './types';

import { computed } from 'vue';

import { PlatformButton } from '../button';
import { PlatformIcon } from '../icon';
import { PlatformStatusTag } from '../status';

const props = withDefaults(
  defineProps<{
    actionIcon?: string;
    actionLabel?: string;
    description?: string;
    meta?: string;
    progress?: number;
    progressLabel?: string;
    tags?: PlatformTaskCardTag[];
    title: string;
  }>(),
  {
    actionIcon: 'icon-xinzeng',
    actionLabel: '',
    description: '',
    meta: '',
    progress: undefined,
    progressLabel: '',
    tags: () => [],
  },
);

const emit = defineEmits<{
  action: [];
}>();

const progressStyle = computed(() => ({
  width: `${Math.max(0, Math.min(100, props.progress ?? 0))}%`,
}));
</script>

<template>
  <article class="platform-task-card">
    <div class="platform-task-card__hero">
      <div class="platform-task-card__headline">
        <h3>{{ title }}</h3>
        <p v-if="description">{{ description }}</p>
      </div>
      <slot name="action">
        <PlatformButton
          v-if="actionLabel"
          size="small"
          type="primary"
          @click="emit('action')"
        >
          <template #icon>
            <PlatformIcon :icon="actionIcon" />
          </template>
          {{ actionLabel }}
        </PlatformButton>
      </slot>
    </div>

    <div v-if="tags.length > 0" class="platform-task-card__tags">
      <PlatformStatusTag
        v-for="tag in tags"
        :key="`${tag.label}-${tag.status ?? 'default'}`"
        :label="tag.label"
        :status="tag.status ?? 'default'"
      />
    </div>

    <div v-if="meta" class="platform-task-card__meta">
      <span>{{ meta }}</span>
    </div>

    <div
      v-if="progress !== undefined || progressLabel"
      class="platform-task-card__progress-row"
    >
      <div class="platform-task-card__progress">
        <span
          :style="progressStyle"
          class="platform-task-card__progress-bar"
        ></span>
      </div>
      <span v-if="progressLabel">{{ progressLabel }}</span>
    </div>
  </article>
</template>

<style scoped>
.platform-task-card {
  display: flex;
  flex-direction: column;
  gap: 0;
  width: 100%;
  padding: var(--platform-task-card-padding, 16px);
  text-align: left;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-card);
  transition: transform 0.18s ease;
}

.platform-task-card:hover {
  transform: translateY(-4px);
}

.platform-task-card__hero,
.platform-task-card__meta,
.platform-task-card__progress-row {
  display: flex;
  align-items: flex-start;
}

.platform-task-card__hero,
.platform-task-card__progress-row {
  justify-content: space-between;
  gap: 16px;
}

.platform-task-card__hero,
.platform-task-card__tags {
  margin-bottom: var(--platform-task-card-section-gap, 8px);
}

.platform-task-card__headline {
  min-width: 0;
}

.platform-task-card__headline h3,
.platform-task-card__headline p,
.platform-task-card__meta {
  margin: 0;
}

.platform-task-card__headline h3 {
  overflow: hidden;
  color: hsl(var(--foreground));
  font-size: var(--platform-task-card-title-size, 16px);
  font-weight: 700;
  line-height: 24px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-task-card__headline p {
  margin-top: var(--platform-task-card-section-gap, 8px);
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

.platform-task-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--platform-task-card-section-gap, 8px);
}

.platform-task-card__meta,
.platform-task-card__progress-row {
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

.platform-task-card__meta {
  margin-bottom: var(--platform-task-card-progress-gap, 4px);
}

.platform-task-card__progress {
  flex: 1;
  height: 6px;
  overflow: hidden;
  background: hsl(var(--muted));
  border-radius: 999px;
}

.platform-task-card__progress-bar {
  display: block;
  height: 100%;
  background: hsl(var(--st-color-brand));
  border-radius: inherit;
}
</style>
