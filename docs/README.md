# 文档入口

本目录说明团队如何使用 AI 做“AI 原型+开发工作流”。

当前方向：

1. PM 先用自然语言生成可验证的前端 mock 原型。
2. AI 根据当前环境灵活选择运行方式。
3. PM、UI 和客户基于原型确认需求。
4. 原型确认后，再整理开发交接包。
5. AI 助手在 PM 原型阶段使用 Trellis prototype spec 和原型 task 约束前端 mock、交接草案和验证记录。
6. 开发团队继续使用 Trellis task、PRD、check 和 `.trellis/spec/` 推进正式工程实现。
7. 测试团队基于交接包、原型 task 和开发任务整理验收、回归和验证记录。
8. 后续新增开发、测试、评审、部署类 skills 时，同步完善配置文档和培训材料。

## 推荐阅读顺序

- [01-overview.md](01-overview.md)：总体目标和流程。
- [02-skills.md](02-skills.md)：当前保留辅助能力和使用边界。
- [03-directory.md](03-directory.md)：目录说明。
- [04-trellis-evaluation.md](04-trellis-evaluation.md)：Trellis 定位、目录和使用边界。
- [10-pm-usage.md](10-pm-usage.md)：PM 如何使用。
- [11-ui-usage.md](11-ui-usage.md)：UI 如何参与原型。
- [12-dev-handoff.md](12-dev-handoff.md)：原型确认后如何交给开发。
- [training/01-ppt-storyboard.md](training/01-ppt-storyboard.md)：培训 PPT 脚本和分镜，也是后续生成 HTML 演示文稿的源头。
- [training/03-speech-script.md](training/03-speech-script.md)：培训讲稿。

后续需要成品 PPT HTML 时，先以 `training/01-ppt-storyboard.md` 为准生成，再与讲稿做一致性校验。

## 最短路径

PM 只需要说明业务目标、页面、字段、流程和客户演示诉求。工具安装、依赖处理、前端 mock 运行和环境适配方案由 AI 根据环境处理。

PM/UI 原型阶段由 AI 助手按 `.trellis/spec/prototype/` 创建或接续原型 task，约束可运行前端 Mock MVP、draft API 协议和 draft 数据库/数据模型的产物边界。`prototypes/` 可以作为可运行前端 mock 的承载目录之一，但 PM 阶段不是单纯产出静态文件；开发和测试在原型确认后接收交接包。正式实现阶段，开发团队继续按 `.trellis/spec/backend/`、`.trellis/spec/frontend/` 和 `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md` 对齐 RuoYi-Vue-Plus、ruoyi-plus-vben5、接口契约、权限、租户和验证要求。
