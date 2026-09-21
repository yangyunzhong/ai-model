<script setup lang="ts">
import { computed, h } from 'vue';

import { Steps } from 'antdv-next';

import { PlatformIcon } from '../icon';

defineOptions({
  inheritAttrs: false,
});

defineSlots<{
  iconRender?: (info: unknown) => unknown;
}>();

export type StepItem = {
  content?: string;
  description?: string;
  disabled?: boolean;
  icon?: string;
  status?: 'error' | 'finish' | 'process' | 'wait';
  subTitle?: string;
  title: string;
};

const props = withDefaults(
  defineProps<{
    current?: number;
    items?: StepItem[];
    orientation?: 'horizontal' | 'vertical';
    size?: 'default' | 'small';
    status?: 'error' | 'finish' | 'process' | 'wait';
    type?: 'default' | 'dot' | 'inline' | 'navigation' | 'panel';
    variant?: 'filled' | 'outlined';
  }>(),
  {
    current: 0,
    items: () => [],
    orientation: 'horizontal',
    size: 'default',
    status: 'process',
    type: 'default',
    variant: 'filled',
  },
);

const emit = defineEmits<{
  change: [current: number];
}>();

const resolvedItems = computed(() =>
  props.items.map((item) => ({
    ...item,
    description: item.description || item.content || '',
    icon: item.icon
      ? () => h(PlatformIcon, { icon: item.icon as string })
      : undefined,
  })),
);

function handleChange(current: number) {
  emit('change', current);
}
</script>

<template>
  <div class="platform-steps">
    <Steps
      v-bind="$attrs"
      :current="current"
      :items="resolvedItems"
      :orientation="orientation"
      :size="size"
      :status="status"
      :type="type"
      :variant="variant"
      @change="handleChange"
    >
      <template v-if="$slots['iconRender']" #iconRender="info">
        <slot name="iconRender" v-bind="info"></slot>
      </template>
    </Steps>
  </div>
</template>

<style scoped>
.platform-steps {
  min-width: 0;
}
</style>
