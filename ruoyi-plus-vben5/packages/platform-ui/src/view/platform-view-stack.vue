<template>
  <div class="platform-view-stack">
    <slot></slot>
  </div>
</template>

<style scoped>
.platform-view-stack {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}
</style>
