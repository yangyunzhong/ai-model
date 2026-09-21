<script setup lang="ts">
import { computed } from 'vue';

import { VbenIcon } from '@vben/icons';

import { Skeleton } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    color?: string;
    description?: string;
    icon?: string;
    iconBoxSize?: number | string;
    iconSize?: number | string;
    loading?: boolean;
    title: string;
    trendText?: string;
    trendType?: 'down' | 'neutral' | 'up';
    type?: 'danger' | 'info' | 'primary' | 'success' | 'warning';
    unit?: string;
    value: number | string;
  }>(),
  {
    color: undefined,
    description: '',
    icon: '',
    iconBoxSize: undefined,
    iconSize: undefined,
    loading: false,
    trendText: '',
    trendType: 'neutral',
    type: 'primary',
    unit: '',
  },
);

function toCssSize(value?: number | string) {
  if (typeof value === 'number') {
    return `${value}px`;
  }
  return value;
}

const cardStyle = computed(() => {
  const style: Record<string, string> = {};
  const iconBoxSize = toCssSize(props.iconBoxSize);
  const iconSize = toCssSize(props.iconSize);

  if (props.color) {
    style['--platform-stat-color'] = props.color;
  }
  if (iconBoxSize) {
    style['--platform-stat-icon-box-size'] = iconBoxSize;
  }
  if (iconSize) {
    style['--platform-stat-icon-size'] = iconSize;
  }

  return Object.keys(style).length > 0 ? style : undefined;
});
const trendIcon = computed(() => {
  if (props.trendType === 'up') {
    return '↑';
  }
  if (props.trendType === 'down') {
    return '↓';
  }
  return '';
});
</script>

<template>
  <article
    v-bind="$attrs"
    class="platform-stat-card"
    :class="`platform-stat-card--${type}`"
    :style="cardStyle"
  >
    <Skeleton v-if="loading" active :paragraph="{ rows: 2 }" />
    <template v-else>
      <div class="platform-stat-card__header">
        <span class="platform-stat-card__title">{{ title }}</span>
        <span v-if="icon" class="platform-stat-card__icon">
          <VbenIcon :icon="icon" />
        </span>
      </div>
      <div class="platform-stat-card__value">
        <strong>{{ value }}</strong>
        <span v-if="unit">{{ unit }}</span>
      </div>
      <p
        v-if="trendText"
        class="platform-stat-card__trend"
        :class="`platform-stat-card__trend--${trendType || 'neutral'}`"
      >
        <span v-if="trendIcon">{{ trendIcon }}</span>
        {{ trendText }}
      </p>
      <p v-else-if="description" class="platform-stat-card__description">
        {{ description }}
      </p>
    </template>
  </article>
</template>

<style scoped>
.platform-stat-card {
  --platform-stat-color: hsl(var(--primary));
  --platform-stat-icon-box-size: var(--st-stat-card-icon-box-size);
  --platform-stat-icon-size: var(--st-stat-card-icon-size);

  position: relative;
  min-height: 118px;
  padding: var(--st-module-content-padding);
  overflow: hidden;
  background: hsl(var(--st-color-stat-card-bg));
  border-radius: var(--st-radius-card);
  box-shadow: var(--st-shadow-stat-card);
  transition: transform 0.18s ease;
}

.platform-stat-card::before {
  position: absolute;
  inset: 0 0 auto;
  height: 3px;
  content: '';
  background: var(--platform-stat-color);
}

.platform-stat-card:hover {
  transform: translateY(-4px);
}

.platform-stat-card--success {
  --platform-stat-color: hsl(var(--success));
}

.platform-stat-card--warning {
  --platform-stat-color: hsl(var(--warning));
}

.platform-stat-card--danger {
  --platform-stat-color: hsl(var(--destructive));
}

.platform-stat-card--info {
  --platform-stat-color: hsl(var(--st-color-stat-card-info));
}

.platform-stat-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.platform-stat-card__title,
.platform-stat-card__description {
  color: hsl(var(--st-color-stat-card-text));
}

.platform-stat-card__title {
  color: hsl(var(--foreground));
}

.platform-stat-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: var(--platform-stat-icon-box-size);
  height: var(--platform-stat-icon-box-size);
  flex: 0 0 var(--platform-stat-icon-box-size);
  font-size: var(--platform-stat-icon-size);
  color: var(--platform-stat-color);
  background: color-mix(in srgb, var(--platform-stat-color) 12%, transparent);
  border-radius: var(--st-radius-control);
}

.platform-stat-card__icon :deep(svg) {
  width: 1em;
  height: 1em;
}

.platform-stat-card__value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 8px;
}

.platform-stat-card__value strong {
  font-family: var(--st-font-family-number);
  font-size: 32px;
  font-variant-numeric: lining-nums tabular-nums;
  font-weight: 700;
  line-height: 40px;
  color: var(--platform-stat-color);
}

.platform-stat-card__value span {
  font-size: 14px;
  color: hsl(var(--st-color-stat-card-text));
}

.platform-stat-card__trend,
.platform-stat-card__description {
  margin: 0;
  font-size: 13px;
}

.platform-stat-card__trend {
  font-weight: 700;
}

.platform-stat-card__trend--up {
  color: hsl(var(--success));
}

.platform-stat-card__trend--down {
  color: hsl(var(--destructive));
}

.platform-stat-card__trend--neutral {
  color: hsl(var(--st-color-stat-card-text));
}
</style>
