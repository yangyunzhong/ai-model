# 开发交接说明

## 什么时候开发和测试介入

PM 原型阶段不要求开发提前设计正式工程方案。PM 或客户确认原型之后，开发和测试应基于交接包正式介入。

开发介入前应具备：

- 可运行、可预览的前端 mock 原型
- 页面清单
- 字段说明
- 模拟数据
- draft API 协议设计
- draft 数据库/数据模型设计
- 核心流程
- 主要状态
- 待确认问题
- Trellis 原型 task 记录，包括 `prd.md`、产物位置、预览验证和已知限制

测试介入前还应尽量具备：

- 主流程和分支流程
- 关键状态和异常状态
- 角色或权限差异
- 客户演示路径

## 交接包内容

建议包含：

- `prototype-notes.md`：原型说明
- `mock-data/`：模拟数据
- `draft-api.md`：draft API 协议设计
- `draft-data-model.md`：draft 数据库/数据模型设计
- `dev-handoff.md`：开发交接说明
- 页面截图或预览地址

## 开发需要重新判断什么

开发阶段重新判断：

- 正式 API
- 正式数据库
- 权限设计
- 租户隔离
- 数据权限
- 前端工程路径
- 后端模块归属

PM 原型不直接等于正式实现方案。
draft API 协议和 draft 数据库/数据模型是原型阶段的交接草案，开发需要评审其技术合理性，不能要求 PM 负责判断。

## 开发参考规范

正式开发参考底座：

- 后端：`RuoYi-Vue-Plus/`
- 前端：`ruoyi-plus-vben5/`

Trellis 已沉淀对应规范：

- `.trellis/spec/prototype/`：PM/UI 可运行前端 Mock MVP 的页面、Mock 数据、状态、预览和交接边界。
- `.trellis/spec/backend/`：Controller、Service、Mapper、BO、VO、分页、权限、租户、数据权限、日志和错误处理。
- `.trellis/spec/frontend/`：`apps/web-antd` 目录、Vben5、Vue 3、antdv-next、Alova、VxeGrid、Form、Modal、Pinia 和类型组织。
- `.trellis/spec/guides/ruoyi-plus-vben5-contract-guide.md`：列表、详情、新增、编辑、删除、导入、导出、权限码、BO/VO 和前端 API 类型的对齐规则。

PM 原型阶段，AI 助手已应读取 `.trellis/spec/prototype/` 并创建或接续 Trellis 原型 task，用它约束前端 mock、交接草案和验证记录。开发介入时，AI 助手继续读取 backend/frontend/guides 规范，再根据交接包创建或接续正式开发 Trellis 任务。

## 测试需要整理什么

测试阶段应基于原型、交接包和原型 task 整理：

- 验收用例
- 主流程冒烟路径
- 异常和边界场景
- 权限和数据范围检查
- 回归清单
- 验证结果记录

后续新增测试验证类 skills 时，应把输入物料、产出格式和完成前检查同步写入本文档和培训材料。
