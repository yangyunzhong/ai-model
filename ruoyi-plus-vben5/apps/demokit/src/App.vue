<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { PlatformIcon, PlatformModal, PlatformViewToolbar } from '@st/platform-ui';

import type { DemoComponentEntry, GuideEntry, TypicalBusinessPageEntry } from './registry';

import {
  businessPages,
  componentCategories,
  componentRegistry,
  guideRegistry,
  knownPlatformGroups,
  knownPlatformComponentCount,
} from './registry';

type ActiveSection = 'components' | 'guides' | 'overview';

const demoSourceFiles = import.meta.glob('./demos/*.vue', {
  eager: true,
  import: 'default',
  query: '?raw',
}) as Record<string, string>;

const platformSourceFiles = import.meta.glob('../../packages/platform-ui/src/**/*.vue', {
  eager: true,
  import: 'default',
  query: '?raw',
}) as Record<string, string>;

const demoCount = componentRegistry.length + businessPages.length;
const categoryCount = componentCategories.length;
const guideCount = guideRegistry.length;
const knownGroupCount = knownPlatformGroups.length;

const initialHash = location.hash.replace('#', '');
const initialGuide = guideRegistry.find((item) => item.id === initialHash);
const initialComponent = componentRegistry.find((item) => item.id === initialHash);

const activeSection = ref<ActiveSection>(
  initialGuide ? 'guides' : initialComponent ? 'components' : 'overview',
);
const activeId = ref(initialHash || componentRegistry[0]?.id || guideRegistry[0]?.id || '');
const activeTab = ref<'code' | 'docs' | 'preview'>('preview');
const keyword = ref('');
const activeBusinessPage = ref<TypicalBusinessPageEntry | null>(null);
const businessPageModalOpen = ref(false);

const filteredComponents = computed(() => {
  const query = keyword.value.trim().toLowerCase();
  if (!query) {
    return componentRegistry;
  }
  return componentRegistry.filter((item) =>
    [item.title, item.name, item.category, item.description, item.group]
      .join(' ')
      .toLowerCase()
      .includes(query),
  );
});

const groupedComponents = computed(() => {
  return componentCategories
    .map((category) => ({
      ...category,
      items: filteredComponents.value.filter((item) => item.category === category.title),
    }))
    .filter((group) => group.items.length > 0);
});

const categoryCards = computed(() =>
  componentCategories.map((category) => ({
    count: componentRegistry.filter((item) => item.category === category.title).length,
    ...category,
  })),
);

const activeComponent = computed<DemoComponentEntry>(
  () =>
    componentRegistry.find((item) => item.id === activeId.value) ||
    filteredComponents.value[0] ||
    componentRegistry[0]!,
);

const activeGuide = computed<GuideEntry>(
  () => guideRegistry.find((item) => item.id === activeId.value) || guideRegistry[0]!,
);

const activeDemoSource = computed(() =>
  getRawSource(activeComponent.value.demoPath, './', demoSourceFiles),
);

const activePlatformSource = computed(() =>
  getRawSource(activeComponent.value.sourcePath, '../../', platformSourceFiles),
);

const componentHeaderTabs = [
  { label: '预览', value: 'preview' },
  { label: '文档', value: 'docs' },
  { label: '源码', value: 'code' },
] as const;

watch(keyword, (value) => {
  if (value.trim()) {
    activeSection.value = 'components';
  }
  if (
    filteredComponents.value.length > 0 &&
    !filteredComponents.value.some((item) => item.id === activeId.value)
  ) {
    activeId.value = filteredComponents.value[0]!.id;
  }
});

watch(activeId, (id) => {
  if (id) {
    location.hash = id;
  }
});

function selectComponent(id: string) {
  activeId.value = id;
  activeSection.value = 'components';
  activeTab.value = 'preview';
}

function selectGuide(id: string) {
  activeId.value = id;
  activeSection.value = 'guides';
}

function openSection(section: ActiveSection) {
  activeSection.value = section;
}

function openBusinessPage(page: TypicalBusinessPageEntry) {
  activeBusinessPage.value = page;
  businessPageModalOpen.value = true;
}

function getRawSource(
  sourcePath: string,
  prefix: string,
  sourceFiles: Record<string, string>,
) {
  const fileKey = `${prefix}${sourcePath.replace(/^apps\/demokit\/src\//, '')}`;
  return (
    sourceFiles[fileKey] ||
    `// 未找到源码文件\n// 预期路径: ${sourcePath}\n// 实际 glob key: ${fileKey}`
  );
}
</script>

<template>
  <main class="demokit-layout">
    <div class="demokit-shell">
      <aside class="demokit-sidebar">
        <div class="demokit-brand">
          <span class="demokit-logo" aria-hidden="true">
            <PlatformIcon icon="icon-LOGO-green" />
          </span>
          <strong>GZZR 平台母版可视化</strong>
      </div>

      <label class="demokit-search">
        <input
          v-model="keyword"
          placeholder="搜索组件、文档、Demo"
          @focus="openSection('components')"
        />
        <span class="demokit-search__shortcut" aria-hidden="true">
          <span class="demokit-search__key">⌘</span>
            <span class="demokit-search__key">K</span>
          </span>
        </label>

        <nav class="demokit-nav">
          <section class="demokit-nav__group demokit-nav__group--guides">
            <h2>开发指南</h2>
            <button
              class="demokit-nav__guide-link"
              :class="{ 'is-active': activeSection === 'overview' }"
              type="button"
              @click="openSection('overview')"
            >
              组件总览
            </button>
            <button
              v-for="guide in guideRegistry"
              :key="guide.id"
              class="demokit-nav__guide-link"
              :class="{ 'is-active': guide.id === activeId && activeSection === 'guides' }"
              type="button"
              @click="selectGuide(guide.id)"
            >
              {{ guide.title }}
            </button>
          </section>

          <section v-for="group in groupedComponents" :key="group.title" class="demokit-nav__group">
            <h2>{{ group.title }}</h2>
            <button
              v-for="item in group.items"
              :key="item.id"
              :class="[
                'demokit-nav__item',
                { 'is-active': item.id === activeComponent.id && activeSection === 'components' },
              ]"
              type="button"
              @click="selectComponent(item.id)"
            >
              <span class="demokit-nav__item-line">
                <span class="demokit-nav__item-title">{{ item.title }}</span>
                <span class="demokit-nav__item-name">{{ item.name.replace(/^Platform/, '') }}</span>
              </span>
            </button>
          </section>
        </nav>
      </aside>

      <section class="demokit-main">
        <template v-if="activeSection === 'overview'">
          <PlatformViewToolbar
            description="当前以 `packages/platform-ui` 为真实源组件，以 DemoKit 做可视化查看入口。现在已经按 Ant Design Vue 左侧菜单的分类思路整理为通用、布局、导航、数据录入、数据展示、反馈六大类，组件缺口补齐后，后面可以直接从分类定位到源组件。"
            title="组件总览"
          />

          <section class="demokit-kpi-grid">
            <article class="demokit-kpi-card">
              <span>组件</span>
              <strong>{{ knownPlatformComponentCount }}</strong>
            </article>
            <article class="demokit-kpi-card">
              <span>Demos</span>
              <strong>{{ demoCount }}</strong>
            </article>
            <article class="demokit-kpi-card">
              <span>分类</span>
              <strong>{{ categoryCount }}</strong>
            </article>
            <article class="demokit-kpi-card">
              <span>组件组</span>
              <strong>{{ knownGroupCount }}</strong>
            </article>
            <article class="demokit-kpi-card">
              <span>规则页</span>
              <strong>{{ guideCount }}</strong>
            </article>
          </section>

          <section class="demokit-category-grid">
            <article
              v-for="category in categoryCards"
              :key="category.title"
              class="demokit-category-card"
            >
              <strong>{{ category.title }}</strong>
              <span>{{ category.count }} 个组件</span>
              <p>{{ category.description }}</p>
            </article>
          </section>

          <section class="demokit-showcase">
            <div class="demokit-showcase__header">
              <div>
                <h2>业务页面范例</h2>
                <p>完整页面的拼装示范，方便倒推需要补充哪些平台组件和页面模板。</p>
              </div>
            </div>
            <div class="demokit-showcase__grid">
              <button
                v-for="page in businessPages"
                :key="page.title"
                class="demokit-showcase-card"
                type="button"
                @click="openBusinessPage(page)"
              >
                <div class="demokit-showcase-card__thumb">
                  <span>{{ page.recipe }}</span>
                </div>
                <div class="demokit-showcase-card__body">
                  <h3>{{ page.title }}</h3>
                  <p>{{ page.description }}</p>
                  <span class="demokit-showcase-card__route">{{ page.routeHint }}</span>
                  <code>{{ page.sourcePath }}</code>
                </div>
              </button>
            </div>
          </section>

          <PlatformModal
            v-model:open="businessPageModalOpen"
            :footer="null"
            :width="920"
            class="demokit-business-modal"
            destroy-on-close
          >
            <article v-if="activeBusinessPage" class="demokit-business-detail">
              <header class="demokit-business-detail__header">
                <span>{{ activeBusinessPage.recipe }}</span>
                <h2>{{ activeBusinessPage.title }}</h2>
                <p>{{ activeBusinessPage.description }}</p>
              </header>

              <section class="demokit-business-detail__grid">
                <div>
                  <h3>参考位置</h3>
                  <dl>
                    <dt>业务入口</dt>
                    <dd>{{ activeBusinessPage.routeHint }}</dd>
                    <dt>源码路径</dt>
                    <dd><code>{{ activeBusinessPage.sourcePath }}</code></dd>
                  </dl>
                </div>

                <div>
                  <h3>优先组件</h3>
                  <ul class="demokit-business-detail__tags">
                    <li
                      v-for="component in activeBusinessPage.components"
                      :key="component"
                    >
                      {{ component }}
                    </li>
                  </ul>
                </div>
              </section>

              <section class="demokit-business-detail__section">
                <h3>后续生成页面时只替换这些内容</h3>
                <ul>
                  <li
                    v-for="item in activeBusinessPage.replacementPoints"
                    :key="item"
                  >
                    {{ item }}
                  </li>
                </ul>
              </section>

              <section class="demokit-business-detail__section">
                <h3>给 AI 的使用方式</h3>
                <pre><code>先匹配 {{ activeBusinessPage.recipe }};
参考 {{ activeBusinessPage.sourcePath }};
保留平台组件组合和 token 间距；
只替换业务字段、接口、权限码、按钮和文案。</code></pre>
              </section>
            </article>
          </PlatformModal>
        </template>

        <template v-else-if="activeSection === 'guides' && activeGuide">
          <article class="demokit-guide-panel">
            <component :is="activeGuide.component" />
          </article>
        </template>

        <template v-else>
          <div v-if="activeComponent" class="demokit-detail-header">
            <PlatformViewToolbar
              :description="`${activeComponent.name} · ${activeComponent.group} / ${activeComponent.id}`"
              :title="activeComponent.title"
            />
            <div class="demokit-tabs" role="tablist" aria-label="组件内容分段">
              <button
                v-for="tab in componentHeaderTabs"
                :key="tab.value"
                :class="{ 'is-active': activeTab === tab.value }"
                type="button"
                @click="activeTab = tab.value"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <article class="demokit-panel" v-if="activeComponent">
            <section v-if="activeTab === 'preview'" class="demokit-preview">
              <component :is="activeComponent.component" :demo-id="activeComponent.id" />
            </section>

            <section v-else-if="activeTab === 'docs'" class="demokit-docs">
              <h2>{{ activeComponent.name }}</h2>
              <p>{{ activeComponent.description }}</p>

              <h3>使用场景</h3>
              <ul>
                <li v-for="scene in activeComponent.scenarios" :key="scene">
                  {{ scene }}
                </li>
              </ul>

              <h3>API</h3>
              <ul>
                <li v-for="api in activeComponent.api" :key="api">
                  <code>{{ api }}</code>
                </li>
              </ul>

              <h3>修改入口</h3>
              <p>
                直接告诉 Codex 修改 <code>{{ activeComponent.name }}</code>，真实源码在
                <code>{{ activeComponent.sourcePath }}</code>。DemoKit 和引用它的业务项目会一起看到变化。
              </p>
            </section>

            <section v-else class="demokit-code">
              <div class="demokit-code__section">
                <p class="demokit-code__label">Demo 示例</p>
                <p class="demokit-code__path">{{ activeComponent.demoPath }}</p>
                <pre><code>{{ activeDemoSource }}</code></pre>
              </div>
              <div class="demokit-code__section">
                <p class="demokit-code__label">平台组件源码</p>
                <p class="demokit-code__path">{{ activeComponent.sourcePath }}</p>
                <pre><code>{{ activePlatformSource }}</code></pre>
              </div>
            </section>
          </article>
        </template>
      </section>
    </div>
  </main>
</template>
