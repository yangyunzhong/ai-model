# 优化培训材料 Trellis 讲解

## 目标

优化 `docs/training/01-ppt-storyboard.md` 和 `docs/training/03-speech-script.md` 中 Trellis 相关内容，让分享稿更贴近 Trellis 官方文档，同时保持本项目“AI 原型+开发工作流”的本地化定位。

## 范围

- 强化 Trellis 作为团队级 Agent Harness 和内置 LLM wiki 的定位。
- 补充 SessionStart、per-turn workflow-state、active task、Skill-first、上下文清单、finish-work 边界等官方重点。
- 明确本项目对官方默认流程的本地化规则：PM 原型和开发变更默认进入 Trellis task，PM 侧仍不需要理解 Trellis 操作。
- 同步修改分镜和讲稿，保持页码、主线和口播一致。
- 按 4 个大章节重排整体页序，不压缩 36 页规模：背景与问题、方法概念与工具、工作流核心、角色协作与实战。

## 非目标

- 不修改代码、脚本、原型或 `.trellis/spec/` 规范。
- 不重写整套分享结构。
- 不把材料改成 Trellis 官方工具教程。

## 验收标准

- 两份 training 文档中的 Trellis 段落都体现“自动注入、任务状态、Skill-first、持久知识、finish-work 边界”。
- PM/UI 的低门槛边界不被削弱。
- 开发/测试能更清楚 Trellis 在本项目中的执行节奏和文件落点。
- 36 页页数保持不变，并形成“背景与问题 -> 方法概念与工具 -> 工作流核心 -> 角色协作与实战”的章节顺序。
- Markdown 基本结构正常，通过轻量文本检查。
