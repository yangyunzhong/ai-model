# 补充 AGENTS Trellis 手动流程要求

## Goal

在根目录 `AGENTS.md` 中补充 Trellis 必装、必初始化，以及无 Hooks AI 工具也必须由当前 AI Agent 显式执行 Trellis skills 的约束，避免 Trellis 流程只依赖平台 Hooks 自动注入或变成对用户的建议。

## Requirements

- 明确进入本仓库开展 AI coding 或原型到开发阶段工作前，AI Agent 必须自行检查、安装并初始化 Trellis。
- 明确没有 Hooks 能力的平台，也要由 AI Agent 手动执行 Trellis 启动和收尾流程。
- 明确 AI Agent 不应只建议用户安装、初始化或运行 Trellis，除非权限、网络或环境确实阻塞。
- 开始新任务时，指向 `trellis-start`、`trellis-brainstorm`、`trellis-before-dev` 等相关 skills。
- 结束任务时，指向 `trellis-check`、`trellis-update-spec`、`trellis-finish-work` 等相关 skills。
- 内容放在 `AGENTS.md` 前部，便于 AI 工具优先读到。

## Acceptance Criteria

- [ ] `AGENTS.md` 顶部包含 AI Agent 自行检查、安装和初始化 Trellis 的要求。
- [ ] `AGENTS.md` 明确说明无 Hooks 工具需要由 AI Agent 手动执行 Trellis skills。
- [ ] `AGENTS.md` 列出开始新任务和结束任务的最小 skill 路由。
- [ ] `AGENTS.md` 保持当前 PM 原型入口和开发阶段边界，不把 Trellis 重新变成 PM 主入口。

## Technical Approach

直接编辑根目录 `AGENTS.md`，在现有“项目上下文”之前增加一个“AI 工具 Trellis 强制流程”章节；不改 `.trellis/` 流程脚本，也不调整已有 skills。

## Out of Scope

- 不改 Trellis hook 脚本。
- 不新增或重命名 skills。
- 不改 PM 原型阶段的低门槛定位。

## Technical Notes

- 官方参考：`https://docs.trytrellis.app/zh/start/how-it-works`
- 本仓库已有 skills：`trellis-start`、`trellis-brainstorm`、`trellis-before-dev`、`trellis-check`、`trellis-update-spec`、`trellis-finish-work` 等。
