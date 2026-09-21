<script setup lang="ts">
import { ref, computed } from 'vue';

import { PlatformIcon } from '@st/platform-ui';

const iconfontIcons = [
  { name: 'icon-xinzeng', label: '新增' },
  { name: 'icon-bianji', label: '编辑' },
  { name: 'icon-shanchu', label: '删除' },
  { name: 'icon-sousuo', label: '搜索' },
  { name: 'icon-shaixuan', label: '筛选' },
  { name: 'icon-xiazai1', label: '下载' },
  { name: 'icon-fanhui1', label: '返回' },
  { name: 'icon-jiantouxia', label: '箭头下' },
  { name: 'icon-chakangengduo', label: '查看更多' },
  { name: 'icon-zhangjie', label: '章节' },
  { name: 'icon-qiehuan', label: '切换' },
  { name: 'icon-shuiliduixiangshuju', label: '水利对象数据' },
  { name: 'icon-danchuangguanli', label: '弹窗管理' },
  { name: 'icon-danchuangquanping', label: '弹窗全屏' },
  { name: 'icon-danchuangshouqi', label: '弹窗收起' },
  { name: 'icon-gongsi', label: '公司' },
  { name: 'icon-gongshiyujianzhiguankong', label: '公示与建筑管控' },
  { name: 'icon-gantetu', label: '甘特图' },
  { name: 'icon-kanban', label: '看板' },
  { name: 'icon-jindukeshihuagenzong', label: '进度可视化跟踪' },
  { name: 'icon-biandongyuliushishuaitongji', label: '变动率流失率统计' },
  { name: 'icon-xiangmuxinxiguanli', label: '项目信息管理' },
  { name: 'icon-xiangmuzonglan', label: '项目总览' },
  { name: 'icon-wendangyutaizhangguanli', label: '文档与台账管理' },
  { name: 'icon-renyuanzonglan', label: '人员总览' },
  { name: 'icon-renyuandanganguanli', label: '人员档案管理' },
  { name: 'icon-zizhiyuzhunruguankong', label: '资质与准入管控' },
  { name: 'icon-zhongqipingguyuyanshouguanli', label: '中期评估与验收管理' },
  { name: 'icon-shigongguanli', label: '施工管理' },
  { name: 'icon-hetongyufukuanguanli', label: '合同与付款管理' },
];

const lucideIcons = [
  { name: 'lucide:plus', label: 'Plus' },
  { name: 'lucide:search', label: 'Search' },
  { name: 'lucide:settings', label: 'Settings' },
  { name: 'lucide:check', label: 'Check' },
  { name: 'lucide:x', label: 'X' },
  { name: 'lucide:chevron-down', label: 'ChevronDown' },
  { name: 'lucide:chevron-left', label: 'ChevronLeft' },
  { name: 'lucide:chevron-right', label: 'ChevronRight' },
  { name: 'lucide:arrow-up', label: 'ArrowUp' },
  { name: 'lucide:arrow-down', label: 'ArrowDown' },
  { name: 'lucide:arrow-left', label: 'ArrowLeft' },
  { name: 'lucide:arrow-right-to-line', label: 'ArrowRightToLine' },
  { name: 'lucide:ellipsis', label: 'Ellipsis' },
  { name: 'lucide:eye', label: 'Eye' },
  { name: 'lucide:eye-off', label: 'EyeOff' },
  { name: 'lucide:bell', label: 'Bell' },
  { name: 'lucide:mail-check', label: 'MailCheck' },
  { name: 'lucide:lock-keyhole', label: 'LockKeyhole' },
  { name: 'lucide:log-out', label: 'LogOut' },
  { name: 'lucide:copy', label: 'Copy' },
  { name: 'lucide:edit', label: 'Edit' },
  { name: 'lucide:trash-2', label: 'Trash2' },
  { name: 'lucide:download', label: 'Download' },
  { name: 'lucide:upload', label: 'Upload' },
  { name: 'lucide:external-link', label: 'ExternalLink' },
  { name: 'lucide:folder-tree', label: 'FolderTree' },
  { name: 'lucide:table-properties', label: 'TableProperties' },
  { name: 'lucide:badge-check', label: 'BadgeCheck' },
  { name: 'lucide:fullscreen', label: 'Fullscreen' },
  { name: 'lucide:minimize', label: 'Minimize' },
  { name: 'lucide:circle-alert', label: 'CircleAlert' },
  { name: 'lucide:circle-check-big', label: 'CircleCheckBig' },
  { name: 'lucide:circle-x', label: 'CircleX' },
  { name: 'lucide:circle-help', label: 'CircleHelp' },
  { name: 'lucide:info', label: 'Info' },
  { name: 'lucide:loader-circle', label: 'LoaderCircle' },
  { name: 'lucide:pin', label: 'Pin' },
  { name: 'lucide:pin-off', label: 'PinOff' },
  { name: 'lucide:layout-grid', label: 'LayoutGrid' },
  { name: 'lucide:grid', label: 'Grid' },
  { name: 'lucide:grip', label: 'Grip' },
  { name: 'lucide:grip-vertical', label: 'GripVertical' },
  { name: 'lucide:panel-left', label: 'PanelLeft' },
  { name: 'lucide:panel-right', label: 'PanelRight' },
  { name: 'lucide:rotate-cw', label: 'RotateCw' },
  { name: 'lucide:shrink', label: 'Shrink' },
  { name: 'lucide:expand', label: 'Expand' },
  { name: 'lucide:sun', label: 'Sun' },
  { name: 'lucide:moon-star', label: 'MoonStar' },
  { name: 'lucide:palette', label: 'Palette' },
  { name: 'lucide:swatch-book', label: 'SwatchBook' },
  { name: 'lucide:languages', label: 'Languages' },
  { name: 'lucide:user-round-pen', label: 'UserRoundPen' },
  { name: 'lucide:inbox', label: 'Inbox' },
  { name: 'lucide:inspection-panel', label: 'InspectionPanel' },
  { name: 'lucide:book-open-text', label: 'BookOpenText' },
  { name: 'lucide:corner-down-left', label: 'CornerDownLeft' },
  { name: 'lucide:square-check-big', label: 'SquareCheckBig' },
  { name: 'lucide:square-minus', label: 'SquareMinus' },
  { name: 'lucide:fold-horizontal', label: 'FoldHorizontal' },
];

const searchQuery = ref('');

const filteredIconfont = computed(() => {
  if (!searchQuery.value) return iconfontIcons;
  const q = searchQuery.value.toLowerCase();
  return iconfontIcons.filter(
    (i) => i.name.toLowerCase().includes(q) || i.label.includes(q),
  );
});

const filteredLucide = computed(() => {
  if (!searchQuery.value) return lucideIcons;
  const q = searchQuery.value.toLowerCase();
  return lucideIcons.filter(
    (i) => i.name.toLowerCase().includes(q) || i.label.toLowerCase().includes(q),
  );
});
</script>

<template>
  <div class="demo-flow">
    <div class="demo-mode">
      <h1>图标来源</h1>
      <h2>
        PlatformIcon 是双源图标路由器，不内置任何图标。根据 icon
        值自动判断来源：以 icon- 开头走 iconfont 字体图标，否则走 VbenIcon（基于
        Iconify，支持 lucide: 等前缀）。
      </h2>
    </div>

    <div class="demo-mode">
      <h1>搜索图标</h1>
      <input
        v-model="searchQuery"
        class="demo-search-input"
        placeholder="输入图标名称或中文标签搜索..."
      />
    </div>

    <div class="demo-mode">
      <h1>iconfont 业务图标（{{ filteredIconfont.length }} / {{ iconfontIcons.length }}）</h1>
      <h2>icon 值以 icon- 开头，使用项目 iconfont 字体文件</h2>
      <div class="demo-icon-grid">
        <div
          v-for="item in filteredIconfont"
          :key="item.name"
          class="demo-icon-item"
        >
          <PlatformIcon :icon="item.name" />
          <span class="demo-icon-item__label">{{ item.label }}</span>
          <code class="demo-icon-item__name">{{ item.name }}</code>
        </div>
      </div>
    </div>

    <div class="demo-mode">
      <h1>Lucide 图标（{{ filteredLucide.length }} / {{ lucideIcons.length }}）</h1>
      <h2>使用 lucide: 前缀，基于 Iconify 在线加载，支持 1000+ 图标</h2>
      <div class="demo-icon-grid">
        <div
          v-for="item in filteredLucide"
          :key="item.name"
          class="demo-icon-item"
        >
          <PlatformIcon :icon="item.name" />
          <span class="demo-icon-item__label">{{ item.label }}</span>
          <code class="demo-icon-item__name">{{ item.name }}</code>
        </div>
      </div>
    </div>

    <div class="demo-mode">
      <h1>图标尺寸</h1>
      <h2>通过 font-size 或 CSS 控制图标大小</h2>
      <div class="demo-row" style="gap: 24px; align-items: center">
        <span style="font-size: 12px"><PlatformIcon icon="lucide:check" /></span>
        <span style="font-size: 16px"><PlatformIcon icon="lucide:check" /></span>
        <span style="font-size: 20px"><PlatformIcon icon="lucide:check" /></span>
        <span style="font-size: 24px"><PlatformIcon icon="lucide:check" /></span>
        <span style="font-size: 32px"><PlatformIcon icon="lucide:check" /></span>
        <span style="font-size: 48px"><PlatformIcon icon="lucide:check" /></span>
      </div>
      <div class="demo-row" style="gap: 24px; align-items: center; margin-top: 12px">
        <span style="font-size: 12px"><PlatformIcon icon="icon-sousuo" /></span>
        <span style="font-size: 16px"><PlatformIcon icon="icon-sousuo" /></span>
        <span style="font-size: 20px"><PlatformIcon icon="icon-sousuo" /></span>
        <span style="font-size: 24px"><PlatformIcon icon="icon-sousuo" /></span>
        <span style="font-size: 32px"><PlatformIcon icon="icon-sousuo" /></span>
        <span style="font-size: 48px"><PlatformIcon icon="icon-sousuo" /></span>
      </div>
    </div>

    <div class="demo-mode">
      <h1>基础用法</h1>
      <div class="demo-row">
        <PlatformIcon icon="icon-xinzeng" />
        <PlatformIcon icon="lucide:search" />
        <PlatformIcon icon="lucide:settings" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.demo-mode {
  display: grid;
  gap: 12px;
}

.demo-mode h1 {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 16px;
  font-weight: 700;
}

.demo-mode h2 {
  margin: 0;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
  font-weight: 400;
  line-height: 1.6;
}

.demo-search-input {
  width: 100%;
  max-width: 400px;
  height: 36px;
  padding: 0 14px;
  color: hsl(var(--foreground));
  font-size: 14px;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-control);
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.demo-search-input:focus {
  border-color: hsl(var(--primary) / 45%);
  box-shadow: 0 0 0 3px hsl(var(--primary) / 10%);
}

.demo-search-input::placeholder {
  color: hsl(var(--muted-foreground));
}

.demo-icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.demo-icon-item {
  display: grid;
  gap: 6px;
  justify-items: center;
  padding: 16px 8px;
  text-align: center;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  border-radius: 12px;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;
  cursor: default;
}

.demo-icon-item:hover {
  border-color: hsl(var(--primary) / 30%);
  box-shadow: 0 4px 12px hsl(var(--primary) / 8%);
}

.demo-icon-item :deep(.platform-icon),
.demo-icon-item :deep(svg) {
  font-size: 24px;
}

.demo-icon-item__label {
  color: hsl(var(--foreground));
  font-size: 13px;
  font-weight: 600;
}

.demo-icon-item__name {
  color: hsl(var(--muted-foreground));
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 11px;
  word-break: break-all;
}
</style>
