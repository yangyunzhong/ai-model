# 后端开发规范

> 适用于本仓库参考后端 `RuoYi-Vue-Plus/`。后续正式后端开发应先按这些约定判断落点，再考虑新增结构。

## 技术底座

- 框架：RuoYi-Vue-Plus 5.x，Spring Boot 3.5，JDK 17/21。
- 认证授权：Sa-Token + JWT，接口权限使用 `@SaCheckPermission`。
- ORM：MyBatis-Plus，优先使用 `BaseMapperPlus<Entity, Vo>`、`LambdaQueryWrapper`、`PageQuery`、`TableDataInfo`。
- 多租户与数据权限：使用框架内置 tenant/data permission 能力，不手写绕过租户条件的 SQL。
- 通用返回：单对象/操作结果使用 `R<T>`，分页列表使用 `TableDataInfo<T>`。
- 常见模块：`ruoyi-admin` 作为启动与 Web 入口，通用能力放在 `ruoyi-common/*`，业务模块放在 `ruoyi-modules/*`。

## 规范索引

| 规范 | 说明 | 使用时机 |
| --- | --- | --- |
| [Directory Structure](./directory-structure.md) | RuoYi-Vue-Plus 模块、包、类命名和新增业务落点 | 新增模块、Controller、Service、Mapper、BO/VO |
| [Database Guidelines](./database-guidelines.md) | MyBatis-Plus、分页、数据权限、租户与 SQL 规则 | 写查询、分页、Mapper、导入导出、删除校验 |
| [Error Handling](./error-handling.md) | `R`、`ServiceException`、参数校验、异常边界 | 接口返回、业务校验、异常处理 |
| [Quality Guidelines](./quality-guidelines.md) | 必须遵守和禁止的后端代码模式 | 代码生成后整理、手写业务逻辑、评审 |
| [Logging Guidelines](./logging-guidelines.md) | 操作日志、业务日志、敏感信息边界 | 新增增删改导入导出、排查问题 |

## 开发前检查

1. 先判断是否已有相近模块：`system`、`demo`、`workflow`、`generator` 中通常已有样例。
2. 常规 CRUD 优先参考 `TestDemoController`、`TestDemoServiceImpl`、`TestDemoMapper` 模式。
3. 新增接口前先确认前端实际页面路径、权限码、菜单/按钮权限和返回结构。
4. 原型确认前不要提前设计正式库表；原型确认后再按交接包补接口意图和数据对象建议。

## 禁止

- 不在 Controller 中堆业务逻辑。
- 不绕过 `R`、`TableDataInfo` 直接返回随意 Map。
- 不为分页手写 PageHelper 风格代码。
- 不在业务 SQL 中手工拼接租户、部门、用户权限条件，优先使用框架注解和插件。
- 不直接修改 `ruoyi-common` 公共能力，除非确认影响范围和回归方式。
