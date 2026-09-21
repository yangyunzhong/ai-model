<script setup lang="ts">
import type { PlatformFileListItem, PlatformFileType } from './types';

import { computed } from 'vue';

import { VbenIcon } from '@vben/icons';

import { PlatformButton } from '../button';

const props = withDefaults(
  defineProps<{
    downloading?: boolean;
    item: PlatformFileListItem;
  }>(),
  {
    downloading: false,
  },
);

const emit = defineEmits<{
  download: [item: PlatformFileListItem];
}>();

const fileTypeMeta: Record<
  PlatformFileType,
  { className: string; icon: string; label: string }
> = {
  doc: {
    className: 'platform-file-item__icon--doc',
    icon: 'lucide:file-type-2',
    label: 'DOC',
  },
  other: {
    className: 'platform-file-item__icon--other',
    icon: 'lucide:file',
    label: 'FILE',
  },
  pdf: {
    className: 'platform-file-item__icon--pdf',
    icon: 'lucide:file-type',
    label: 'PDF',
  },
  report: {
    className: 'platform-file-item__icon--report',
    icon: 'lucide:file-bar-chart-2',
    label: 'RPT',
  },
  xls: {
    className: 'platform-file-item__icon--xls',
    icon: 'lucide:file-spreadsheet',
    label: 'XLS',
  },
};

const meta = computed(() => fileTypeMeta[props.item.type || 'other']);
const secondaryText = computed(() =>
  [props.item.projectName, props.item.size, props.item.date]
    .filter(Boolean)
    .join(' · '),
);
</script>

<template>
  <li class="platform-file-item">
    <span class="platform-file-item__icon-wrap">
      <span class="platform-file-item__icon" :class="meta.className">
        <VbenIcon :icon="meta.icon" />
        <span>{{ meta.label }}</span>
      </span>
    </span>

    <div class="platform-file-item__body">
      <strong>{{ item.name }}</strong>
      <div class="platform-file-item__meta">
        <p v-if="secondaryText">{{ secondaryText }}</p>
        <p v-else-if="item.description">{{ item.description }}</p>
        <span class="platform-file-item__date">{{ item.date }}</span>
      </div>
    </div>

    <PlatformButton
      :loading="downloading"
      :title="`下载 ${item.name}`"
      class="platform-file-item__download"
      scene="toolbar"
      @click="emit('download', item)"
    >
      <template #icon>
        <VbenIcon icon="lucide:download" />
      </template>
    </PlatformButton>
  </li>
</template>

<style scoped>
.platform-file-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-height: 168px;
  padding: 16px;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--st-color-table-outline));
  border-radius: var(--st-radius-control);
  transition:
    transform 0.18s ease;
}

.platform-file-item:hover {
  transform: translateY(-4px);
}

.platform-file-item__icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 88px;
  flex-direction: column;
  gap: 4px;
  flex: 0 0 72px;
  padding: 18px 10px 14px;
  overflow: hidden;
  border-radius: 8px;
  clip-path: polygon(0 0, 76% 0, 100% 22%, 100% 100%, 0 100%);
  box-shadow: 0 10px 24px rgb(15 23 42 / 12%);
}

.platform-file-item__icon-wrap {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 18px;
}

.platform-file-item__icon :deep(svg) {
  width: 26px;
  height: 26px;
}

.platform-file-item__icon span {
  font-size: 15px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: 0.02em;
  text-align: center;
}

.platform-file-item__icon::after {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 24px;
  height: 24px;
  content: '';
  background: linear-gradient(
    135deg,
    rgb(255 255 255 / 68%) 0%,
    rgb(255 255 255 / 44%) 56%,
    rgb(255 255 255 / 20%) 100%
  );
  border-radius: 4px;
  filter: brightness(1.04);
  transform: translateZ(0);
}

.platform-file-item__icon--pdf {
  color: #fff;
  background: linear-gradient(180deg, #ff6b75 0%, #ea4453 100%);
}

.platform-file-item__icon--doc {
  color: #fff;
  background: linear-gradient(180deg, #42b6f5 0%, #2798e6 100%);
}

.platform-file-item__icon--xls {
  color: #fff;
  background: linear-gradient(180deg, #28d878 0%, #12bc61 100%);
}

.platform-file-item__icon--report {
  color: #fff;
  background: linear-gradient(180deg, #8a7dff 0%, #6a58ef 100%);
}

.platform-file-item__icon--other {
  color: #fff;
  background: linear-gradient(
    180deg,
    hsl(var(--st-color-stat-card-info)) 0%,
    hsl(var(--st-color-brand)) 100%
  );
}

.platform-file-item__body {
  min-width: 0;
  margin-top: 16px;
  text-align: center;
}

.platform-file-item__body strong {
  display: block;
  overflow: hidden;
  color: hsl(var(--foreground));
  font-size: var(--st-font-size-base);
  font-weight: 700;
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-file-item__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
}

.platform-file-item__meta p {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.platform-file-item__date {
  flex: 0 0 auto;
  color: hsl(var(--muted-foreground));
  font-size: var(--st-font-size-sm);
  line-height: 20px;
  white-space: nowrap;
}

.platform-file-item__download {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 32px;
  min-width: 32px;
  height: 32px;
  padding: 0;
  padding-inline: 0;
  border: none;
  border-radius: 999px;
  background: transparent;
  box-shadow: none;
}

.platform-file-item__download:hover {
  background: hsl(var(--st-color-fill-selected));
  border-color: transparent;
  box-shadow: none;
}

.platform-file-item__download :deep(svg) {
  width: 16px;
  height: 16px;
}

@media (max-width: 640px) {
  .platform-file-item {
    min-height: 156px;
  }

  .platform-file-item__download {
    top: 14px;
    right: 14px;
  }
}
</style>
