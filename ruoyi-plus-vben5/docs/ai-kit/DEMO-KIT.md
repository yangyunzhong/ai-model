# DemoKit 工程化说明

`apps/demokit` 是平台组件、页面模板和 AI 使用规则的可运行展示入口。它的定位不是“再做一套 UI”，而是让 AI 和开发能快速看见已有能力，再回到 `packages/platform-ui` 和 `apps/web-antd` 做真实实现。

## 当前结构

```txt
apps/demokit/
  src/
    App.vue
    registry.ts
    demos/
    guides/
    styles.css
```

## 职责边界

| 区域 | 职责 | 禁止 |
| --- | --- | --- |
| `src/registry.ts` | 组件、示例、业务范例索引 | 只登记不真实存在的组件 |
| `src/demos/` | 展示单个平台组件用法 | 复制业务页面完整实现 |
| `src/guides/` | 展示规则页、构建指南、组件优先级 | 与 `docs/ai-kit` 规则长期不一致 |
| `packages/platform-ui` | 平台组件源码 | 反向依赖 DemoKit 或业务路由 |
| `apps/web-antd` | 真实业务页面 | 直接消费 DemoKit 内部组件 |

## 维护规则

1. 新增平台组件时，先在 `packages/platform-ui` 实现，再补 DemoKit demo。
2. `registry.ts` 中的 `sourcePath` 必须指向真实源码。
3. `usageSnippet` 应展示推荐写法，不展示临时绕路写法。
4. 指南页只表达长期规则，短期实验不要写成硬规则。
5. DemoKit 内部页面优先使用 `@st/platform-ui`，不重复手写平台结构。

## 平台组件生命周期

新增、修改平台封装组件时，AI 默认要同步维护 DemoKit，不需要用户额外提醒。

| 动作 | DemoKit 行为 |
| --- | --- |
| 修改已有 `Platform*` 组件 | DemoKit demo 直接引用 `@st/platform-ui`，预览应自动体现组件变化 |
| 新增 `Platform*` 组件并导出 | 必须新增或更新 `apps/demokit/src/demos/*` 和 `apps/demokit/src/registry.ts` |
| 新增组件所属场景已存在 | 归入现有分类，例如数据录入、数据展示、反馈 |
| 新增组件属于新页面模式 | 同步更新 `docs/ai-kit/TYPICAL-PAGES.md` 或 `AI-USAGE.md` |
| 新增 token 或样式变量 | 同步更新 `DESIGN-SYSTEM.md` |

`pnpm run check:ai-kit` 会扫描 `packages/platform-ui/src/**/index.ts` 中导出的 `Platform*` 组件，并检查它是否已登记到 `apps/demokit/src/registry.ts`。如果有遗漏，AI 必须补齐 demo 和归类。

## 与 AI Kit 的关系

| DemoKit 内容 | AI Kit 文档 |
| --- | --- |
| `src/registry.ts` | `components-manifest.json` |
| `src/guides/system-component-priority-page.vue` | `AI-USAGE.md` |
| `src/guides/design-system-spec-page.vue` | `DESIGN-SYSTEM.md` |
| `src/guides/system-build-guide-page.vue` | `DEMO-KIT.md` |

当 DemoKit 规则页和 `docs/ai-kit` 不一致时，以 `docs/ai-kit` 为 AI 入口，以 `.trellis/spec/frontend` 为长期治理源。

## 推荐启动

```bash
pnpm -F @st/demokit run dev
```

默认地址：

```txt
http://127.0.0.1:5174
```

## 新增 Demo 检查清单

- 是否从 `@st/platform-ui` 引入组件？
- 是否补 `registry.ts`？
- 是否补 `components-manifest.json`？
- 是否说明使用场景和避免场景？
- 是否避免把 DemoKit 私有结构拿去业务页面复用？
- 是否运行 `pnpm -F @st/demokit run typecheck` 或说明未运行原因？
