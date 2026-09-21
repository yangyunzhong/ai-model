# 调整原型阶段产出物规范

## 目标

将 `.trellis/spec/prototype/` 中关于 PM/UI 原型产出物的规范调整为与 `.agents/skills/product-prototype/SKILL.md` 一致，明确每个原型必须在 `docs/prototypes/[feature-name]/` 下沉淀固定交接文件。

## 需求

- 参考 `.agents/skills/product-prototype/SKILL.md` 的产出物目录约束。
- 在 prototype spec 中明确以下目录是原型阶段文档交接产物的固定位置：

```text
docs
└── prototypes/
      └── [feature-name]/
          ├── prd.md
          ├── draft-api.md
          ├── draft-data-model.md
          ├── draft-design-dev.md
          └── dev-handoff.md
```

- 明确上述 5 个文件是原型确认和开发交接前的必须产物；信息不完整时也要生成文件，并在文件中标注待确认项。
- 保留可运行前端 Mock MVP 是原型阶段第一质量门槛的规则，不让文档产物替代可预览前端 mock。
- 明确 `.trellis/tasks/<task>/prd.md` 是 Trellis 任务上下文，不替代 `docs/prototypes/[feature-name]/prd.md` 作为原型交接产物。

## 验收标准

- `.trellis/spec/prototype/index.md` 的原型前检查和质量检查能反映固定产物目录。
- `.trellis/spec/prototype/runnable-mock-mvp.md` 的产物契约和承载位置能明确 `docs/prototypes/[feature-name]/` 结构。
- `.trellis/spec/prototype/pm-ui-workflow.md` 的交接清单能要求引用这 5 个文件。
- `.trellis/spec/prototype/trellis-task-lifecycle.md` 能区分 Trellis task 记录与 docs 原型交接产物。
