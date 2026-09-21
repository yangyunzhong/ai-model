# 补充 product-prototype skill 完成与交接约束

## 背景

`product-prototype` skill 用于 PM/UI 原型生成与开发交接。当前流程已经要求同步产出 draft API、draft 数据模型和开发交接草案，但对文件级产物 `draft-api.md`、`draft-data-model.md`、draft 级设计开发文档的稳定生成约束不够明确；部分工具也可能无法稳定触发 Trellis 收尾流程。

## 目标

更新 `.agents/skills/product-prototype/SKILL.md`，让后续使用该 skill 的 AI 助手在原型任务完成后稳定执行以下动作：

- 一个原型任务完成后，需要触发 `trellis-finish-work`。
- 当需求已经运行原型并确认可行后，必须生成 `draft-api.md` 和 `draft-data-model.md`。
- 当需求已经运行原型并确认可行后，必须生成 draft 级设计开发文档 `draft-design-dev.md`。
- 三个 draft 文件应作为原型交接草案稳定落盘，并在交接包和 Trellis task 中记录位置。

## 范围

- 修改 `product-prototype` skill 文档。
- 同步更新 `.trellis/spec/prototype/` 中的原型阶段交接产物约束。
- 不修改正式前端、后端或原型代码。
- 不处理用户已有的无关工作区改动。

## 验收

- `SKILL.md` 明确要求完成后触发 `trellis-finish-work`。
- `SKILL.md` 明确要求运行原型并确认可行后稳定生成 `draft-api.md` 和 `draft-data-model.md`。
- `SKILL.md` 明确要求运行原型并确认可行后稳定生成 `draft-design-dev.md`。
- skill 语义保持 PM 低门槛：PM 不需要理解 Trellis、Node、API 或数据库设计。
