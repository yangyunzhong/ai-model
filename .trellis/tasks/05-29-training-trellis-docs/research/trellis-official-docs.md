# Trellis 官方文档对照结论

## 来源

- https://docs.trytrellis.app/zh
- https://docs.trytrellis.app/zh/start/how-it-works
- https://docs.trytrellis.app/zh/start/everyday-use
- https://docs.trytrellis.app/zh/advanced/architecture

## 关键点

- Trellis 的官方定位接近“team-level Agent Harness + built-in LLM wiki”：一边控制 AI coding workflow，一边把 spec、task、research、journal 等项目知识保存在仓库文件中。
- 官方强调 auto-injection：支持 hook 的平台自动注入 SessionStart 和每轮 workflow-state；其他平台通过 prelude、skill 或手动入口加载上下文。
- How It Works 的起点不是创建 task，而是会话启动、每轮 workflow-state 注入、当前 turn 分流，然后才进入 task creation。
- Trellis 0.5 之后是 skill-first：多数能力由 auto-trigger skills 或 sub-agents 承接，用户侧常见命令保持很少，主要是 `continue`、`finish-work`，必要平台才有 `start`。
- `implement.jsonl` 和 `check.jsonl` 是窄上下文清单，只列稳定 spec/research 文件；不要把即将修改的源码文件放进去。Inline 模式可以由主会话直接读 task/spec，而不是强制依赖 JSONL 注入。
- `trellis-check` 不只是报告问题，它允许自修并重跑验证。
- `trellis-update-spec` 用于把可复用知识写回 `.trellis/spec/`；task-local facts 留在 `.trellis/tasks/<task>/`，session notes 留在 `.trellis/workspace/<developer>/`。
- work commit 和 `/trellis:finish-work` 是分开的：主会话先提出 commit plan 并提交工作改动，`finish-work` 负责归档 task 和写 journal，不提交功能代码。
- 官方默认任务创建前会询问用户同意；本项目 `.trellis/workflow.md` 已本地化为 PM 原型/开发变更默认进入 task，只有用户显式要求跳过才 inline。培训稿应说明这是项目规则，而不是 Trellis 官方默认。
