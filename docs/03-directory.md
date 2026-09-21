# 目录说明

## 顶层目录

### `RuoYi-Vue-Plus/`

后端参考底座。当前 PM 原型阶段不直接修改这里。

### `ruoyi-plus-vben5/`

前端参考底座。PM 阶段可以参考其风格，也可以在明确的前端 mock 区生成原型；不得为了演示污染正式业务模块。

### `.agents/skills/`

项目技能目录。

当前关注：

- `product-prototype/`
- `vue/`
- `webapp-testing/`
- `verification-before-completion/`
- `trellis-*`

后续新增开发、测试、评审、部署类 skills 时，也放在这里，并同步补充 `docs/` 下的用途说明和培训材料。

### `.codex/`

Codex 项目级配置，由 Trellis 初始化生成。包含 hooks、agent 配置和项目级 Codex 默认设置。

### `.trellis/`

Trellis 原型+开发工作流治理目录。它是团队共享目录，应进入 Git。

主要内容：

- `workflow.md`：Trellis 工作流说明。
- `config.yaml`：Trellis 项目配置。
- `spec/backend/`：RuoYi-Vue-Plus 后端规范。
- `spec/frontend/`：ruoyi-plus-vben5 前端规范。
- `spec/prototype/`：PM/UI 可运行前端 Mock MVP 原型规范，PM 阶段由 AI 助手读取并执行。
- `spec/guides/`：跨层思考和前后端契约指南。
- `tasks/`：原型任务、开发任务、PRD、上下文清单和研究材料。
- `workspace/`：团队可共享的开发会话记录。
- `scripts/`：Trellis 本地运行脚本。

`.trellis/.gitignore` 已自动忽略 `.developer`、`.current-task`、`.runtime/`、`.backup-*`、`*.tmp`、`*.pyc` 等个人指针和运行态文件。不要在项目根 `.gitignore` 中忽略整个 `.trellis/`。

### `docs/`

团队说明文档目录。

当前培训材料：

- `training/01-ppt-storyboard.md`：PPT 脚本和分镜。
- `training/03-speech-script.md`：逐页讲稿。

成品 PPT HTML 后续再根据 `training/01-ppt-storyboard.md` 生成。

### `prototypes/`

可作为可运行前端 mock 的承载目录之一。PM 阶段的目标是直接产出可运行前端，并同步起草 draft API 协议和 draft 数据库/数据模型设计，不是单纯把静态文件写到 `prototypes/`。

推荐结构：

```text
prototypes/
└── [feature-name]/
    ├── README.md
    ├── package.json
    ├── src/
    ├── mock-data/
    ├── prototype-notes.md
    ├── draft-api.md
    ├── draft-data-model.md
    └── dev-handoff.md
```

## 规则

- PM 原型阶段必须由 AI 创建或接续 Trellis 原型 task，并产出可运行前端 mock。
- 每个原型 task 应包含 `prototype.md`，作为来源需求和产物范围的入口说明。
- 可选择 `ruoyi-plus-vben5/` 的明确原型区、独立轻量前端工程或 `prototypes/` 承载目录。
- 不要为了 PM 演示污染正式前后端业务模块；如复用前端底座，必须有清晰的 mock/原型边界。
- 产物必须同步包含模拟数据、页面状态、draft API 协议和 draft 数据库/数据模型设计。
- 原型确认后，再由开发判断是否迁移到 `ruoyi-plus-vben5/` 和 `RuoYi-Vue-Plus/`。
- 新增 skill 时，同步更新 `docs/02-skills.md`、角色使用说明和培训材料。
- 新增或调整开发规范时，同步更新 `.trellis/spec/` 和相关 `docs/` 说明。
