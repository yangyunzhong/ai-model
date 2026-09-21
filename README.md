# AI 原型+开发工作流验证仓库

本仓库用于验证一套面向团队协作的“AI 原型+开发工作流”：PM 先用自然语言生成可验证原型，UI 参与体验修正，原型确认后再由开发和测试基于交接包进入正式工程实现与验证。

当前重点不是把 PM 拉进开发工具链，而是把 PM 原型入口和开发阶段治理分开：

- PM 阶段保持低门槛：不要求理解 Node、依赖安装、API、数据库、Git、任务拆解或开发框架。
- 原型阶段优先保证 MVP 可运行或可打开，不能让 draft 技术设计阻塞 PM 预览。
- PM 不需要判断 draft API 协议或 draft 数据库/数据模型设计是否合理。
- 原型确认前：不生成正式开发任务，不做最终后端实现设计，不强行输出开发规格；但原型生成/修改本身必须进入 Trellis 原型任务，以保存 PRD、上下文、产物位置和验证记录。
- 原型阶段：同步产出 draft API 设计和 draft 数据库/数据模型设计，作为开发交接草案。
- 原型确认后：固化开发交接包，再由开发团队按真实技术栈判断 API、数据库、权限、租户和工程结构。
- 原型和开发阶段：使用 Trellis 管理 AI workflow、任务上下文、项目记忆和框架规范。

## 技术基线

后端参考底座：

- `RuoYi-Vue-Plus/`
- 官方文档：<https://plus-doc.dromara.org/#/ruoyi-vue-plus/home>

前端参考底座：

- `ruoyi-plus-vben5/`
- 仓库：<https://github.com/imdap/ruoyi-plus-vben5>
- 文档：<https://dapdap.top/guide/quick-start.html>

## Trellis 接入状态

Trellis 已初始化，作为 PM 原型阶段和开发阶段的 workflow harness，但不作为 PM 需要直接操作的原型生成入口。

团队共享目录：

- `.trellis/spec/backend/`：基于 RuoYi-Vue-Plus 的后端开发规范。
- `.trellis/spec/frontend/`：基于 ruoyi-plus-vben5 的前端开发规范。
- `.trellis/spec/prototype/`：PM/UI 可运行 Mock MVP 的原型阶段规范。
- `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md`：原型确认后的前后端契约指南。
- `.trellis/tasks/`：原型任务、开发任务、PRD、上下文和研究材料。
- `.trellis/workspace/`：团队可共享的开发会话记录。

Codex/Trellis 辅助能力：

- `.codex/`：Codex hooks 和 agent 配置。
- `.agents/skills/trellis-*`：Trellis 为 Codex 提供的工作流技能。

`.trellis/` 默认应进入 Git；运行态和个人指针由 `.trellis/.gitignore` 忽略。

## 目录入口

- `AGENTS.md`：仓库级 AI 协作规则。
- `docs/README.md`：文档入口和阅读顺序。
- `docs/01-overview.md`：整体流程说明。
- `docs/02-skills.md`：当前 skills 和 Trellis 能力说明。
- `docs/03-directory.md`：目录说明。
- `docs/04-trellis-evaluation.md`：Trellis 定位和使用边界。
- `docs/10-pm-usage.md`：PM 使用说明。
- `docs/11-ui-usage.md`：UI 使用说明。
- `docs/12-dev-handoff.md`：原型确认后的开发交接说明。

## 推荐流程

1. PM 描述业务目标、用户角色、页面、字段、流程和客户演示诉求。
2. AI 自动创建或接续 Trellis 原型任务，先沉淀设计方案和待确认问题。
3. AI 根据当前环境生成可运行或可打开的原型，并补齐模拟数据和页面状态。
4. PM、UI 和客户基于预览继续修改并确认原型。
5. AI 在原型 task 中输出原型说明、页面清单、字段说明、模拟数据说明和待确认问题。
6. 需要开发介入时，补充 draft API 设计、draft 数据库/数据模型设计、权限/租户/数据权限风险。
7. 开发团队使用 Trellis 和 `.trellis/spec/` 进入正式实现。
8. 测试团队基于原型流程、交接包和开发任务整理验收与回归检查。

## 开发阶段注意事项

开发阶段新增或修改正式代码前，先读取对应 Trellis 规范：

- PM/UI 原型改动：先读 `.trellis/spec/prototype/index.md`。
- 后端改动：先读 `.trellis/spec/backend/index.md`。
- 前端改动：先读 `.trellis/spec/frontend/index.md`。
- 跨前后端契约：先读 `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md`。

PM 原型阶段仍优先写入 `prototypes/` 或独立轻量原型目录，不要为了演示直接污染 `RuoYi-Vue-Plus/` 或 `ruoyi-plus-vben5/`。
