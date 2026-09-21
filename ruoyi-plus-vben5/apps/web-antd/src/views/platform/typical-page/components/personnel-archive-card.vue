<script setup lang="ts">
import type { PersonnelArchiveRecord } from '#/mock/modules/platform/typical-page';

import { PlatformStatusTag } from '@st/platform-ui';

import {
  getPersonnelArchiveEmploymentMeta,
  getPersonnelArchiveInitial,
  getPersonnelArchiveTags,
} from '#/mock/modules/platform/typical-page';

const props = defineProps<{
  record: PersonnelArchiveRecord;
}>();

const emit = defineEmits<{
  click: [record: PersonnelArchiveRecord];
}>();

function handleClick() {
  emit('click', props.record);
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key !== 'Enter' && event.key !== ' ') {
    return;
  }

  event.preventDefault();
  handleClick();
}
</script>

<template>
  <article
    class="personnel-archive-card"
    role="button"
    tabindex="0"
    @click="handleClick"
    @keydown="handleKeydown"
  >
    <header class="personnel-archive-card__header">
      <span
        class="personnel-archive-card__avatar"
        :style="{ backgroundColor: record.theme }"
      >
        {{ getPersonnelArchiveInitial(record.name) }}
      </span>

      <div class="personnel-archive-card__summary">
        <div class="personnel-archive-card__headline">
          <h3>{{ record.name }}</h3>
          <PlatformStatusTag
            :label="getPersonnelArchiveEmploymentMeta(record.status).label"
            :status="getPersonnelArchiveEmploymentMeta(record.status).status"
          />
        </div>
        <p>
          {{ record.contractor }} · {{ record.position }} · {{ record.age }}岁
        </p>
      </div>
    </header>

    <div class="personnel-archive-card__chips">
      <span
        v-for="chip in getPersonnelArchiveTags(record)"
        :key="chip.label"
        class="personnel-archive-card__chip"
        :class="`personnel-archive-card__chip--${chip.tone}`"
      >
        {{ chip.label }}
      </span>
    </div>
  </article>
</template>

<style scoped>
.personnel-archive-card {
  display: flex;
  min-width: 0;
  min-height: 118px;
  flex-direction: column;
  gap: 14px;
  padding: 16px;
  overflow: hidden;
  cursor: pointer;
  background: hsl(var(--background));
  border-radius: var(--st-radius-card);
  box-shadow: 0 1px 2px rgb(15 23 42 / 4%);
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease;
}

.personnel-archive-card:hover {
  box-shadow:
    0 12px 24px rgb(15 23 42 / 8%),
    0 2px 8px rgb(15 23 42 / 6%);
  transform: translateY(-4px);
}

.personnel-archive-card:focus-visible {
  box-shadow:
    0 12px 24px rgb(15 23 42 / 8%),
    0 2px 8px rgb(15 23 42 / 6%);
  transform: translateY(-4px);
  outline: 2px solid hsl(var(--primary) / 28%);
  outline-offset: 1px;
}

.personnel-archive-card__header {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
}

.personnel-archive-card__avatar {
  display: inline-flex;
  width: 56px;
  height: 56px;
  flex: 0 0 56px;
  align-items: center;
  justify-content: center;
  color: hsl(var(--primary-foreground));
  border-radius: 14px;
  font-size: 28px;
  font-weight: 700;
}

.personnel-archive-card__summary {
  min-width: 0;
}

.personnel-archive-card__headline {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.personnel-archive-card__headline h3 {
  min-width: 0;
  margin: 0;
  overflow: hidden;
  color: hsl(var(--foreground));
  font-size: 18px;
  font-weight: 700;
  line-height: 24px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.personnel-archive-card__summary p {
  margin: 4px 0 0;
  overflow: hidden;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.personnel-archive-card__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.personnel-archive-card__chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.personnel-archive-card__chip::before {
  width: 6px;
  height: 6px;
  content: '';
  border-radius: 999px;
}

.personnel-archive-card__chip--success {
  color: hsl(var(--success));
  background: color-mix(in srgb, hsl(var(--success)) 12%, transparent);
}

.personnel-archive-card__chip--success::before {
  background: hsl(var(--success));
}

.personnel-archive-card__chip--warning {
  color: hsl(var(--warning));
  background: color-mix(in srgb, hsl(var(--warning)) 14%, transparent);
}

.personnel-archive-card__chip--warning::before {
  background: hsl(var(--warning));
}

.personnel-archive-card__chip--danger {
  color: hsl(var(--destructive));
  background: color-mix(in srgb, hsl(var(--destructive)) 12%, transparent);
}

.personnel-archive-card__chip--danger::before {
  background: hsl(var(--destructive));
}

.personnel-archive-card__chip--info {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 10%);
}

.personnel-archive-card__chip--info::before {
  background: hsl(var(--primary));
}

@media (max-width: 640px) {
  .personnel-archive-card__header {
    grid-template-columns: 48px minmax(0, 1fr);
    gap: 12px;
  }

  .personnel-archive-card__avatar {
    width: 48px;
    height: 48px;
    font-size: 24px;
  }

  .personnel-archive-card__headline {
    flex-wrap: wrap;
  }
}
</style>
