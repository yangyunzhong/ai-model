---
name: product-prototype
description: PM 产品原型生成入口。用于 PM 或非技术人员用自然语言提出产品需求、业务流程、页面想法、客户演示诉求时，由 AI 助手自动判断本机环境，生成可运行的前端 mock 原型，补齐模拟数据、页面状态、预览入口、draft API 协议、draft 数据库/数据模型、draft 设计开发文档和开发交接包；PM 不需要理解 Node、依赖安装、模拟服务、API、数据库、Git 或开发框架。
---

# PM 前端 mock 原型生成

## 定位

这是 PM 面向 AI 原型工具的第一入口。PM 只表达业务和页面诉求，不需要知道依赖安装、前端工程、接口、数据库或开发流程。

本 skill 负责把自然语言需求变成可运行的前端 mock 原型，并同步产出开发交接所需的 **draft 级 API 协议设计**、**draft 级数据库/数据模型设计** 和 **draft 级设计开发文档**。只要本轮会产生或修改原型产物，它就必须进入 Trellis 原型 task 路径，持久化 `prd.md`、上下文、产物位置和验证记录。它不负责正式开发任务拆解，也不负责最终正式 API、数据库表、前端架构或迁移设计。

## 工作原则

- 先让 PM 看得见、跑得起来，再考虑开发交接。
- 可运行前端 mock 是原型阶段的硬门槛；draft API 协议、draft 数据库设计和 draft 设计开发文档不能优先于可预览前端 mock。
- 由 AI 助手根据当前机器和仓库环境灵活决策，不依赖固定自动化脚本。
- PM 阶段由 AI 助手使用 Trellis prototype spec 约束产物边界，但不要求 PM 理解或操作 Trellis。
- PM 阶段的可运行原型属于 Trellis 原型 task；AI 自动创建、接续和维护 task，PM 不需要感知。
- 优先选择 PM 能马上验证的方案。
- 如果环境缺失，先探测再选择最小可行安装或其他可运行前端 mock 方案。
- 不让 PM 承担技术判断；技术不确定项写入交接包。
- draft API 协议、draft 数据库/数据模型设计和 draft 设计开发文档由 AI 根据业务原型推导，必须标注为草案，开发阶段可以推翻或重做。
- PM 不需要判断 draft API 协议、draft 数据库设计或 draft 设计开发文档是否合理；这些由开发阶段评审。

## 输入识别

当用户提出以下任一诉求时使用本 skill：

- “帮我做一个产品原型”
- “生成一个能给客户看的页面”
- “PM 要用 AI 做需求/页面/原型”
- “做个模拟项目跑起来”
- “根据业务描述生成后台管理页面”
- “我不懂技术，帮我把原型跑起来并产出交接文档”

## 执行流程

1. 读取 `AGENTS.md`、`docs/01-overview.md`、`docs/02-skills.md`、`docs/10-pm-usage.md` 和 `.trellis/spec/prototype/index.md`，让 Trellis prototype spec 介入 PM 原型阶段的产物约束。
2. 先按 `.trellis/spec/prototype/trellis-task-lifecycle.md` 检查 Trellis task：
   - 如果本轮只是解释、查询或不写文件，可以直接回答。
   - 如果本轮会生成或修改可运行原型、Mock 数据、说明文档、draft API、draft 数据模型、draft 设计开发文档或交接包，必须创建或接续 `.trellis/tasks/` 下的原型 task。
   - 如果没有 active task，用 `python ./.trellis/scripts/task.py create "<title>" --slug "prototype-<feature-name>"` 创建原型 task。
   - 如果已有 active task 但明显不是本次原型任务，不要把原型写进无关 task；创建或切换到独立原型 task，并在回复中简短说明“我会把这个原型作为独立 Trellis 任务记录”。
   - 在需求交互阶段维护当前 task 的 `prototype.md` 和 `prd.md`：`prototype.md` 作为原型入口说明，`prd.md` 记录设计方案、待确认问题和原型验收条件，不写成正式开发实现计划。
3. 判断当前处于哪个阶段：
   - 需求交互 / 方案确认阶段：用户还在讨论模块、页面、流程、功能、字段、状态或体验，尚未确认执行。
   - 执行 / 交接草案阶段：用户已经明确要求生成、修改、继续实现或确认方案执行。
4. 需求交互 / 方案确认阶段只输出设计方案：
   - 模块范围、页面清单、用户角色、核心功能、关键字段、关键状态、交互动线和 PM/UI 待确认问题。
   - 不展开 Mock 数据文件路径、组件映射、代码文件落点、draft API、draft 数据库/数据模型或 draft 设计开发文档。
   - 结尾说明“确认后再补可运行前端 Mock、Mock 数据、draft API、draft 数据模型和 draft 设计开发文档”。
5. 执行 / 交接草案阶段再用业务语言整理 PM 输入，识别模块、角色、页面、字段、状态、操作和验收场景。
6. 如缺少关键业务信息，只问少量 PM 能回答的问题；不要问技术实现问题。
7. 探测当前环境和仓库：
   - 查找可用 Node、包管理器、前端启动脚本、端口占用、已有模拟数据和前端 mock 承载位置。
   - 优先复用已有前端或轻量前端 mock 工程。
   - 可参考 `ruoyi-plus-vben5/` 的后台风格，但不要默认污染正式前端工程。
8. 根据环境选择执行方式：
   - 有合适前端环境时，创建或更新可运行前端 mock。
   - 如果使用 `prototypes/`，也必须将其作为可运行前端 mock 工程承载位置。
   - 如果当前方案跑不起来，改选另一种可运行前端 mock 方案，不用静态文件替代 PM 阶段产物。
9. 生成模拟数据、页面状态和核心交互：
   - 至少覆盖与需求相关的列表、详情、新增/编辑、空状态、加载态、错误态、无权限或禁用态。
   - 字段命名要稳定，便于 PM、UI 和开发沟通。
   - 如果原型写入 `ruoyi-plus-vben5/`，mock 数据和前端模拟服务统一放到 `apps/web-antd/src/mock/modules/<domain>/`，页面只引用 `#/mock/modules/...`。
10. 启动或准备预览：
   - 启动本地服务并给出 URL。
   - 默认只做轻量验证：确认服务可启动或 URL 可访问，记录 PM 可手动检查的主流程。
   - 不默认运行 Playwright、浏览器自动化、批量截图、多视口截图或视频录制。
   - 只有用户明确同意，或 AI 发现明显页面空白/布局/关键流程风险并先询问用户后，才运行自动化浏览器验证。
   - 询问用语保持简单：“是否需要我跑 Playwright 自动化验证并保存截图？会更耗时；不跑的话我给你预览地址和手动检查路径。”
   - 如果当前方案跑不起来，先修复或切换到另一个可运行前端 mock 方案，再记录环境限制。
11. 原型已经运行并确认可行后，必须稳定生成交接草案文件：
   - 必须生成 `prd.md`、`draft-api.md`、`draft-data-model.md`、`draft-design-dev.md` 和 `dev-handoff.md`，不要只在聊天回复、`README.md`、`prototype-notes.md` 或单个 `dev-handoff.md` 中口头描述。
   - 固定生成目录为 `docs/prototypes/[yy-MM-dd]-[feature-name]/`；`[yy-MM-dd]` 使用当前日期的两位年份、月份、日期，例如 `26-06-02-personnel-info/`。
   - 可运行前端 mock 工程可以承载在 `ruoyi-plus-vben5/`、`prototypes/` 或其他轻量工程目录中，但交接文档仍统一放到 `docs/prototypes/[yy-MM-dd]-[feature-name]/`。
   - 信息不完整时也要生成文件，用“待确认”章节标注缺口；不要因为 draft 不完美而跳过。
   - draft 文件必须明确标注为草案，不代表正式 API、数据库表、权限、租户、前端架构或后端实现已确定。
12. 整理原型交接包：
   - 原型预览地址
   - 页面清单
   - 字段说明
   - 模拟数据说明
   - 关键流程说明
   - `draft-api.md`：候选接口、方法、路径、请求参数、响应结构、错误状态和权限/租户提示
   - `draft-data-model.md`：候选实体、表、字段、关系、状态枚举、索引和数据权限风险
   - `draft-design-dev.md`：页面结构、用户流程、关键状态、前端 mock 实现说明、组件/交互建议、开发拆分建议、验收点和实现风险
   - PM 待确认问题
   - 开发介入时需要重新判断的事项，明确 draft 不等于正式实现方案
13. 更新当前 Trellis 原型 task：
   - 确保 `prototype.md` 存在，并清晰标识这是原型 task、来源需求和产物边界。
   - 在 `prd.md` 或 task 说明中记录已确认的设计方案、产物位置、预览入口、验证结果、`draft-api.md`、`draft-data-model.md`、`draft-design-dev.md` 和待确认问题。
   - 如果有 research、用户同意生成的截图或交接文档，保存在 task 目录或在 task 中指向实际文件。
   - 如果未运行 Playwright 或浏览器自动化，记录为“轻量验证，未运行自动化浏览器截图”。
14. 一个 Trellis 原型任务完成后，必须触发 `trellis-finish-work`：
   - 不要只依赖 `/trellis:finish-work` 斜杠命令；部分工具不能稳定触发 Trellis。
   - 优先加载并执行 `$trellis-finish-work` skill；如果当前工具不支持 skill 触发，直接读取 `.agents/skills/trellis-finish-work/SKILL.md` 并按步骤收尾。
   - 只有质量验证、产物记录、必要提交或用户确认的手工提交边界都处理完后，才声明 task 完成。
   - 如果因为未提交、未验证或用户未确认而不能收尾，明确记录阻断原因和下一步，不要假装已经触发完成流程。

## 环境决策规则

AI 助手应动态判断，不写死一种安装方式：

- 优先使用仓库已有 `package.json`、lockfile、脚本和包管理器。
- 优先使用用户本机已有工具；缺失时再考虑用户级安装。
- 不能使用需要管理员权限的安装作为默认路径。
- 遇到企业代理、网络失败、权限限制时，改用可离线或轻量的方案。
- 不因为某个工具缺失就停止；先改选能运行的前端 mock 方案，再记录限制。
- 不因为 draft API 协议、draft 数据库设计或 draft 设计开发文档尚不完美而阻塞 PM 预览；技术草案交给开发评审。

常见判断：

- 有可用 Node 和前端脚本：创建或复用可运行前端 mock。
- 有 Node 但依赖缺失：按项目 lockfile 选择 `pnpm`、`npm` 或 `yarn` 安装。
- 无可用 Node：优先寻找仓库既有前端运行方式、内置运行时或用户级可安装方案；不能启动前端时，不声明 PM 原型已完成。
- 端口被占用：自动选择新端口并告知 PM。

## 产出物

必须尽量产出：

- 可运行的前端 mock PM 演示原型
- 模拟数据
- 页面、字段、状态、操作说明
- `draft-api.md`：draft API 协议设计
- `draft-data-model.md`：draft 数据库/数据模型设计
- `draft-design-dev.md`：draft 级设计开发文档
- PM 待确认问题
- 开发交接说明

稳定生成规则：

- 原型运行并确认可行后，`draft-api.md`、`draft-data-model.md` 和 `draft-design-dev.md` 是必交付文件。
- 如果业务细节仍缺失，文件中保留“待确认”项，而不是跳过生成。
- `dev-handoff.md` 必须引用这三个文件的位置，避免交接时只剩聊天记录。
- 原型交接文档固定生成在 `docs/prototypes/[yy-MM-dd]-[feature-name]/`；`[yy-MM-dd]` 使用当前日期，例如 `26-06-02-personnel-info/`。

推荐目录：

```text
docs
└── prototypes/
      └── [yy-MM-dd]-[feature-name]/
          ├── prd.md
          ├── draft-api.md
          ├── draft-data-model.md
          ├── draft-design-dev.md
          └── dev-handoff.md
```

## PM 交互规则

只问 PM 能回答的问题，例如：

- 这个功能服务哪个业务场景？
- 哪些角色会使用？
- 客户最想先看哪条流程？
- 页面上必须出现哪些字段？
- 哪些状态或按钮是必须演示的？

需求交互阶段的回复必须围绕设计确认：

- 模块范围。
- 页面和功能。
- 用户流程。
- 字段和状态。
- 体验与演示重点。
- PM/UI 待确认问题。

不要在 PM 还没确认方案时输出：

- Mock 数据目录和文件名。
- 组件映射和代码落点。
- draft API 路径、方法、请求/响应结构。
- draft 数据库表、字段类型、索引和关系。

不要问 PM：

- 用哪个前端框架？
- 接口路径怎么定？
- 表怎么建？
- 字段类型用什么？
- Controller、Service、Mapper 放哪里？

draft API 协议、draft 数据库/数据模型设计和 draft 设计开发文档由 AI 根据页面、字段、流程和状态推导，不要求 PM 直接设计。
PM 也不需要判断这些 draft 技术草案是否合理，只确认业务、页面、字段、流程和状态。

## 与开发阶段的关系

- 本 skill 是 PM 原型入口。
- 本 skill 进入 Trellis 原型 task 路径，但不要求 PM 理解或手工操作 Trellis。
- 原型阶段应产出 draft API 协议、draft 数据库/数据模型设计和 draft 级设计开发文档，作为交接草案。
- 原型确认前，可以创建原型 task 保存需求、上下文和产物记录；不拆正式开发任务，不把 draft API、draft 数据库设计或 draft 设计开发文档当成最终方案。
- 原型确认后，固化交接包供开发评审。
- 开发团队基于交接包重新判断正式工程方案。
