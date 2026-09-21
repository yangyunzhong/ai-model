# 沉淀 RuoYi Plus Vben5 框架规范

## 背景

本仓库用于验证 AI 原型+开发工作流，正式开发参考底座为：

- 后端：`RuoYi-Vue-Plus/`
- 前端：`ruoyi-plus-vben5/`

Trellis 初始化后，`.trellis/spec/` 仍是默认占位内容，需要结合当前前后端框架沉淀为可执行的团队规范。

## 目标

1. 将 RuoYi-Vue-Plus 的模块、接口、Service、Mapper、分页、权限、租户、日志和错误处理习惯写入 Trellis 后端规范。
2. 将 ruoyi-plus-vben5 的 Vben5、Vue 3、antdv-next、Alova、VxeGrid、Form、Modal、Pinia 和类型组织习惯写入 Trellis 前端规范。
3. 增加前后端接口契约协作规范，帮助原型确认后进入开发交接。
4. 更新 spec index，让后续 Codex/Trellis 任务能按索引加载相关规范。

## 非目标

- 不修改 `RuoYi-Vue-Plus/` 和 `ruoyi-plus-vben5/` 源码。
- 不引入新的后端架构、前端组件库或 API 风格。
- 不把 PM 原型阶段提前变成后端设计阶段。

## 验收

- `.trellis/spec/backend/` 不再是占位内容，能指导 RuoYi-Vue-Plus 开发。
- `.trellis/spec/frontend/` 不再是占位内容，能指导 ruoyi-plus-vben5 开发。
- `.trellis/spec/guides/` 包含跨层接口契约说明。
- `git diff --check` 通过。
