<script setup lang="ts">
import { DatePicker } from 'antdv-next';
defineOptions({
  inheritAttrs: false,
});
const RangePicker = DatePicker.RangePicker;
</script>

<template>
  <RangePicker v-bind="$attrs" class="platform-range-picker">
    <template #suffixIcon>
      <slot name="suffixIcon">
        <i
          class="platform-range-picker__suffix-icon iconfont icon-jiantouxia"
          aria-hidden="true"
        ></i>
      </slot>
    </template>
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </RangePicker>
</template>

<style scoped>
.platform-range-picker {
  width: 100%;
}

.platform-range-picker__suffix-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 12px;
  height: 12px;
  color: currentColor;
  font-size: 12px;
  line-height: 1;
}
</style>
