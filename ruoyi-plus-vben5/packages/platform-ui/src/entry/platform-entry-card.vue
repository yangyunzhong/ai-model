<script setup lang="ts">
import { computed } from 'vue';

import { PlatformIcon } from '../icon';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    cardBackground?: string;
    icon: string;
    iconBackground?: string;
    iconColor?: string;
    label: string;
    variant?: 'app' | 'shortcut';
  }>(),
  {
    cardBackground: '',
    iconBackground: '',
    iconColor: '',
    variant: 'shortcut',
  },
);

const cardStyle = computed(() => {
  const style: Record<string, string> = {};

  if (props.cardBackground) {
    style['--platform-entry-card-bg'] = props.cardBackground;
  }

  if (props.iconBackground) {
    style['--platform-entry-card-icon-bg'] = props.iconBackground;
  }

  if (props.iconColor) {
    style['--platform-entry-card-icon-color'] = props.iconColor;
  }

  return Object.keys(style).length > 0 ? style : undefined;
});
</script>

<template>
  <button
    v-bind="$attrs"
    class="platform-entry-card"
    :class="`platform-entry-card--${variant}`"
    :style="cardStyle"
    type="button"
  >
    <span class="platform-entry-card__icon">
      <PlatformIcon :icon="icon" />
    </span>
    <span class="platform-entry-card__label">{{ label }}</span>
  </button>
</template>

<style scoped>
.platform-entry-card {
  width: 100%;
  min-width: 0;
  border: 0;
  cursor: pointer;
  transition:
    transform 0.16s ease,
    background 0.16s ease,
    color 0.16s ease,
    box-shadow 0.16s ease;
}

.platform-entry-card:hover {
  transform: translateY(-1px);
}

.platform-entry-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.platform-entry-card__label {
  display: block;
  overflow: hidden;
  color: hsl(var(--foreground));
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-entry-card--shortcut {
  --platform-entry-card-icon-bg: hsl(var(--primary));
  --platform-entry-card-icon-color: #fff;

  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 0;
  padding: 8px 0 12px;
  background: transparent;
}

.platform-entry-card--shortcut .platform-entry-card__icon {
  width: 72px;
  height: 72px;
  color: var(--platform-entry-card-icon-color);
  background: var(--platform-entry-card-icon-bg);
  border-radius: 12px;
  box-shadow: var(--st-shadow-card);
}

.platform-entry-card--shortcut .platform-entry-card__icon :deep(svg) {
  width: 24px;
  height: 24px;
}

.platform-entry-card--shortcut .platform-entry-card__label {
  width: 100%;
  font-size: 14px;
  line-height: 32px;
  text-align: center;
}

.platform-entry-card--app {
  --platform-entry-card-bg: hsl(var(--st-color-border-subtle));
  --platform-entry-card-icon-color: hsl(var(--primary));

  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  min-height: 72px;
  padding: 0 20px;
  overflow: hidden;
  text-align: left;
  background: var(--platform-entry-card-bg);
  border-radius: 10px;
}

.platform-entry-card--app:hover {
  background: hsl(var(--st-color-page-bg));
  box-shadow: none;
}

.platform-entry-card--app .platform-entry-card__icon {
  width: 32px;
  height: 32px;
  color: var(--platform-entry-card-icon-color);
  flex: 0 0 auto;
}

.platform-entry-card--app .platform-entry-card__icon :deep(svg) {
  width: 28px;
  height: 28px;
}

.platform-entry-card--app .platform-entry-card__label {
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  text-align: left;
}

.platform-entry-card--app:hover .platform-entry-card__label {
  color: hsl(var(--primary));
}
</style>
