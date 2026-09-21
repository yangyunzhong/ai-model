# AI Kit 导航

`docs/ai-kit/` 是给 AI、前端开发和 UI 协作者看的工程化入口。它把平台组件、设计 token、DemoKit 示例、页面模板和禁止规则放在一个可检索位置，避免每个 AI 工具重新猜项目结构。

## 文件职责

| 文件 | 用途 | 什么时候读 |
| --- | --- | --- |
| `AGENTS.md` | 本目录索引和强制读取顺序 | 所有 AI 前端任务 |
| `AI-USAGE.md` | 组件选型、页面模板、缺口处理 | 新增页面、重构页面、截图还原 |
| `DESIGN-SYSTEM.md` | token、密度、样式落点 | 调整视觉、间距、主题、全局样式 |
| `DESIGN.md` | AI 设计规则短入口 | 工具优先查找 DESIGN.md 时 |
| `DEMO-KIT.md` | DemoKit 结构和维护规则 | 维护 `apps/demokit` |
| `TYPICAL-PAGES.md` | 3 类高重复后台页面 Recipe | 新增列表页、指标列表页、左树右表页 |
| `ROUTING.md` | 路由配置模板和检查规则 | 新增页面、菜单、详情页 |
| `components-manifest.json` | AI 组件注册表 | 让 AI 决定用哪个组件 |

## AI 必须遵守的读取顺序

1. 先读 `../../AGENTS.md`，确认项目级入口。
2. 再读 `AI-USAGE.md`，确定页面骨架和组件优先级。
3. 查 `TYPICAL-PAGES.md`，判断是否命中 3 类高重复后台页面。
4. 新增页面或菜单时读 `ROUTING.md`，先输出路由方案。
5. 查 `components-manifest.json`，选择组件和避免项。
6. 涉及样式时读 `DESIGN-SYSTEM.md`。
7. 涉及 DemoKit 时读 `DEMO-KIT.md`。

## 输出要求

AI 在新增或重构页面前，必须先输出简版组件映射：

| 页面区域 | 推荐组件 | 样式落点 | 是否需要平台扩展 |
| --- | --- | --- | --- |
| 页面头部 | `PlatformViewToolbar` | 平台组件 | 否 |
| 查询区 | `PlatformQueryPanel` | 平台组件/token | 否 |
| 数据区 | `PlatformSection + PlatformTable` | 平台组件 | 否 |
| 弹窗 | `PlatformModal + PlatformEditForm` | 平台组件 | 视字段而定 |

## 完成前检查

```bash
pnpm run check:ai-kit
```

检查结果不是完整质量门禁，但它能快速暴露最常见的 AI 产出偏移：没有用平台组件、页面硬编码间距、绕开 token、缺少 manifest 登记。

## 平台组件变更自动维护 DemoKit

AI 只要新增或修改 `packages/platform-ui/src/**` 里的平台封装组件，就必须同步检查 DemoKit：

- 修改已有组件：确认对应 DemoKit demo 是否自动体现变化。
- 新增组件：补 `apps/demokit/src/demos/*`、`apps/demokit/src/registry.ts`、`docs/ai-kit/components-manifest.json`。
- 新增页面模式：补 `docs/ai-kit/TYPICAL-PAGES.md`。
- 新增 token：补 `docs/ai-kit/DESIGN-SYSTEM.md`。

这条规则默认生效，不需要用户再次说明。
