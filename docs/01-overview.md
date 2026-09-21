# AI 原型+开发工作流

## 目标

本仓库当前目标不是只寻找一个原型工具，也不是只寻找一个开发工具，而是沉淀一套适合团队协作的“AI 原型+开发工作流”：先让 PM 和 UI 快速验证可运行前端 mock 原型，再让开发基于交接包继续推进正式工程，测试验证作为开发工作流的重要环节同步接入。

理想状态：

- PM 不懂技术也能用自然语言生成前端 mock 原型。
- AI 能根据当前环境自动判断如何跑起来。
- 原型阶段优先保证前端 Mock MVP 可运行、可预览、可交互。
- 原型能用于客户确认和内部讨论。
- UI 可以继续修正体验和页面状态。
- 原型阶段同步沉淀 draft API 协议和 draft 数据库/数据模型设计。
- 原型确认后，AI 固化开发交接包。
- AI 助手在 PM 原型阶段使用 Trellis prototype spec 和原型 task 约束前端 mock、交接草案和验证记录。
- 开发使用 Trellis task、PRD、check 和 `.trellis/spec/` 接手工程设计、实现和检查。
- 测试基于原型、交接包、原型 task 和开发任务整理验收、回归、验证记录。
- 后续新增开发、测试、部署、评审类 skills 时，持续完善配置文档和培训材料。

## 为什么不以开发规格工具为中心

规格、计划、任务和开发治理工具不适合作为 PM 手工入口，但适合作为 AI 助手在后台持久化原型上下文的工作流底座。

PM 阶段需要的是：

- 快速生成页面
- 快速看到效果
- 快速修改流程和字段
- 能演示给客户
- 不需要理解 API、数据库、任务拆解或工程结构

因此，当前流程不要求 PM 直接使用开发规格工具，也不会把 PM 拉进工程命令和任务拆解里。Trellis 应介入 PM 原型阶段：AI 助手读取 `.trellis/spec/prototype/`，并为会产生或修改原型产物的工作创建或接续 Trellis 原型 task，用它保存 `prd.md`、设计方案、产物位置、验证结果和交接草案，不改变 PM 原型入口的低门槛原则。

## 标准流程

1. PM 用自然语言描述业务目标、用户角色、页面、字段、流程和客户演示诉求。
2. AI 助手自动创建或接续 Trellis 原型 task，并把需求交互沉淀到 `prd.md`。
3. PM 使用团队选定的原型生成工具生成可运行的前端 mock 原型，并同步起草 draft API 协议和 draft 数据库/数据模型设计。
4. AI 助手探测当前环境，复用已有前端或创建轻量前端 mock，并启动本地预览。
5. PM 基于预览继续修改。
6. UI 可继续优化布局、组件状态、空状态、错误态和视觉一致性。
7. PM 或客户确认原型。
8. AI 助手在原型 task 中整理包含 draft API 协议和 draft 数据库/数据模型的开发交接包。
9. 开发团队使用 Trellis task、PRD、check、`.trellis/spec/` 规范和交接包决定正式 API、数据库、权限和工程结构。
10. 测试团队基于原型流程、状态、交接包、原型 task 和开发任务整理验收与回归检查。
11. 团队将新增 skills、案例和踩坑经验回写到配置文档。

## PM 不需要关心什么

- Node、pnpm、npm、yarn
- 依赖安装
- 端口占用
- 模拟服务
- API 路径
- 数据库表
- 后端分层
- Git 分支
- 正式开发任务拆解
- draft API 协议和 draft 数据库/数据模型是否技术合理

## 当前适合保留的能力

- PM 前端 mock 原型生成
- Vue 原型实现参考
- Web 原型启动和浏览器验证
- draft API、draft 数据库/数据模型和开发交接包整理
- Trellis 原型阶段规范、开发阶段任务、上下文和检查治理
- 开发和测试 skills 的持续补充
- 完成前验证和交付检查

## 不放在 PM 原型入口的能力

- 规格驱动开发
- 自动拆正式开发任务
- 后端接口和数据库最终正式设计
- 要求 PM 理解 Trellis 任务、PRD、workflow、spec
- 在原型确认前启动正式后端设计或开发任务

这些能力可以在开发、测试或团队治理阶段单独评估和补齐，但不应要求 PM 在原型入口理解或使用。

## 开发阶段技术基线

原型确认后，正式工程参考以下底座：

- 后端：`RuoYi-Vue-Plus/`，参考 <https://plus-doc.dromara.org/#/ruoyi-vue-plus/home>
- 前端：`ruoyi-plus-vben5/`，参考 <https://github.com/imdap/ruoyi-plus-vben5> 和 <https://dapdap.top/guide/quick-start.html>

Trellis 规范入口：

- `.trellis/spec/backend/`：RuoYi-Vue-Plus 后端开发规范。
- `.trellis/spec/frontend/`：ruoyi-plus-vben5 前端开发规范。
- `.trellis/spec/prototype/`：PM/UI 可运行前端 Mock MVP 的原型阶段规范。
- `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md`：原型到开发交接时的前后端契约规范。
