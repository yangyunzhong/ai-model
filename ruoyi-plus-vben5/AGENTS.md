# ruoyi-plus-vben5 AI 开发入口

本目录是正式前端参考底座。AI 在 `ruoyi-plus-vben5/` 内新增页面、维护 DemoKit、修改平台组件或调整样式前，必须先按本文件读取工程上下文。

## 必读顺序

1. `../.trellis/spec/frontend/index.md`
2. `../.trellis/spec/frontend/platform-governance.md`
3. `docs/ai-kit/AGENTS.md`
4. `docs/ai-kit/AI-USAGE.md`
5. 新增页面或菜单时读 `docs/ai-kit/ROUTING.md`
6. `docs/ai-kit/components-manifest.json`
7. 涉及设计 token 时读 `docs/ai-kit/DESIGN-SYSTEM.md`
8. 涉及 DemoKit 时读 `docs/ai-kit/DEMO-KIT.md`

## 默认工作流

1. 先判断页面类型：列表页、详情页、表单页、仪表盘、看板页、弹窗/抽屉。
2. 从 `components-manifest.json` 找优先组件和禁止替代项。
3. 输出组件映射：页面区域、推荐组件、样式落点、是否需要新增平台能力。
4. 默认从 `@st/platform-ui` 导入平台组件。
5. 默认从 `@st/platform-styles` 和 CSS token 消费间距、颜色、圆角、阴影。
6. 若现有平台组件不满足，先说明缺口，再决定扩展平台组件或保留页面私有结构。
7. 完成后运行 `pnpm run check:ai-kit`，再按变更范围运行类型检查、构建或浏览器验证。
8. 只要新增或修改路由，必须运行 `pnpm run check:routes`。

## 硬性规则

- 不绕开 `@st/platform-ui` 自己拼后台页面基础结构。
- 不在业务页面长期维护 Button、Table、Form、Modal、Drawer、Tree、Tabs、StatCard、FileList 等通用组件实现。
- 不把 DemoKit 示例代码复制成业务页面；DemoKit 是索引和验证场，真实能力沉淀在 `packages/platform-ui`。
- 不在页面 scoped CSS 中硬编码可被 token 表达的颜色、圆角、阴影和标准间距。
- 不给模块卡片默认增加描边；业务页、工作台、看板页、DemoKit 和典型页中的模块卡片，默认只用背景、圆角、阴影和间距区分层级，除非需求明确要求描边。
- 不为了单页效果修改 Vben 核心布局、菜单、面包屑、tabs，除非先完成影响范围和回滚方案说明。

## 常用命令

```bash
pnpm run check:ai-kit
pnpm -F @st/demokit run typecheck
pnpm -F @st/demokit run build
pnpm -F @vben/web-antd run dev
```

本地预览端口约定：

- `web-antd` 固定使用 `http://127.0.0.1:5175/`。
- `demokit` 固定使用 `http://127.0.0.1:5174/`。
- 启动 `web-antd` 前先检查并清理旧的 `web-antd` Vite 进程；不允许同时保留 `5173` 和 `5175` 两个 `web-antd` 服务。
- `web-antd` 的 dev 脚本使用 `--strictPort --force`，端口被占用时必须暴露问题，不能自动漂移到其他端口。

## 产物维护

- 新增平台组件：同步更新 `packages/platform-ui/src/index.ts`、`apps/demokit/src/registry.ts`、`docs/ai-kit/components-manifest.json`。
- 新增页面模式：同步更新 `docs/page-component-mapping.md` 和 `docs/ai-kit/AI-USAGE.md`。
- 新增 token：同步更新 `packages/platform-styles/src/tokens/index.css` 和 `docs/ai-kit/DESIGN-SYSTEM.md`。
- 新增 DemoKit 指南：保持 `apps/demokit/src/guides/*` 与 `docs/ai-kit/*` 规则一致。
