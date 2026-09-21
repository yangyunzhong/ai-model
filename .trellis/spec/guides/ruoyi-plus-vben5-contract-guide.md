# RuoYi Plus Vben5 前后端契约指南

> 用于原型确认后，把页面、字段和交互整理成 RuoYi-Vue-Plus + ruoyi-plus-vben5 可实现的开发交接。

## 适用场景

- PM 原型已确认，需要开发介入。
- 一个功能同时涉及后端接口、数据库对象、权限菜单和前端页面。
- AI 需要从可运行前端 mock 原型迁移到正式前后端实现。

## 先确认边界

开发前必须明确：

1. 页面路径和菜单归属。
2. 角色、按钮权限和数据权限要求。
3. 列表字段、查询字段、表单字段、详情字段。
4. 是否需要导入、导出、批量删除、状态切换。
5. 是否涉及租户隔离、部门/用户数据权限、文件上传、字典、加密字段。
6. 是否只是原型演示，还是进入正式工程实现。

## 后端接口形态

常规 CRUD 按 RuoYi-Vue-Plus 习惯设计：

| 动作 | HTTP | 路径示例 | 后端返回 | 前端调用 |
| --- | --- | --- | --- | --- |
| 分页列表 | GET | `/system/user/list` | `TableDataInfo<Vo>` | `alovaInstance.get<PageResult<T>>` |
| 详情 | GET | `/system/user/{id}` | `R<Vo>` | `alovaInstance.get<T>` |
| 新增 | POST | `/system/user` | `R<Void>` | `postWithMsg<void>` |
| 编辑 | PUT | `/system/user` | `R<Void>` | `putWithMsg<void>` |
| 删除 | DELETE | `/system/user/{ids}` | `R<Void>` | `deleteWithMsg<void>` |
| 导出 | POST | `/system/user/export` | `Blob` | `commonExport` |
| 导入 | POST | `/system/user/importData` | 原始响应/消息 | `FORM_DATA` |

## 字段映射

- 后端 Entity 字段不等于前端可见字段，正式接口以 VO 为准。
- 新增/编辑入参以 BO 为准，不让前端提交无关 Entity 字段。
- 前端 `model.d.ts` 应贴近 VO/BO，而不是贴近数据库表。
- Long/雪花 ID 在前端使用通用 `ID`，不要强行 number 化。

## 权限契约

- 后端 `@SaCheckPermission("module:resource:action")`。
- 前端按钮 `v-access:code="['module:resource:action']"`。
- 菜单、按钮、接口三者权限码必须同源。
- 前端权限只负责显示隐藏，后端权限才是安全边界。

## 分页与查询

- 前端 VxeGrid 查询把 `currentPage` / `pageSize` 映射为后端 `pageNum` / `pageSize`。
- 查询字段对应 BO 的 QueryGroup 字段。
- 时间范围可使用 `params.beginXxx` / `params.endXxx` 模式。
- 列表返回对应前端 `PageResult<T>`。

## 字典、状态和枚举

- 稳定系统字典优先走后端字典和前端 `DictEnum` / `getDictOptions`。
- 页面临时枚举只允许在原型阶段存在，进入正式实现时要说明是否转为字典。
- 状态切换接口需要独立权限和操作日志。

## 导入导出

- 导出后端使用 `ExcelUtil.exportExcel`，前端使用 `commonExport`。
- 导入后端使用 `MultipartFile` + Excel 工具，前端使用 `ContentTypeEnum.FORM_DATA`。
- 导入模板下载要明确是否返回 Blob、是否跳过统一响应转换。

## 租户与数据权限

- 只要数据属于组织、部门、用户或租户范围，必须判断是否需要 `@DataPermission`。
- 跨租户操作必须说明业务原因，并优先使用 `TenantHelper.dynamic` 等框架能力。
- 前端不要通过隐藏查询条件来替代后端数据权限。

## 原型到开发交接输出

交接包至少包含：

1. 页面清单与路由建议。
2. 字段表：字段名、中文名、展示/查询/编辑/必填/字典/校验。
3. 接口意图：列表、详情、新增、编辑、删除、导入、导出、状态切换。
4. 数据对象建议：BO、VO、可能的 Entity 字段。
5. 权限建议：菜单权限、按钮权限、数据权限。
6. 风险：租户、数据权限、文件、加密、工作流、第三方服务。

## 禁止

- 禁止原型未确认就输出正式库表设计。
- 禁止让前端 mock 字段直接变成数据库字段。
- 禁止后端返回 Entity 给前端。
- 禁止前端绕开 `alovaInstance` 自写 fetch/axios。
- 禁止只做按钮隐藏而缺少后端 `@SaCheckPermission`。
