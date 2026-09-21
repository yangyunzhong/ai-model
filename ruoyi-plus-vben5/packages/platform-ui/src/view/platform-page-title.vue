<script setup lang="ts">
withDefaults(
  defineProps<{
    description?: string;
    title?: string;
  }>(),
  {
    description: '',
    title: '',
  },
);
</script>

<template>
  <div class="platform-page-title">
    <h1 v-if="title" class="platform-page-title__text">{{ title }}</h1>
    <p v-if="description" class="platform-page-title__description">
      {{ description }}
    </p>
  </div>
</template>

<style scoped>
.platform-page-title {
  min-width: 0;
}

.platform-page-title__text {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 22px;
  font-weight: 700;
  line-height: 32px;
}

.platform-page-title__description {
  margin: 2px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 20px;
}
</style>
