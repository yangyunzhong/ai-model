# skills 能力说明

本仓库的 skills 服务“AI 原型+开发工作流”。PM 不需要直接理解或调用 skill 名称，AI 助手应根据用户目标和当前环境选择合适能力。后续新增开发、测试、评审、部署类 skills 时，本文件需要同步补充用途、输入物料、产出物和边界。

## PM 前端 mock 原型生成

用途：

- PM 用自然语言生成前端 mock 原型。
- AI 助手自动判断当前环境。
- AI 助手自动创建或接续 Trellis 原型 task，记录 `prd.md`、设计方案、产物位置和验证结果。
- 生成可运行、可预览、可交互的前端 mock 页面。
- 保证 MVP 能运行和预览，不能只交付说明、静态文件或技术草案。
- 生成模拟数据和页面状态。
- 启动预览并给出本地 URL。
- 同步整理 draft API 协议和 draft 数据库/数据模型设计。
- 原型确认后固化开发交接包。

PM 输入：

- 业务目标
- 用户角色
- 页面想法
- 字段和状态
- 核心流程
- 客户演示重点

AI 输出：

- 前端 mock 原型页面
- Trellis 原型 task 记录
- 预览地址
- 模拟数据
- 页面说明
- draft API 协议设计
- draft 数据库/数据模型设计
- 待确认问题
- 开发交接包

draft API 协议和 draft 数据库/数据模型只作为开发交接草案。PM 不需要判断协议、字段类型、索引或表关系是否合理。

## 设计和前端辅助能力

### Vue

用于生成 Vue/Vite 轻量原型，或参考 ruoyi-plus-vben5 的前端风格。

### webapp-testing

用于启动本地 web 原型、打开浏览器、检查页面是否可见、是否有明显布局问题。PM 原型阶段默认不自动运行 Playwright 或批量截图；只有用户明确同意，或 AI 先询问并获得确认后才执行自动化浏览器验证。

### verification-before-completion

用于要求 AI 在声明完成前先做必要验证，例如检查文件是否存在、前端 mock 是否可运行、构建或静态检查是否通过、产物之间是否一致。

## Trellis 原型+开发工作流能力

Trellis 已在本仓库初始化，相关能力由 `.agents/skills/trellis-*` 和 `.trellis/` 共同提供。它应在 PM 原型阶段介入规范约束和原型 task 管理，在开发阶段介入正式开发 task、PRD、实现检查和经验沉淀。

用途：

- 管理 PM 原型阶段的 prototype spec、原型 task、产物边界和交接草案。
- 管理开发阶段任务、PRD、上下文和检查。
- 将团队长期工程规范沉淀到 `.trellis/spec/`。
- 让 Codex 在写代码前读取后端、前端和跨层契约规范。
- 将开发会话记录到 `.trellis/workspace/`，方便团队协作和接续。

当前规范：

- `.trellis/spec/backend/`：基于 `RuoYi-Vue-Plus/` 的后端规范。
- `.trellis/spec/frontend/`：基于 `ruoyi-plus-vben5/` 的前端规范。
- `.trellis/spec/prototype/`：PM/UI 可运行前端 Mock MVP 的原型阶段规范。
- `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md`：原型确认后的前后端接口契约和交接规则。

边界：

- Trellis 不是 PM 需要直接操作的原型生成工具，但 AI 助手在 PM 原型阶段必须使用 `.trellis/spec/prototype/` 并维护原型 task。
- PM 不需要理解 Trellis 命令、任务目录、hooks 或 Git。
- 原型确认前，不因为 Trellis 存在就提前生成正式后端设计和开发任务。

## 后续开发和测试能力

本仓库后续会继续加入开发、测试和交付相关 skills。每个新增 skill 都需要补充：

- 面向角色：PM、UI、前端、后端、测试或项目负责人。
- 输入物料：它需要读取或接收什么。
- 产出物：它应该生成什么文件、说明或验证结果。
- 使用边界：它不应该替代谁的判断。
- 完成检查：它在说完成前应该验证什么。

优先补齐方向：

- 前端开发 skill：组件、路由、权限、状态管理、构建检查。
- 后端开发 skill：接口、实体、权限、租户、数据权限、事务边界。
- 测试验证 skill：用例、边界、回归、浏览器验证、验收记录。
- 评审和部署 skill：代码评审、交接包评审、环境检查、日志和回滚说明。

## 使用边界

PM 原型入口不要求 PM 理解开发规格、任务拆解、后端设计和工程治理。AI 可以在后台维护 Trellis 原型 task，但不能把它变成 PM 的操作负担。原型确认后，正式开发和测试能力可以逐步接入，但必须保留人工评审和正式工程判断。
