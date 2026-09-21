<script setup lang="ts">
import { ChevronsLeft, ChevronsRight } from '@vben-core/icons';

const collapsed = defineModel<boolean>('collapsed');

function handleCollapsed() {
  collapsed.value = !collapsed.value;
}
</script>

<template>
  <div
    :class="collapsed ? 'left-2' : 'right-3'"
    class="vben-sidebar-collapse-button absolute bottom-2 z-10 flex-center cursor-pointer"
    @click.stop="handleCollapsed"
  >
    <ChevronsRight v-if="collapsed" class="size-[18px]" />
    <ChevronsLeft v-else class="size-[18px]" />
  </div>
</template>

<style scoped>
.vben-sidebar-collapse-button {
  width: 32px;
  height: 32px;
  color: hsl(var(--st-color-text-tertiary));
  background: hsl(var(--st-color-border-subtle));
  border-radius: var(--st-radius-control);
  transition:
    color 0.16s ease,
    background-color 0.16s ease;
}

.vben-sidebar-collapse-button:hover {
  color: hsl(var(--st-color-text-secondary));
  background: hsl(var(--st-color-border-control));
}
</style>
