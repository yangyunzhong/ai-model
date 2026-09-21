# Trellis 定位和使用边界

## 结论

Trellis 已在本仓库初始化，并应介入 PM 原型阶段和开发阶段。

它不是要求 PM 直接操作的原型生成器；PM 仍只需要用业务语言说明目标、页面、字段、流程和客户演示诉求。Trellis 的介入方式是：AI 助手在 PM 原型阶段读取 `.trellis/spec/prototype/`，并为会产生或修改原型产物的工作创建或接续原型 task，用它约束可运行前端 mock、Mock 数据、页面状态、draft API 协议、draft 数据库/数据模型和开发交接包。

原型确认前，Trellis 记录原型 task、PRD、上下文、产物和验证结果；原型确认后，Trellis 继续承接开发阶段的 task、PRD、上下文、实现检查、规范更新和团队记忆。

## Trellis 是什么

Trellis 官方定位是团队 AI coding harness。它把原本可能堆在 `AGENTS.md`、`CLAUDE.md`、`.cursorrules` 里的上下文，拆成 `.trellis/` 下的规格、任务、工作区记忆、工作流和平台适配。

官方仓库：<https://github.com/mindfold-ai/Trellis>

它的核心目录包括：

- `.trellis/spec/`
- `.trellis/tasks/`
- `.trellis/workspace/`
- `.trellis/workflow.md`

## 和当前目标的关系

当前目标：

- PM 不懂技术。
- PM 要先生成可运行前端 mock 原型。
- 原型用于客户确认。
- 工具要自动适配环境。
- 不希望 PM 进入开发语境。

Trellis 在本项目中的作用：

- 在 PM 原型阶段提供 prototype spec，约束前端 mock MVP、draft API 协议和 draft 数据模型。
- 在 PM 原型阶段管理原型 task、PRD、产物位置、验证记录和 workspace 记录。
- 在原型确认后管理正式开发 task、PRD、research、实现上下文和检查上下文。
- 让 Codex 在写代码或整理交接包前读取后端、前端、prototype 和跨层契约规范。
- 让团队的 AI 原型和 AI coding 工作更稳定、可追踪、可沉淀。

因此，Trellis 不是 PM 要学习的产品入口，但它应成为 AI 助手执行 PM 原型和开发交接时的规则底座。

## 当前项目接入状态

已接入：

- `.trellis/`：Trellis 工作流、任务、规范和 workspace。
- `.codex/`：Codex hooks 和 agent 配置。
- `.agents/skills/trellis-*`：Codex 可读取的 Trellis skills。
- `.trellis/spec/prototype/`：PM/UI 可运行前端 Mock MVP 的原型阶段规范。
- `.trellis/tasks/<date>-<slug>/prototype.md`：原型 task 的显式入口说明，区分原型产出和正式开发 task。
- `.trellis/spec/backend/`：基于 `RuoYi-Vue-Plus/` 的后端规范。
- `.trellis/spec/frontend/`：基于 `ruoyi-plus-vben5/` 的前端规范。
- `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md`：原型到开发交接的前后端契约指南。

## 适合和不适合

适合：

- PM 原型阶段的规范约束和产物边界检查。
- PM 原型阶段的 task、PRD、产物位置和验证记录沉淀。
- 原型确认后的开发任务管理。
- 团队共享 AI 原型、AI 开发规范和项目记忆。
- 让 Codex 在生成前端 mock、交接包或正式代码前读取对应 spec。
- 管理 PRD、research、实现上下文和检查上下文。

不适合：

- 不适合作为 PM 直接操作的工具界面。
- 不适合要求 PM 理解 Trellis 命令、task、workflow、spec、Git 或 Node。
- 不适合在原型确认前提前生成正式后端设计和开发任务。

## 建议

当前建议：

- Trellis 介入 PM 原型阶段，但由 AI 助手使用，不要求 PM 直接使用。
- PM 阶段聚焦自然语言生成可运行前端 mock、页面、字段、流程、模拟数据、draft API 协议、draft 数据模型和预览。
- AI 助手在 PM 阶段先读取 `.trellis/spec/prototype/`，并创建或接续 Trellis 原型 task 检查产物边界。
- 原型确认后，再由 AI 助手根据交接包创建或接续 Trellis 正式开发任务。
- 开发阶段新增规则时，优先更新 `.trellis/spec/`，再同步必要的 `docs/` 说明。

因此，本仓库的主线仍是“AI 原型+开发工作流”：PM 入口低门槛，Trellis 从原型阶段开始参与规则治理，并在开发阶段继续承接任务、上下文和检查。
