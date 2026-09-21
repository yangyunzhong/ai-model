<script setup lang="ts">
import PlatformForm from './platform-form.vue';

defineOptions({
  inheritAttrs: false,
});
</script>

<template>
  <PlatformForm v-bind="$attrs" variant="edit">
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </PlatformForm>
</template>
