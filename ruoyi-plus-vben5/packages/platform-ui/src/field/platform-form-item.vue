<script setup lang="ts">
import { FormItem } from 'antdv-next';

defineOptions({
  inheritAttrs: false,
});
</script>

<template>
  <FormItem v-bind="$attrs" class="platform-form-item">
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </FormItem>
</template>
