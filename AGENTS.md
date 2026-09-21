## AI Agent Trellis 强制执行流程

本仓库的开发阶段 AI coding 必须由当前 AI Agent 主动执行 Trellis 流程，而不是把命令或建议转交给用户。即使某些 AI 工具没有 Hooks 功能、Hooks 不稳定，或不能自动注入 `.trellis/workflow.md` 上下文，也不能跳过 Trellis；AI Agent 必须按下面规则自己检查、自己初始化、自己触发对应 skills，并把执行结果反馈给用户。

1. 启动前先检查 Trellis 是否可用：AI Agent 进入本仓库后先检查 `.trellis/` 是否存在，并运行 `trellis --version` 或等价命令确认 CLI 可用；如果缺失，AI Agent 直接执行安装和初始化（如 `npm install -g @mindfoldhq/trellis`、`trellis init -u <开发者名>`，并按实际工具追加 `--codex`、`--cursor`、`--kiro`、`--gemini` 等平台参数）。只有在权限、网络或运行环境阻塞时，才向用户报告具体阻塞点；不要只建议用户“去安装/初始化”。
2. 开始新任务时，AI Agent 必须先加载 `.agents/skills/trellis-start/SKILL.md` 或直接调用 `trellis-start`，读取当前任务、git 状态、工作流阶段、spec 索引和团队规范；没有 skill 调用能力的工具，也必须手动按 `SKILL.md` 中的命令执行，而不是跳过。
3. 新任务涉及实现、重构、构建、文档落库、原型到开发交接、正式前端/后端改造时，AI Agent 必须创建或切换到对应 Trellis task，并维护 `prd.md`。需求不清或存在多种方案时，AI Agent 先执行 `trellis-brainstorm`；正式写代码或改文件前，AI Agent 执行 `trellis-before-dev` 读取相关 `.trellis/spec/` 规范。
4. PM 原型阶段仍保持低门槛：PM 不需要理解 Trellis、Git、Node 或开发任务；但 AI 助手在原型确认后进入开发交接或正式实现时，必须由 AI Agent 自己回到 Trellis 流程并完成必要操作。
5. 结束任务时，AI Agent 必须先执行 `trellis-check` 做规范、lint、type-check、测试和差异检查；如果产生新的工程约定、坑点或可复用模式，AI Agent 执行 `trellis-update-spec` 写回 `.trellis/spec/`；任务完成、提交后，AI Agent 执行 `trellis-finish-work` 归档任务并记录 session。若同类问题反复修复，AI Agent 先执行 `trellis-break-loop` 做复盘。
6. 继续中断任务或发现 AI 跳阶段时，AI Agent 执行 `trellis-continue` 或重新加载 `trellis-start`，以当前 task 状态和 `.trellis/workflow.md` 决定下一步。
7. 参考文档：<https://docs.trytrellis.app/zh/start/how-it-works>、<https://docs.trytrellis.app/zh/start/install-and-first-task>。

## 项目上下文

本仓库现在用于验证一套“AI 原型+开发工作流”：PM 先生成可验证的前端 mock 原型，UI 参与体验修正，开发基于交接包进入正式实现，测试验证作为开发工作流的重要环节同步接入。

它不再以规格驱动开发为主线。当前目标是找到更适合 PM、UI、开发和测试协作的 AI 原型+开发工作流。

- 后端参考底座：`RuoYi-Vue-Plus/`
- 前端参考底座：`ruoyi-plus-vben5/`
- 前端 AI 工程化入口：`ruoyi-plus-vben5/AGENTS.md`、`ruoyi-plus-vben5/docs/ai-kit/`
- PM 原型入口：团队选定的 AI 原型工具
- 主要说明文档：`docs/`
- 项目辅助能力目录：`.agents/skills/`
- 开发工作流治理：`.trellis/`

## 当前原则

1. PM 入口必须保持原型生成的低门槛，不要求 PM 理解开发工具链。
2. PM 不需要理解 Node、依赖安装、模拟服务、API、数据库、Git、脚本或开发框架。
3. AI 助手应根据当前环境灵活判断：复用已有前端或创建轻量前端 mock，并启动本地预览。
4. 原型阶段必须优先保证前端 mock MVP 可运行、可预览、可交互；draft API 协议和 draft 数据库/数据模型不能优先于可预览前端 mock。
5. PM 不需要判断 draft API 协议或 draft 数据库/数据模型设计是否合理，技术草案由开发阶段评审。
6. 固定脚本只能作为可选辅助，不能取代 AI 对当前环境的判断。
7. 原型确认前，不生成正式开发任务，不做最终后端实现设计，不强行输出开发规格。
8. 原型阶段应同步产出 draft API 协议和 draft 数据库/数据模型设计，作为开发交接草案，不作为最终实现承诺。
9. 原型确认后，再固化开发交接包；开发团队根据真实技术栈决定是否进入正式实现，测试团队基于交接包提炼验收和回归检查。
10. Trellis 只用于开发阶段的 AI coding 工作流治理，不作为 PM 原型生成主入口。

## 推荐流程

PM 阶段：

1. PM 用自然语言描述业务目标、用户角色、页面、字段和客户演示诉求。
2. 使用团队选定的原型生成工具生成可运行的前端 mock 原型，并同步起草 draft API 协议和 draft 数据库/数据模型设计。
3. AI 助手自动判断环境，选择合适方式生成或修改前端 mock 页面、模拟数据和预览入口。
4. PM 基于预览继续修改字段、流程、页面和状态。
5. PM 或客户确认原型。

交接阶段：

1. AI 助手输出原型说明、页面清单、字段说明、模拟数据说明和待确认问题。
2. 如果需要开发介入，再补充 draft API 协议、draft 数据库/数据模型设计和实现风险说明。
3. 前后端开发以交接包为输入，结合 `.trellis/spec/` 中的 RuoYi-Vue-Plus 和 ruoyi-plus-vben5 规范，决定正式 API、数据库、权限、租户和工程结构。

## 当前保留的辅助能力

- `.agents/skills/product-prototype/`：面向 PM 原型生成入口的本仓库示例能力。
- `.agents/skills/vue/`：Vue 相关实现建议，可用于轻量原型或正式前端参考。
- `.agents/skills/webapp-testing/`：本地 web 原型启动、浏览器验证和截图检查。
- `.agents/skills/verification-before-completion/`：完成前验证辅助。
- `.agents/skills/trellis-*`：Trellis 为 Codex 提供的开发工作流、任务上下文、检查和规范更新入口。

## Trellis 使用边界

Trellis 已在本仓库初始化，用于开发阶段的 AI coding harness。它管理 PRD、任务上下文、项目记忆、工作流、Codex hooks、agent 配置和 `.trellis/spec/` 团队规范。

当前定位：

- 不把 Trellis 作为 PM 主入口。
- PM 原型阶段仍保持低门槛，不要求 PM 理解 `.trellis/`、Git、Node 或开发任务。
- 原型确认后，开发团队再使用 Trellis 管理任务、规范、上下文和检查。
- `.trellis/spec/prototype/` 已沉淀 PM/UI 可运行前端 Mock MVP 的原型阶段规范，但它仍不要求 PM 直接理解或操作 Trellis。
- `.trellis/spec/backend/` 已按 `RuoYi-Vue-Plus/` 沉淀后端规范。
- `.trellis/spec/frontend/` 已按 `ruoyi-plus-vben5/` 沉淀前端规范。
- `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md` 用于原型到开发交接时对齐前后端契约。

## 正式开发参考框架

后端参考：

- `RuoYi-Vue-Plus/`
- 官方文档：<https://plus-doc.dromara.org/#/ruoyi-vue-plus/home>

前端参考：

- `ruoyi-plus-vben5/`
- 仓库：<https://github.com/imdap/ruoyi-plus-vben5>
- 文档：<https://dapdap.top/guide/quick-start.html>

开发阶段新增或修改代码前，先读取 `.trellis/spec/` 对应规范，优先复用框架既有模式。

前端正式开发规则统一沉淀在 `.trellis/spec/frontend/`：

- `.trellis/spec/frontend/index.md`：前端规范入口和开发前检查。
- `.trellis/spec/frontend/platform-governance.md`：平台组件治理、Vben 布局保护、前端 Mock 开发和交付预览规则。
- `ruoyi-plus-vben5/AGENTS.md` 与 `ruoyi-plus-vben5/docs/ai-kit/`：面向 Codex、Trae 等 AI 工具的前端工程化入口，包含组件注册表、组件选型、设计 token、DemoKit 维护和检查命令。

前端模块卡片长期默认规则补充：

- 所有业务页面、工作台、看板、典型页、DemoKit 和后续新增页面中的模块卡片，默认不使用描边。
- 模块层级统一通过背景、圆角、阴影和间距区分，不得把 `border: 1px solid ...` 当作默认卡片样式。
- 只有在需求明确强调“需要描边”的情况下，才允许给模块卡片增加边框；否则应视为不符合项目默认规范。

设计师、PM、UI 主导的原型 / 展示 / DemoKit 长期规范统一更新 `.trellis/spec/prototype/index.md`；正式前端开发长期规则更新 `.trellis/spec/frontend/`；跨全仓库协作边界的规则再更新根目录 `AGENTS.md`。

### DemoKit 约束

1. 后续维护 `ruoyi-plus-vben5/apps/demokit` 时，凡是现有平台封装组件已经覆盖的标题、区块、表格、描述区、分段切换、文件列表等能力，必须优先直接使用 `@st/platform-ui`，不允许为了赶效果再手写一套页面结构或样式。
2. 如果 DemoKit 需要的能力在现有平台组件中不存在，必须先明确指出缺少哪一个平台能力、影响哪些页面、是否属于共性模式；在用户确认前，不先自行新增临时页面组件替代。
3. 如果 `apps/web-antd` 已经存在符合当前组件能力或页面模式的真实案例，DemoKit 优先直接复用或映射该整体案例，不再额外做一版结构、层级或视觉都不一致的“新示例”。
4. 只有在用户明确提出差异化目标、特殊展示诉求或对比实验需求时，才允许在 DemoKit 中做偏离 `apps/web-antd` 现有案例的差异化设计；此时必须说明差异点和原因。
5. AI 新增或重构前端页面前，必须先读取 `ruoyi-plus-vben5/docs/ai-kit/AI-USAGE.md` 和 `components-manifest.json`，并输出组件映射。

## 文档入口

- `docs/README.md`：文档入口和阅读顺序。
- `docs/01-overview.md`：总体目标和 AI 原型+开发工作流。
- `docs/02-skills.md`：当前保留辅助能力说明。
- `docs/03-directory.md`：目录说明。
- `docs/04-trellis-evaluation.md`：Trellis 在本项目中的定位和使用边界。
- `docs/10-pm-usage.md`：PM 使用说明。
- `docs/11-ui-usage.md`：UI 使用说明。
- `docs/12-dev-handoff.md`：原型确认后的开发交接说明。
