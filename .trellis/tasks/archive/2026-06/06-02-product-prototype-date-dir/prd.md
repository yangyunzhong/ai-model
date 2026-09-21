# 补充 product-prototype 生成目录命名

## 目标

更新 `.agents/skills/product-prototype/SKILL.md` 中原型产出物的生成目录说明，将交接文档目录从 `[feature-name]/` 调整为 `[yy-MM-dd]-[feature-name]/`。

## 需求

- 在 `docs/prototypes/` 推荐目录中使用 `[yy-MM-dd]-[feature-name]/`。
- 补充目录命名规则，说明日期前缀使用两位年份、月份、日期，例如 `26-06-02-personnel-info/`。
- 目录规则用于原型文档/交接产物生成目录，不改变 Trellis task slug 的 `prototype-<feature-name>` 创建规则。
- 保持 `prd.md`、`draft-api.md`、`draft-data-model.md`、`draft-design-dev.md` 和 `dev-handoff.md` 五个文件不变。

## 验收标准

- `.agents/skills/product-prototype/SKILL.md` 的推荐目录从 `[feature-name]/` 改为 `[yy-MM-dd]-[feature-name]/`。
- skill 中能明确 `[yy-MM-dd]` 的含义和示例。
- 不修改当前工作树中无关的原型页面、mock 数据或 `docs/prototypes/` 产物。
