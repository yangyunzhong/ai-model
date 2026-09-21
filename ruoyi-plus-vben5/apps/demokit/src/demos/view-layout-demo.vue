<script setup lang="ts">
import type { PlatformStatusBoardColumn, PlatformViewOption } from '@st/platform-ui';

import { ref } from 'vue';

import {
  PlatformDescriptions,
  PlatformPageBreadcrumb,
  PlatformPageTabs,
  PlatformSection,
  PlatformSectionTitle,
  PlatformStatusBoard,
  PlatformViewStack,
  PlatformViewSwitch,
} from '@st/platform-ui';

const currentView = ref('board');

const breadcrumbItems = [
  { icon: 'lucide:layout-grid', path: '/', title: '平台组件' },
  { icon: 'lucide:files', title: '布局与标题' },
];

type PageTab = NonNullable<
  InstanceType<typeof PlatformPageTabs>['$props']['tabs']
>[number];

function createSampleTab(options: {
  affixTab?: boolean;
  icon: string;
  path: string;
  title: string;
}): PageTab {
  return {
    fullPath: options.path,
    hash: '',
    key: options.path,
    matched: [],
    meta: {
      affixTab: options.affixTab,
      icon: options.icon,
      title: options.title,
    },
    name: options.title,
    params: {},
    path: options.path,
    query: {},
    redirectedFrom: undefined,
  };
}

const sampleTabs: NonNullable<
  InstanceType<typeof PlatformPageTabs>['$props']['tabs']
> = [
  createSampleTab({
    affixTab: true,
    icon: 'lucide:monitor',
    path: '/analytics',
    title: '分析页',
  }),
  createSampleTab({
    icon: 'lucide:users',
    path: '/system/user',
    title: '用户管理',
  }),
  createSampleTab({
    icon: 'lucide:shield-check',
    path: '/system/role',
    title: '角色管理',
  }),
];

type DescriptionItem = {
  children: string;
  key: string;
  label: string;
};

const descriptionItems: DescriptionItem[] = [
  { key: 'name', label: '项目名称', children: '华东治理专项' },
  { key: 'owner', label: '项目负责人', children: '王敏' },
  { key: 'period', label: '执行周期', children: '2026-03 至 2026-12' },
  { key: 'status', label: '当前状态', children: '进行中' },
];

const viewOptions: PlatformViewOption[] = [
  { icon: 'lucide:layout-dashboard', label: '看板', value: 'board' },
  { icon: 'lucide:list', label: '列表', value: 'list' },
];

const statusColumns: PlatformStatusBoardColumn[] = [
  {
    color: '#1677ff',
    items: [
      { id: 'todo-1', meta: '责任人: 王敏', progress: 36, title: '补齐验收资料' },
      { id: 'todo-2', meta: '责任人: 李蕾', progress: 52, title: '复核预算偏差' },
    ],
    key: 'todo',
    label: '待处理',
  },
  {
    color: '#faad14',
    items: [
      { id: 'doing-1', meta: '责任人: 陈涛', progress: 68, title: '质量缺陷复盘' },
    ],
    key: 'doing',
    label: '处理中',
  },
  {
    color: '#52c41a',
    items: [
      { id: 'done-1', meta: '责任人: 张衡', progress: 100, title: '合同节点同步' },
    ],
    key: 'done',
    label: '已完成',
  },
];

const props = defineProps<{
  demoId?: string;
}>();
</script>

<template>
  <PlatformViewStack class="demo-flow">
    <div v-if="props.demoId === 'platform-view-stack'" class="demo-flow">
      <PlatformSection title="概览信息" description="第一段内容区示例">
        <p class="demo-muted">ViewStack 负责页面纵向堆叠节奏，不负责替代每个区块内部结构。</p>
      </PlatformSection>
      <PlatformSection title="附件与流程" description="第二段内容区示例">
        <p class="demo-muted">多个 PlatformSection 按统一间距向下排列，就是它最核心的价值。</p>
      </PlatformSection>
    </div>

    <div
      v-if="
        props.demoId === 'platform-page-breadcrumb' ||
        props.demoId === 'platform-page-tabs' ||
        props.demoId === 'platform-view-switch'
      "
      class="demo-surface demo-flow"
    >
      <PlatformPageBreadcrumb
        v-if="props.demoId === 'platform-page-breadcrumb'"
        :items="breadcrumbItems"
      />
      <PlatformPageTabs
        v-if="props.demoId === 'platform-page-tabs'"
        active="/system/user"
        :tabs="sampleTabs"
      />
      <div v-if="props.demoId === 'platform-view-switch'" class="demo-row">
        <PlatformViewSwitch
          v-model="currentView"
          :options="viewOptions"
        />
      </div>
    </div>

    <PlatformSection
      v-if="
        props.demoId === 'platform-section' ||
        props.demoId === 'platform-section-title' ||
        props.demoId === 'platform-descriptions'
      "
      description="用于统一详情页模块标题、附加操作和正文容器。"
      title="模块标题与内容区"
    >
      <template #extra>
        <span class="demo-muted">平台化标题与内容容器</span>
      </template>

      <div class="demo-flow">
        <PlatformSectionTitle
          v-if="props.demoId !== 'platform-descriptions'"
          description="适合卡片头、右侧信息块和详情页二级模块。"
          divider
          icon="lucide:badge-check"
          title="运行概览"
        />

        <PlatformDescriptions
          v-if="props.demoId !== 'platform-section-title'"
          :column="2"
          :items="descriptionItems"
        />
      </div>
    </PlatformSection>

    <div v-if="props.demoId === 'platform-status-board'" class="demo-surface demo-flow">
      <h3 class="demo-title">状态看板</h3>
      <PlatformStatusBoard :columns="statusColumns" />
    </div>
  </PlatformViewStack>
</template>
