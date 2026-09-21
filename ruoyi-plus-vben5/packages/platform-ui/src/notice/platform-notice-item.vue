<script setup lang="ts">
import type { PlatformNoticeListItem } from './types';

import { computed } from 'vue';

import { VbenIcon } from '@vben/icons';

import { PlatformButton } from '../button';

const props = withDefaults(
  defineProps<{
    item: PlatformNoticeListItem;
    variant?: 'default' | 'workbench';
  }>(),
  {
    variant: 'default',
  },
);

const emit = defineEmits<{
  action: [item: PlatformNoticeListItem];
}>();

const status = computed(() => props.item.status || 'default');
</script>

<template>
  <li
    class="platform-notice-item"
    :class="[
      `platform-notice-item--${status}`,
      `platform-notice-item--${props.variant}`,
    ]"
  >
    <div class="platform-notice-item__content">
      <span v-if="item.tag" class="platform-notice-item__tag">
        <span class="platform-notice-item__dot"></span>
        {{ item.tag }}
      </span>
      <strong>{{ item.title }}</strong>
      <span v-if="item.meta" class="platform-notice-item__meta">
        {{ item.meta }}
      </span>
      <p v-if="item.description">{{ item.description }}</p>
    </div>

    <PlatformButton
      v-if="item.actionText"
      :disabled="item.disabled"
      class="platform-notice-item__action"
      scene="default"
      size="small"
      @click="emit('action', item)"
    >
      <template #icon>
        <VbenIcon icon="lucide:bell-ring" />
      </template>
      {{ item.actionText }}
    </PlatformButton>
  </li>
</template>

<style scoped>
.platform-notice-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16px;
  align-items: center;
  min-height: 56px;
  padding: 16px 0;
  border-bottom: 1px solid hsl(var(--st-color-border-subtle));
}

.platform-notice-item:first-child {
  padding-top: 0;
}

.platform-notice-item:last-child {
  border-bottom: 0;
}

.platform-notice-item--workbench {
  min-height: 0;
  padding-inline: 8px;
}

.platform-notice-item__content {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 10px;
}

.platform-notice-item__content strong {
  flex: 0 0 auto;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  font-weight: 700;
  line-height: 22px;
}

.platform-notice-item__content p,
.platform-notice-item__meta {
  min-width: 0;
  overflow: hidden;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-notice-item__content p {
  margin: 0;
}

.platform-notice-item--workbench .platform-notice-item__content {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  grid-template-areas:
    'tag title meta'
    'desc desc desc';
  gap: 6px 12px;
}

.platform-notice-item--workbench .platform-notice-item__tag {
  grid-area: tag;
}

.platform-notice-item--workbench .platform-notice-item__content strong {
  grid-area: title;
  min-width: 0;
}

.platform-notice-item--workbench .platform-notice-item__meta {
  grid-area: meta;
  justify-self: end;
  white-space: nowrap;
}

.platform-notice-item--workbench .platform-notice-item__content p {
  grid-area: desc;
}

.platform-notice-item--workbench .platform-notice-item__content strong,
.platform-notice-item--workbench .platform-notice-item__content p {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-notice-item__tag {
  display: inline-flex;
  height: 24px;
  flex: 0 0 auto;
  align-items: center;
  gap: 6px;
  padding-inline: 10px;
  color: hsl(var(--warning));
  font-size: var(--st-font-size-sm);
  font-weight: 700;
  line-height: 24px;
  background: hsl(var(--warning) / 10%);
  border-radius: var(--st-radius-control);
}

.platform-notice-item__dot {
  width: 6px;
  height: 6px;
  background: currentColor;
  border-radius: 999px;
}

.platform-notice-item--danger .platform-notice-item__tag {
  color: hsl(var(--st-color-danger));
  background: hsl(var(--st-color-danger) / 10%);
}

.platform-notice-item--success .platform-notice-item__tag {
  color: hsl(var(--success));
  background: hsl(var(--success) / 10%);
}

.platform-notice-item--info .platform-notice-item__tag {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 10%);
}

.platform-notice-item__action {
  min-width: 92px;
  color: hsl(var(--primary));
  background: hsl(var(--background));
  border-color: hsl(var(--primary));
}

.platform-notice-item__action:not(:disabled):not(.ant-btn-disabled):hover,
.platform-notice-item__action:not(:disabled):not(.ant-btn-disabled):focus-visible {
  color: hsl(var(--primary-foreground));
  background: hsl(var(--primary));
  border-color: hsl(var(--primary));
}

.platform-notice-item__action :deep(svg) {
  width: 14px;
  height: 14px;
}

@media (max-width: 640px) {
  .platform-notice-item {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }

  .platform-notice-item__content {
    flex-wrap: wrap;
  }

  .platform-notice-item--workbench .platform-notice-item__content {
    grid-template-columns: 1fr;
    grid-template-areas:
      'tag'
      'title'
      'meta'
      'desc';
  }

  .platform-notice-item--workbench .platform-notice-item__meta {
    justify-self: start;
  }

  .platform-notice-item__action {
    justify-self: start;
  }
}
</style>
