<script setup lang="ts">
import type { PlatformTableColumns } from '@st/platform-ui';

import {
  PlatformSection,
  PlatformTable,
  PlatformViewStack,
} from '@st/platform-ui';

type TokenRow = {
  cssVar: string;
  description: string;
  key: string;
  name: string;
  value: string;
  swatch?: string;
  swatchMode?: 'background' | 'border' | 'text';
};

type TokenSection = {
  key: string;
  rows: TokenRow[];
  title: string;
};

const tokenColumns: PlatformTableColumns = [
  {
    dataIndex: 'name',
    key: 'name',
    title: '变量名',
    width: 300,
  },
  {
    dataIndex: 'value',
    key: 'value',
    title: '变量值',
    width: 300,
  },
  {
    dataIndex: 'cssVar',
    key: 'cssVar',
    title: 'CSS变量',
    width: 260,
  },
  {
    dataIndex: 'description',
    key: 'description',
    title: '描述',
  },
];

const tokenSections: TokenSection[] = [
  {
    key: 'primary',
    title: '主色',
    rows: [
      {
        cssVar: '--st-color-brand',
        description: '常规',
        key: 'brand',
        name: 'brand',
        swatch: 'hsl(var(--st-color-brand))',
        swatchMode: 'background',
        value: '146 100% 30%',
      },
      {
        cssVar: '--st-color-brand-hover',
        description: '悬浮',
        key: 'brand-hover',
        name: 'brand-hover',
        swatch: 'hsl(var(--st-color-brand-hover))',
        swatchMode: 'background',
        value: '146 100% 34%',
      },
      {
        cssVar: '--st-color-brand-active',
        description: '点击',
        key: 'brand-active',
        name: 'brand-active',
        swatch: 'hsl(var(--st-color-brand-active))',
        swatchMode: 'background',
        value: '146 100% 26%',
      },
      {
        cssVar: '--st-color-brand-outline',
        description: '描边 / 选中轮廓',
        key: 'brand-outline',
        name: 'brand-outline',
        swatch: 'hsl(var(--st-color-brand-outline))',
        swatchMode: 'background',
        value: '146 100% 30% / 30%',
      },
    ],
  },
  {
    key: 'success',
    title: '成功色',
    rows: [
      {
        cssVar: '--success',
        description: '成功提示 / 通过状态',
        key: 'success',
        name: 'success',
        swatch: 'hsl(var(--success))',
        swatchMode: 'background',
        value: '144 57% 58%',
      },
    ],
  },
  {
    key: 'warning',
    title: '警示色',
    rows: [
      {
        cssVar: '--warning',
        description: '警示提示 / 待处理',
        key: 'warning',
        name: 'warning',
        swatch: 'hsl(var(--warning))',
        swatchMode: 'background',
        value: '42 84% 61%',
      },
      {
        cssVar: '--st-color-warning-action',
        description: '操作型警示强调',
        key: 'warning-action',
        name: 'warning-action',
        swatch: 'hsl(var(--st-color-warning-action))',
        swatchMode: 'background',
        value: '71 61% 54%',
      },
    ],
  },
  {
    key: 'danger',
    title: '错误色',
    rows: [
      {
        cssVar: '--destructive',
        description: '错误提示 / 表单校验',
        key: 'destructive',
        name: 'destructive',
        swatch: 'hsl(var(--destructive))',
        swatchMode: 'background',
        value: '359.33 100% 65.1%',
      },
      {
        cssVar: '--st-color-danger',
        description: '删除 / 驳回 / 危险动作',
        key: 'platform-danger',
        name: 'platform-danger',
        swatch: 'hsl(var(--st-color-danger))',
        swatchMode: 'background',
        value: '355 76% 59%',
      },
    ],
  },
  {
    key: 'link',
    title: '链接色',
    rows: [
      {
        cssVar: '-',
        description: '当前默认沿用主色，待后续统一封装',
        key: 'link',
        name: 'link',
        value: '待补充',
      },
    ],
  },
  {
    key: 'border',
    title: '边框颜色',
    rows: [
      {
        cssVar: '--st-color-border-subtle',
        description: '浅色 / 卡片边框 / 分隔线',
        key: 'border-subtle',
        name: 'border-subtle',
        swatch: 'hsl(var(--st-color-border-subtle))',
        swatchMode: 'border',
        value: '220 23% 95%',
      },
      {
        cssVar: '--st-color-border-control',
        description: '一般 / 输入框描边',
        key: 'border-control',
        name: 'border-control',
        swatch: 'hsl(var(--st-color-border-control))',
        swatchMode: 'border',
        value: '220 13% 91%',
      },
      {
        cssVar: '--border',
        description: '系统默认边框',
        key: 'border',
        name: 'border',
        swatch: 'hsl(var(--border))',
        swatchMode: 'border',
        value: 'var(--st-color-border-subtle)',
      },
      {
        cssVar: '--input',
        description: '表单输入框边框',
        key: 'input-border',
        name: 'input-border',
        swatch: 'hsl(var(--input))',
        swatchMode: 'border',
        value: 'var(--st-color-border-control)',
      },
    ],
  },
  {
    key: 'fill',
    title: '填充颜色',
    rows: [
      {
        cssVar: '--st-color-fill-selected',
        description: '选中 / 浅强调',
        key: 'fill-selected',
        name: 'fill-selected',
        swatch: 'hsl(var(--st-color-fill-selected))',
        swatchMode: 'background',
        value: '146 100% 30% / 10%',
      },
      {
        cssVar: '--st-color-fill-disabled',
        description: '禁用填充',
        key: 'fill-disabled',
        name: 'fill-disabled',
        swatch: 'hsl(var(--st-color-fill-disabled))',
        swatchMode: 'background',
        value: '218 13% 86%',
      },
      {
        cssVar: '--accent',
        description: '悬浮 / 轻交互背景',
        key: 'accent',
        name: 'accent',
        swatch: 'hsl(var(--accent))',
        swatchMode: 'background',
        value: 'var(--st-color-fill-selected)',
      },
    ],
  },
  {
    key: 'text',
    title: '文字颜色',
    rows: [
      {
        cssVar: '--st-color-text-primary',
        description: '主文字',
        key: 'text-primary',
        name: 'text-primary',
        swatch: 'hsl(var(--st-color-text-primary))',
        swatchMode: 'text',
        value: '223 10% 13%',
      },
      {
        cssVar: '--st-color-text-secondary',
        description: '次级说明',
        key: 'text-secondary',
        name: 'text-secondary',
        swatch: 'hsl(var(--st-color-text-secondary))',
        swatchMode: 'text',
        value: '220 9% 28%',
      },
      {
        cssVar: '--st-color-text-tertiary',
        description: '弱化文案',
        key: 'text-tertiary',
        name: 'text-tertiary',
        swatch: 'hsl(var(--st-color-text-tertiary))',
        swatchMode: 'text',
        value: '222 5% 48%',
      },
      {
        cssVar: '--st-color-text-placeholder',
        description: '占位文字',
        key: 'text-placeholder',
        name: 'text-placeholder',
        swatch: 'hsl(var(--st-color-text-placeholder))',
        swatchMode: 'text',
        value: '223 10% 13% / 40%',
      },
      {
        cssVar: '--st-color-text-disabled',
        description: '禁用文字',
        key: 'text-disabled',
        name: 'text-disabled',
        swatch: 'hsl(var(--st-color-text-disabled))',
        swatchMode: 'text',
        value: '222 8% 78%',
      },
    ],
  },
  {
    key: 'background',
    title: '背景颜色',
    rows: [
      {
        cssVar: '--st-color-page-bg',
        description: '页面背景',
        key: 'page-bg',
        name: 'page-bg',
        swatch: 'hsl(var(--st-color-page-bg))',
        swatchMode: 'background',
        value: '220 20% 95%',
      },
      {
        cssVar: '--st-color-card-bg',
        description: '卡片背景',
        key: 'card-bg',
        name: 'card-bg',
        swatch: 'hsl(var(--st-color-card-bg))',
        swatchMode: 'background',
        value: '0 0% 100%',
      },
      {
        cssVar: '--background',
        description: '系统基础背景',
        key: 'background',
        name: 'background',
        swatch: 'hsl(var(--background))',
        swatchMode: 'background',
        value: '0 0% 100%',
      },
      {
        cssVar: '--popover',
        description: '弹层背景',
        key: 'popover',
        name: 'popover',
        swatch: 'hsl(var(--popover))',
        swatchMode: 'background',
        value: '0 0% 100%',
      },
    ],
  },
  {
    key: 'font-size',
    title: '字体大小',
    rows: [
      {
        cssVar: '--st-font-size-sm',
        description: '小号',
        key: 'font-sm',
        name: 'font-size-sm',
        value: '13px',
      },
      {
        cssVar: '--st-font-size-base',
        description: '常规',
        key: 'font-base',
        name: 'font-size-base',
        value: '14px',
      },
      {
        cssVar: '--st-font-size-lg',
        description: '较大',
        key: 'font-lg',
        name: 'font-size-lg',
        value: '16px',
      },
      {
        cssVar: '--st-font-size-title',
        description: '标题',
        key: 'font-title',
        name: 'font-size-title',
        value: '18px',
      },
    ],
  },
  {
    key: 'font-weight',
    title: '字重',
    rows: [
      {
        cssVar: '-',
        description: '常规',
        key: 'fw-400',
        name: 'font-weight-400',
        value: '400',
      },
      {
        cssVar: '-',
        description: '中等（加粗）',
        key: 'fw-500',
        name: 'font-weight-500',
        value: '500',
      },
      {
        cssVar: '-',
        description: '半粗',
        key: 'fw-600',
        name: 'font-weight-600',
        value: '600',
      },
      {
        cssVar: '-',
        description: '粗体',
        key: 'fw-700',
        name: 'font-weight-700',
        value: '700',
      },
    ],
  },
  {
    key: 'size',
    title: '尺寸',
    rows: [
      {
        cssVar: '--st-layout-section-gap',
        description: '模块间距',
        key: 'section-gap',
        name: 'layout-section-gap',
        value: '24px',
      },
      {
        cssVar: '--st-module-content-padding',
        description: '模块内容内边距',
        key: 'module-padding',
        name: 'module-content-padding',
        value: '24px',
      },
      {
        cssVar: '--st-control-height',
        description: '基础控件高度',
        key: 'control-height',
        name: 'control-height',
        value: '36px',
      },
      {
        cssVar: '--st-header-height',
        description: '头部高度',
        key: 'header-height',
        name: 'header-height',
        value: '56px',
      },
    ],
  },
  {
    key: 'component-size',
    title: '组件尺寸',
    rows: [
      {
        cssVar: '--st-header-action-size',
        description: '头部操作按钮',
        key: 'header-action',
        name: 'header-action-size',
        value: '36px',
      },
      {
        cssVar: '--st-table-header-height',
        description: '表头高度',
        key: 'table-header',
        name: 'table-header-height',
        value: '44px',
      },
      {
        cssVar: '--st-table-row-height',
        description: '表格行高',
        key: 'table-row',
        name: 'table-row-height',
        value: '44px',
      },
      {
        cssVar: '--st-tree-node-height',
        description: '树节点高度',
        key: 'tree-node',
        name: 'tree-node-height',
        value: '32px',
      },
      {
        cssVar: '--st-pagination-item-size',
        description: '分页项尺寸',
        key: 'pagination-item',
        name: 'pagination-item-size',
        value: '32px',
      },
      {
        cssVar: '--st-stat-card-icon-box-size',
        description: '指标卡图标盒子',
        key: 'stat-icon-box',
        name: 'stat-card-icon-box-size',
        value: '48px',
      },
    ],
  },
  {
    key: 'border-width',
    title: '边框宽度',
    rows: [
      {
        cssVar: '-',
        description: '当前系统默认 1px，待后续统一 token 化',
        key: 'border-width-default',
        name: 'border-width-default',
        value: '1px',
      },
    ],
  },
  {
    key: 'radius',
    title: '边框圆角',
    rows: [
      {
        cssVar: '--st-radius-control',
        description: '控件圆角',
        key: 'radius-control',
        name: 'radius-control',
        value: '4px',
      },
      {
        cssVar: '--st-radius-card',
        description: '卡片圆角',
        key: 'radius-card',
        name: 'radius-card',
        value: '6px',
      },
      {
        cssVar: '--st-radius-menu',
        description: '菜单圆角',
        key: 'radius-menu',
        name: 'radius-menu',
        value: '4px',
      },
      {
        cssVar: '--radius',
        description: '系统基础圆角',
        key: 'radius-base',
        name: 'radius-base',
        value: '0.25rem',
      },
    ],
  },
  {
    key: 'shadow',
    title: '阴影',
    rows: [
      {
        cssVar: '--st-shadow-card',
        description: '卡片阴影',
        key: 'shadow-card',
        name: 'shadow-card',
        value: '0 4px 8px hsl(146 100% 10% / 8%)',
      },
      {
        cssVar: '--st-shadow-stat-card',
        description: '指标卡默认阴影',
        key: 'shadow-stat-card',
        name: 'shadow-stat-card',
        value: 'var(--st-shadow-card)',
      },
      {
        cssVar: '--st-shadow-stat-card-hover',
        description: '指标卡悬浮阴影',
        key: 'shadow-stat-card-hover',
        name: 'shadow-stat-card-hover',
        value: '0 10px 22px hsl(146 100% 10% / 10%)',
      },
    ],
  },
];
</script>

<template>
  <PlatformViewStack class="guide-page guide-page--full">
    <div class="guide-callout">
      当前展示优先以 <code>@st/platform-styles</code> 和底层 design token 为准。
      没有统一 token 的分类不硬造值，先明确标注“待补充”，后续新增时直接补到这里。
    </div>

    <PlatformSection
      v-for="section in tokenSections"
      :key="section.key"
      :title="section.title"
    >
      <PlatformTable
        :column-setting-enabled="false"
        :columns="tokenColumns"
        :data-source="section.rows"
        :pagination="false"
        :show-index="false"
        row-key="key"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column?.dataIndex === 'name'">
            <div class="guide-token-row-name">
              <span
                v-if="record.swatch"
                class="guide-token-chip"
                :class="{
                  'is-border': record.swatchMode === 'border',
                  'is-text': record.swatchMode === 'text',
                }"
                :style="
                  record.swatchMode === 'text'
                    ? { color: record.swatch }
                    : record.swatchMode === 'border'
                      ? { borderColor: record.swatch }
                      : { background: record.swatch }
                "
              >
                {{ record.swatchMode === 'text' ? 'Aa' : '' }}
              </span>
              <span>{{ record.name }}</span>
            </div>
          </template>
          <code v-else-if="column?.dataIndex === 'value'">{{ record.value }}</code>
          <code v-else-if="column?.dataIndex === 'cssVar'">{{ record.cssVar }}</code>
          <span v-else>{{ record.description }}</span>
        </template>
      </PlatformTable>
    </PlatformSection>
  </PlatformViewStack>
</template>
