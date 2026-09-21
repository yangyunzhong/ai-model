<script setup lang="ts">
import { computed } from 'vue';

import { VbenAvatar } from '../avatar';

interface Props {
  /**
   * @zh_CN 是否收起文本
   */
  collapsed?: boolean;
  /**
   * @zh_CN Logo 图片适应方式
   */
  fit?: 'contain' | 'cover' | 'fill' | 'none' | 'scale-down';
  /**
   * @zh_CN Logo 跳转地址
   */
  href?: string;
  /**
   * @zh_CN Logo 图片大小
   */
  logoSize?: number;
  /**
   * @zh_CN Logo 图标
   */
  src?: string;
  /**
   * @zh_CN 暗色主题 Logo 图标 (可选，若不设置则使用 src)
   */
  srcDark?: string;
  /**
   * @zh_CN Logo 文本
   */
  text: string;
  /**
   * @zh_CN Logo 主题
   */
  theme?: string;
}

defineOptions({
  name: 'VbenLogo',
});

const props = withDefaults(defineProps<Props>(), {
  collapsed: false,
  href: 'javascript:void 0',
  logoSize: 32,
  src: '',
  srcDark: '',
  theme: 'light',
  fit: 'cover',
});

/**
 * @zh_CN 根据主题选择合适的 logo 图标
 */
const logoSrc = computed(() => {
  // 如果是暗色主题且提供了 srcDark，则使用暗色主题的 logo
  if (props.theme === 'dark' && props.srcDark) {
    return props.srcDark;
  }
  // 否则使用默认的 src
  return props.src;
});

const iconfontClassName = computed(() => {
  if (!logoSrc.value?.startsWith('iconfont:')) {
    return '';
  }
  return logoSrc.value.replace('iconfont:', '').trim();
});
</script>

<template>
  <div :class="theme" class="vben-logo flex h-full items-center text-lg">
    <a
      :class="$attrs.class"
      :href="href"
      class="vben-logo__link flex h-full items-center gap-2 overflow-hidden text-lg leading-normal transition-all duration-500"
    >
      <i
        v-if="iconfontClassName"
        :aria-label="text"
        class="vben-logo__iconfont iconfont"
        :class="iconfontClassName"
      ></i>
      <VbenAvatar
        v-else-if="logoSrc"
        :alt="text"
        :src="logoSrc"
        :size="logoSize"
        :fit="fit"
        class="relative rounded-none bg-transparent"
      />
      <template v-if="!collapsed">
        <slot name="text">
          <span
            class="vben-logo__text text-foreground truncate font-semibold text-nowrap"
          >
            {{ text }}
          </span>
        </slot>
      </template>
    </a>
  </div>
</template>

<style scoped>
.vben-logo__link {
  max-width: 100%;
  padding-inline: 22px 18px;
}

.vben-logo__text {
  color: hsl(var(--header-foreground, var(--foreground)));
  font-size: 24px;
  line-height: 24px;
  letter-spacing: 0;
}

.vben-logo__iconfont {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  color: hsl(var(--header-foreground, var(--foreground)));
  font-size: v-bind('`${props.logoSize}px`');
  line-height: 1;
  width: v-bind('`${props.logoSize}px`');
  height: v-bind('`${props.logoSize}px`');
}
</style>
