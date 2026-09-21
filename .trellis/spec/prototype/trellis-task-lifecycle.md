# 原型阶段 Trellis Task 生命周期

## 目标

PM/UI 原型阶段只要会产生或修改可运行前端 Mock MVP、原型说明、Mock 数据、draft API、draft 数据模型或交接包，就必须进入 Trellis task 路径。

这里的 task 是**原型任务**，用于持久化需求、设计方案、上下文、产物和验证记录；它不是正式开发任务，也不代表已经确认最终 API、数据库、权限、租户或后端实现方案。

## 什么时候创建或接续 task

必须创建或接续 Trellis task：

- PM 要生成一个新的可运行原型。
- PM/UI 要修改已有原型的页面、字段、流程、状态或交互。
- 需要启动本地预览、改前端 mock、整理交接包或生成 draft 技术草案。
- 当前对话会产生文件、可运行产物、验证记录或后续团队需要接续的上下文。

可以不创建 task：

- 只解释 Trellis、原型规范或已有文件，不写文件、不产生产物。
- 一次性简单问答、查询或闲聊。
- 用户本轮明确要求跳过 Trellis。

## 任务内容

原型 task 的 `prd.md` 应记录 PM/UI 能确认的内容：

- 业务目标和演示目标。
- 模块范围和不做范围。
- 页面清单、角色、核心功能、关键字段。
- 用户流程、页面状态、异常状态和待确认问题。
- 可运行前端 Mock MVP 的验收条件。
- 原型确认后需要输出的交接草案范围。

在 PM/UI 确认设计方案前，不把 `prd.md` 写成开发实现计划，不展开正式 API、数据库表、权限码、租户字段、Controller、Service 或 Mapper。

注意区分两类 `prd.md`：

- `.trellis/tasks/<date>-<slug>/prd.md`：Trellis task 的上下文记录，用于保存需求、验收条件、产物位置和验证记录。
- `docs/prototypes/[feature-name]/prd.md`：原型交接产物，必须随 `draft-api.md`、`draft-data-model.md`、`draft-design-dev.md` 和 `dev-handoff.md` 一起生成，供 PM、UI、开发和测试接续。

每个原型 task 还应有一份 `prototype.md` 作为入口说明，写清楚：

- 这是原型 task，不是正式开发 task。
- 来源需求是什么。
- 产物有哪些。
- 预览入口或验证入口是什么。
- 哪些文件属于这个原型 task，以及 `docs/prototypes/[feature-name]/` 下固定交接产物的位置。

## 执行阶段记录

进入执行后，task 中需要沉淀：

- 原型承载位置和预览方式。
- 关键页面、Mock 数据和状态覆盖说明。
- `docs/prototypes/[feature-name]/prd.md`、`draft-api.md`、`draft-data-model.md`、`draft-design-dev.md` 和 `dev-handoff.md` 的位置。
- 本地验证命令、浏览器验证结果和已知限制。
- PM/UI 待确认问题和开发阶段风险。

如果当前原型写入 `ruoyi-plus-vben5/`，task 上下文必须指向 `prototype/` 和 `frontend/` 相关规范；如果后续进入正式开发，再创建或接续正式开发 task。

## 边界

- PM 不需要知道 Trellis task、Git、Node、API 或数据库。
- AI 助手负责自动创建、接续、更新和记录 task。
- 原型 task 可以在原型确认前存在；正式开发 task 只能在原型确认后进入。
- `.trellis/tasks/<date>-<slug>/prototype.md` 应作为原型产物的显式入口文件。
- `docs/prototypes/[feature-name]/` 下的 5 个文件是原型文档交接硬产物，不能只留在 `.trellis/tasks/`、`README.md`、`prototype-notes.md` 或聊天回复里。
- draft API 和 draft 数据模型是交接草案，不是最终实现承诺。
- 原型结束后，如果当前 task 不适合归档，至少用 `add_session.py` 记录 workspace journal。
