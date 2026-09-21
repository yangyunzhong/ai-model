<script setup lang="ts">
import { computed } from 'vue';

import { VbenIcon } from '@vben/icons';

const props = defineProps<{
  icon: string;
}>();

const isIconfont = computed(() => props.icon.startsWith('icon-'));
</script>

<template>
  <i
    v-if="isIconfont"
    class="platform-icon iconfont"
    :class="icon"
    aria-hidden="true"
  ></i>
  <VbenIcon v-else :icon="icon" />
</template>

<style scoped>
.platform-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 1em;
  line-height: 1;
}
</style>
