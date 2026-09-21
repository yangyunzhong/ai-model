<script setup lang="ts">
import { computed } from 'vue';

import { Empty } from 'antdv-next';

import { PlatformIcon } from '../icon';

defineOptions({
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    description?: string;
    icon?: string;
    image?: string;
    scene?: 'default' | 'no-result' | 'no-permission' | 'offline' | 'simple';
  }>(),
  {
    description: '',
    icon: '',
    image: '',
    scene: 'default',
  },
);

const sceneConfig: Record<
  string,
  { description: string; icon: string }
> = {
  default: { description: '暂无数据', icon: 'lucide:inbox' },
  'no-result': { description: '未找到匹配结果', icon: 'lucide:search' },
  'no-permission': { description: '暂无访问权限', icon: 'lucide:lock-keyhole' },
  offline: { description: '网络连接已断开', icon: 'lucide:circle-alert' },
  simple: { description: '暂无数据', icon: '' },
};

const resolvedDescription = computed(
  () => props.description || sceneConfig[props.scene]?.description || '暂无数据',
);

const resolvedIcon = computed(
  () => props.icon || sceneConfig[props.scene]?.icon || '',
);

const resolvedImage = computed(() => {
  if (props.image) {
    return props.image;
  }
  if (props.scene === 'simple') {
    return Empty.PRESENTED_IMAGE_SIMPLE;
  }
  return '';
});
</script>

<template>
  <div class="platform-empty" :class="`platform-empty--${scene}`">
    <Empty
      v-bind="$attrs"
      :description="resolvedDescription"
      :image="resolvedImage || undefined"
    >
      <template v-if="resolvedIcon && !resolvedImage" #image>
        <span class="platform-empty__icon">
          <PlatformIcon :icon="resolvedIcon" />
        </span>
      </template>
      <template v-if="$slots.description" #description>
        <slot name="description"></slot>
      </template>
      <slot></slot>
    </Empty>
  </div>
</template>

<style scoped>
.platform-empty {
  min-width: 0;
}

.platform-empty__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  font-size: 36px;
  color: hsl(var(--muted-foreground) / 60%);
  background: hsl(var(--muted) / 30%);
  border-radius: 50%;
}

.platform-empty__icon :deep(svg) {
  width: 1em;
  height: 1em;
}

.platform-empty :deep(.ant-empty-description) {
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
}

.platform-empty :deep(.ant-empty-footer) {
  margin-top: 12px;
}
</style>
