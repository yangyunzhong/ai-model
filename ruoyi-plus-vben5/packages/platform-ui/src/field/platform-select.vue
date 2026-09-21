<script setup lang="ts">
import type { SelectProps } from 'antdv-next';

import { computed } from 'vue';

import { Select } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});

const props = defineProps<{
  minChars?: number;
  options?: SelectProps['options'];
  width?: number | string;
}>();
const triggerChars = computed(() => {
  const maxLabelLength = getMaxOptionLabelLength(props.options);
  if (maxLabelLength > 0) {
    return maxLabelLength > 4 ? 5 : maxLabelLength;
  }
  return props.minChars;
});
const selectStyle = computed(() => {
  if (props.width) {
    const width =
      typeof props.width === 'number' ? `${props.width}px` : props.width;
    return {
      '--platform-select-width': width,
    };
  }
  if (triggerChars.value) {
    return {
      '--platform-select-width': `calc(${triggerChars.value}em + 72px)`,
    };
  }
  return undefined;
});

function getMaxOptionLabelLength(options?: SelectProps['options']): number {
  if (!options?.length) {
    return 0;
  }

  let maxLength = 0;

  for (const option of options) {
    const labelLength = getOptionLabel(option);
    const childrenLength = getMaxOptionLabelLength(
      'options' in option ? option.options : undefined,
    );
    maxLength = Math.max(maxLength, labelLength, childrenLength);
  }

  return maxLength;
}

function getOptionLabel(option: NonNullable<SelectProps['options']>[number]) {
  const label = 'label' in option ? option.label : undefined;
  if (typeof label === 'number' || typeof label === 'string') {
    return [...String(label)].length;
  }
  if (typeof option.value === 'number' || typeof option.value === 'string') {
    return [...String(option.value)].length;
  }
  return 0;
}
</script>

<template>
  <Select
    v-bind="$attrs"
    class="platform-select"
    :classes="{ popup: { root: 'platform-select-dropdown' } }"
    :options="options"
    :popup-match-select-width="false"
    :style="selectStyle"
  >
    <template #suffixIcon>
      <slot name="suffixIcon">
        <i
          class="platform-select__suffix-icon iconfont icon-jiantouxia"
          aria-hidden="true"
        ></i>
      </slot>
    </template>
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Select>
</template>

<style scoped>
.platform-select {
  min-width: var(--platform-select-width, 0);
  width: var(--platform-select-width, 100%);
}

.platform-select__suffix-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 12px;
  height: 12px;
  color: currentColor;
  font-size: 12px;
  line-height: 1;
}

.platform-select :deep(.ant-select-selection-item),
.platform-select :deep(.ant-select-selection-placeholder) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:global(.platform-select-dropdown .ant-select-item-option-content) {
  white-space: nowrap;
}
</style>
