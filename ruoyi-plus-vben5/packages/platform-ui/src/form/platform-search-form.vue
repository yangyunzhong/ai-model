<script setup lang="ts">
import { computed, useSlots } from 'vue';

import PlatformForm from './platform-form.vue';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    columns?: number | string;
  }>(),
  {
    columns: 'repeat(auto-fit, minmax(180px, 1fr))',
  },
);

const slots = useSlots();
const fieldSlotNames = computed(() =>
  Object.keys(slots).filter((name) => name !== 'actions'),
);
const formStyle = computed(() => ({
  '--platform-search-form-columns':
    typeof props.columns === 'number'
      ? `repeat(${props.columns}, minmax(0, 1fr))`
      : props.columns,
}));
</script>

<template>
  <PlatformForm
    v-bind="$attrs"
    class="platform-search-form"
    label-preset="inline-compact"
    :style="formStyle"
    variant="search"
  >
    <template v-for="name in fieldSlotNames" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
    <div v-if="$slots.actions" class="platform-search-form__actions">
      <slot name="actions"></slot>
    </div>
  </PlatformForm>
</template>

<style scoped>
.platform-search-form__actions {
  display: flex;
  grid-column: 1 / -1;
  align-items: center;
  justify-content: flex-end;
  gap: var(--st-search-form-action-gap);
  margin-top: var(--st-search-form-action-margin-top);
}
</style>
