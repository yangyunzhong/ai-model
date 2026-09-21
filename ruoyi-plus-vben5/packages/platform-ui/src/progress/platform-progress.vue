<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    color?: string;
    label?: string;
    showValue?: boolean;
    value?: number;
  }>(),
  {
    color: 'hsl(var(--primary))',
    label: '',
    showValue: false,
    value: 0,
  },
);

function normalizePercent(value: number) {
  return Math.min(Math.max(value, 0), 100);
}
</script>

<template>
  <div
    class="platform-progress"
    :aria-label="label || '进度'"
    aria-valuemax="100"
    aria-valuemin="0"
    :aria-valuenow="normalizePercent(props.value)"
    role="progressbar"
  >
    <div class="platform-progress__track">
      <span
        class="platform-progress__bar"
        :style="{
          backgroundColor: color,
          width: `${normalizePercent(props.value)}%`,
        }"
      ></span>
    </div>
    <span v-if="showValue" class="platform-progress__value">
      {{ normalizePercent(props.value) }}%
    </span>
  </div>
</template>

<style scoped>
.platform-progress {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.platform-progress__track {
  flex: 1;
  height: 6px;
  overflow: hidden;
  background: hsl(var(--st-color-fill-disabled));
  border-radius: 999px;
}

.platform-progress__bar {
  display: block;
  height: 100%;
  min-width: 0;
  border-radius: inherit;
}

.platform-progress__value {
  flex: none;
  min-width: 36px;
  color: hsl(var(--st-color-text-tertiary));
  font-size: var(--st-font-size-sm);
  font-weight: 700;
  text-align: right;
}
</style>
