<script setup lang="ts">
import { computed } from 'vue';

import { PlatformIcon } from '../icon';

const props = withDefaults(
  defineProps<{
    description?: string;
    divider?: boolean;
    icon?: string;
    iconGap?: number | string;
    iconHeight?: number | string;
    iconSrc?: string;
    iconWidth?: number | string;
    imageAlt?: string;
    padding?: string;
    title: string;
    titleSize?: number | string;
    titleWeight?: number | string;
  }>(),
  {
    description: '',
    divider: false,
    icon: '',
    iconGap: 8,
    iconHeight: '',
    iconSrc: '',
    iconWidth: '',
    imageAlt: '',
    padding: '0',
    titleSize: '',
    titleWeight: '',
  },
);

function toUnit(value: number | string | undefined) {
  if (value === '' || value === undefined) {
    return undefined;
  }
  return typeof value === 'number' ? `${value}px` : value;
}

const titleStyle = computed(() => ({
  '--platform-section-title-gap': toUnit(props.iconGap),
  '--platform-section-title-padding': props.padding,
  '--platform-section-title-size': toUnit(props.titleSize),
  '--platform-section-title-weight': props.titleWeight,
  '--platform-section-title-icon-height': toUnit(props.iconHeight),
  '--platform-section-title-icon-width': toUnit(props.iconWidth),
}));
</script>

<template>
  <header
    class="platform-section-title"
    :class="{ 'platform-section-title--divider': divider }"
    :style="titleStyle"
  >
    <div class="platform-section-title__start">
      <div class="platform-section-title__meta">
        <div class="platform-section-title__heading">
          <img
            v-if="iconSrc"
            class="platform-section-title__image-icon"
            :alt="imageAlt || title"
            :src="iconSrc"
          />
          <PlatformIcon
            v-else-if="icon"
            class="platform-section-title__icon"
            :icon="icon"
          />
          <h2 class="platform-section-title__text">{{ title }}</h2>
        </div>
        <p v-if="description" class="platform-section-title__description">
          {{ description }}
        </p>
      </div>

      <div v-if="$slots['title-extra']" class="platform-section-title__title-extra">
        <slot name="title-extra"></slot>
      </div>
    </div>

    <div v-if="$slots.center" class="platform-section-title__center">
      <slot name="center"></slot>
    </div>

    <div v-if="$slots.extra" class="platform-section-title__extra">
      <slot name="extra"></slot>
    </div>
  </header>
</template>

<style scoped>
.platform-section-title {
  display: grid;
  align-items: center;
  grid-template-columns: minmax(0, auto) minmax(0, 1fr) auto;
  gap: 16px;
  min-width: 0;
  padding: var(--platform-section-title-padding);
}

.platform-section-title--divider {
  border-bottom: 1px solid hsl(var(--st-color-border-subtle));
}

.platform-section-title__start {
  grid-column: 1;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.platform-section-title__meta {
  min-width: 0;
}

.platform-section-title__heading {
  display: inline-flex;
  align-items: center;
  gap: var(--platform-section-title-gap, 8px);
  min-width: 0;
}

.platform-section-title__icon,
.platform-section-title__image-icon {
  flex: 0 0 auto;
  width: var(--platform-section-title-icon-width, auto);
  height: var(--platform-section-title-icon-height, auto);
}

.platform-section-title__image-icon {
  object-fit: contain;
}

.platform-section-title__text {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: var(--platform-section-title-size, var(--st-font-size-title));
  font-weight: var(--platform-section-title-weight, 700);
  line-height: var(--st-line-height-lg);
}

.platform-section-title__description {
  margin: 4px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 20px;
}

.platform-section-title__title-extra,
.platform-section-title__center,
.platform-section-title__extra {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.platform-section-title__title-extra,
.platform-section-title__extra {
  flex: 0 0 auto;
  flex-wrap: wrap;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 22px;
}

.platform-section-title__center {
  grid-column: 2;
  justify-content: center;
}

.platform-section-title__extra {
  grid-column: 3;
  justify-content: flex-end;
}

@media (max-width: 640px) {
  .platform-section-title {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .platform-section-title__start,
  .platform-section-title__center,
  .platform-section-title__extra {
    width: 100%;
  }

  .platform-section-title__start {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .platform-section-title__center,
  .platform-section-title__extra {
    justify-content: flex-start;
  }
}
</style>
