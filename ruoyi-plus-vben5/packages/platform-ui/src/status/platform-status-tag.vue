<script setup lang="ts">
import { computed } from 'vue';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    label?: string;
    status?: 'default' | 'error' | 'processing' | 'success' | 'warning';
    variant?: 'dot' | 'tag';
  }>(),
  {
    label: '',
    status: 'default',
    variant: 'tag',
  },
);

const statusMeta = computed(() => {
  const colorMap = {
    default: {
      dot: 'hsl(var(--muted-foreground))',
      fill: 'hsl(var(--muted-foreground))',
      wash: 'hsl(var(--muted) / 50%)',
    },
    error: {
      dot: 'hsl(var(--destructive))',
      fill: 'hsl(var(--destructive))',
      wash: 'hsl(var(--destructive) / 14%)',
    },
    processing: {
      dot: 'hsl(var(--st-color-stat-card-info))',
      fill: 'hsl(var(--st-color-stat-card-info))',
      wash: 'hsl(var(--st-color-stat-card-info) / 14%)',
    },
    success: {
      dot: 'hsl(var(--primary))',
      fill: 'hsl(var(--primary))',
      wash: 'hsl(var(--primary) / 14%)',
    },
    warning: {
      dot: 'hsl(var(--warning))',
      fill: 'hsl(var(--warning))',
      wash: 'hsl(var(--warning) / 16%)',
    },
  } as const;
  return colorMap[props.status];
});

const statusStyle = computed(() => ({
  '--platform-status-dot-color': statusMeta.value.dot,
  '--platform-status-dot-wash': statusMeta.value.wash,
  '--platform-status-tag-fill': statusMeta.value.fill,
}));
</script>

<template>
  <span
    v-if="variant === 'dot'"
    v-bind="$attrs"
    class="platform-status-tag platform-status-tag--dot"
    :style="statusStyle"
  >
    <span class="platform-status-tag__dot"></span>
    <span class="platform-status-tag__label">
      <slot>{{ label }}</slot>
    </span>
  </span>
  <span
    v-else
    v-bind="$attrs"
    class="platform-status-tag platform-status-tag--tag"
    :style="statusStyle"
  >
    <slot>{{ label }}</slot>
  </span>
</template>

<style scoped>
.platform-status-tag {
  display: inline-flex;
  align-items: center;
}

.platform-status-tag--tag {
  min-width: 56px;
  min-height: 24px;
  padding-inline: 8px;
  justify-content: center;
  line-height: 22px;
  text-align: center;
  color: hsl(var(--primary-foreground));
  background: var(--platform-status-tag-fill);
  border: 1px solid transparent;
  border-radius: var(--st-radius-control);
}

.platform-status-tag--dot {
  gap: 10px;
  min-width: 0;
  color: hsl(var(--foreground));
  line-height: 24px;
}

.platform-status-tag__dot {
  position: relative;
  width: 10px;
  height: 10px;
  flex: 0 0 10px;
  background: var(--platform-status-dot-color);
  border-radius: 999px;
}

.platform-status-tag__dot::after {
  position: absolute;
  inset: -4px;
  content: '';
  background: var(--platform-status-dot-wash);
  border-radius: inherit;
}

.platform-status-tag__label {
  position: relative;
  z-index: 1;
  font-size: var(--st-font-size-base);
  font-weight: 400;
  white-space: nowrap;
}
</style>
