<script setup lang="ts">
import { PlatformButton } from '@st/platform-ui';
import { PlatformEmpty } from '@st/platform-ui';
</script>

<template>
  <div class="demo-flow">
    <div class="demo-mode">
      <h1>默认空状态</h1>
      <h2>scene="default" — 通用暂无数据占位</h2>
      <div class="demo-surface">
        <PlatformEmpty />
      </div>
    </div>

    <div class="demo-mode">
      <h1>无搜索结果</h1>
      <h2>scene="no-result" — 搜索/筛选后无匹配数据</h2>
      <div class="demo-surface">
        <PlatformEmpty scene="no-result" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>无权限</h1>
      <h2>scene="no-permission" — 用户无访问权限</h2>
      <div class="demo-surface">
        <PlatformEmpty scene="no-permission" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>网络断开</h1>
      <h2>scene="offline" — 网络连接已断开</h2>
      <div class="demo-surface">
        <PlatformEmpty scene="offline" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>简洁模式</h1>
      <h2>scene="simple" — 使用 antdv 内置小图，适合表格、列表内嵌</h2>
      <div class="demo-surface">
        <PlatformEmpty scene="simple" />
      </div>
    </div>

    <div class="demo-mode">
      <h1>自定义描述</h1>
      <h2>通过 description 覆盖场景默认描述文案</h2>
      <div class="demo-surface">
        <PlatformEmpty
          icon="lucide:folder-tree"
          description="当前目录下还没有文件，点击上传添加"
        />
      </div>
    </div>

    <div class="demo-mode">
      <h1>自定义图标</h1>
      <h2>通过 icon 指定 PlatformIcon 图标，覆盖场景默认图标</h2>
      <div class="demo-surface">
        <PlatformEmpty
          icon="lucide:mail-check"
          description="暂无未读消息"
        />
      </div>
    </div>

    <div class="demo-mode">
      <h1>自定义图片</h1>
      <h2>通过 image 传入图片 URL，替换默认图标</h2>
      <div class="demo-surface">
        <PlatformEmpty
          image="https://gw.alipayobjects.com/zos/antfincdn/ZHrcdLPrvN/empty.svg"
          description="自定义空状态图片"
        />
      </div>
    </div>

    <div class="demo-mode">
      <h1>带操作按钮</h1>
      <h2>通过默认插槽添加操作按钮，引导用户下一步操作</h2>
      <div class="demo-surface">
        <PlatformEmpty description="还没有任何项目">
          <PlatformButton type="primary" scene="toolbar">新建项目</PlatformButton>
        </PlatformEmpty>
      </div>
    </div>

    <div class="demo-mode">
      <h1>自定义描述插槽</h1>
      <h2>通过 description 插槽自定义描述内容，支持富文本</h2>
      <div class="demo-surface">
        <PlatformEmpty icon="lucide:circle-alert">
          <template #description>
            <span>数据加载失败，请</span>
            <a href="javascript:void(0)" style="color: hsl(var(--primary))">重试</a>
          </template>
        </PlatformEmpty>
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
</style>
