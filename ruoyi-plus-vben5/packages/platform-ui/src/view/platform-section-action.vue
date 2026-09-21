<script setup lang="ts">
import { computed, useSlots } from 'vue';

import { PlatformButton } from '../button';
import { PlatformIcon } from '../icon';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    icon?: string;
    kind?: 'custom' | 'more' | 'refresh';
    label?: string;
  }>(),
  {
    icon: '',
    kind: 'custom',
    label: '',
  },
);

const slots = useSlots();

const actionMeta = {
  more: {
    icon: 'lucide:chevron-right',
    label: '更多',
  },
  refresh: {
    icon: 'lucide:rotate-cw',
    label: '刷新',
  },
} as const;

const resolvedIcon = computed(() => {
  if (props.icon) {
    return props.icon;
  }

  return props.kind === 'custom' ? '' : actionMeta[props.kind].icon;
});

const resolvedLabel = computed(() => {
  if (props.label) {
    return props.label;
  }

  return props.kind === 'custom' ? '' : actionMeta[props.kind].label;
});

const hasDefaultSlot = computed(() => Boolean(slots.default));
</script>

<template>
  <PlatformButton
    v-bind="$attrs"
    class="platform-section-action"
    size="small"
    type="text"
  >
    <template v-if="resolvedIcon" #icon>
      <PlatformIcon :icon="resolvedIcon" />
    </template>

    <slot v-if="hasDefaultSlot"></slot>
    <template v-else>{{ resolvedLabel }}</template>
  </PlatformButton>
</template>

<style scoped>
.platform-section-action {
  min-width: auto;
  padding-inline: 4px;
  cursor: pointer;
  color: hsl(var(--foreground));
  background: transparent;
  border: 0;
  box-shadow: none;
  flex-direction: row-reverse;
  gap: 4px;
  transition: color 0.16s ease;
}

.platform-section-action:hover,
.platform-section-action:focus-visible {
  color: hsl(var(--primary));
  background: transparent;
  border-color: transparent;
  box-shadow: none;
}

.platform-section-action :deep(.platform-icon),
.platform-section-action :deep(svg) {
  color: currentColor;
}
</style>
