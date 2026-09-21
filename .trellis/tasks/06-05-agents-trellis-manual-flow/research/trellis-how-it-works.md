# Trellis how-it-works research

## Source

- Trellis 官方文档：https://docs.trytrellis.app/zh/start/how-it-works
- Trellis 安装与首个任务：https://docs.trytrellis.app/zh/start/install-and-first-task

## Notes

- Trellis 的关键工作方式是用 hook 在会话开始、任务状态变化和子代理运行前注入上下文。
- 文档强调不同平台支持的 hook 能力不同；没有 hook 或 hook 不稳定的平台，需要用显式指令补齐同等流程。
- 安装初始化应包含全局安装 Trellis CLI、在仓库执行 `trellis init -u <name>`，并按平台选择对应参数。
- 对本仓库而言，`AGENTS.md` 应承担无 hook 平台的入口约束：开始任务前手动加载 `trellis-start`，开发前加载 `trellis-before-dev`，验证和结束阶段加载 `trellis-check`、`trellis-update-spec`、`trellis-finish-work`。
- 安装和初始化要求应写在仓库级入口，避免 AI 工具只读到业务说明却跳过 Trellis 上下文。
