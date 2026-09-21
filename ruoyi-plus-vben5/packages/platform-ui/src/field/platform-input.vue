<script setup lang="ts">
import { Input } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});
</script>

<template>
  <Input v-bind="$attrs" class="platform-input">
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </Input>
</template>

<style scoped>
.platform-input {
  width: 100%;
}
</style>
