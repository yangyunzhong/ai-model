<script setup lang="ts">
import { DatePicker } from 'antdv-next';
defineOptions({
  inheritAttrs: false,
});
</script>

<template>
  <DatePicker v-bind="$attrs" class="platform-date-picker">
    <template #suffixIcon>
      <slot name="suffixIcon">
        <i
          class="platform-date-picker__suffix-icon iconfont icon-jiantouxia"
          aria-hidden="true"
        ></i>
      </slot>
    </template>
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </DatePicker>
</template>

<style scoped>
.platform-date-picker {
  width: 100%;
}

.platform-date-picker__suffix-icon {
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
