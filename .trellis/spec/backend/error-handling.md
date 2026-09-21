# 后端错误处理规范

## 返回结构

- 普通接口返回 `R<T>`。
- 操作类接口返回 `R<Void>`，Controller 中使用 `toAjax(boolean)` 转换成功/失败。
- 分页接口返回 `TableDataInfo<Vo>`。
- 导出接口直接写 `HttpServletResponse`，不再包一层 `R`。

## 参数校验

- Controller 类使用 `@Validated`。
- 查询接口使用 `@Validated(QueryGroup.class)`。
- 新增接口使用 `@Validated(AddGroup.class)` 或 `ValidatorUtils.validate(bo, AddGroup.class)`。
- 编辑接口使用 `@Validated(EditGroup.class)`。
- 路径 ID 使用 `@NotNull(message = "主键不能为空")`。
- 批量 ID 使用 `@NotEmpty(message = "主键不能为空")`。

## 业务异常

- 业务规则不满足时抛 `ServiceException` 或沿用现有模块里的框架异常。
- 异常消息面向操作人员，保持简短明确。
- 不在 Controller 中捕获后手写 JSON；交给全局异常处理。

```java
if (list.size() != ids.size()) {
    throw new ServiceException("您没有删除权限!");
}
```

## 重复提交与幂等

- 新增、编辑等写操作按需使用 `@RepeatSubmit`。
- 已有框架级幂等能力时不自造锁表或前端-only 防重复提交。

## 前端可感知错误

- 需要前端弹出操作反馈的接口，应使用 `postWithMsg` / `putWithMsg` / `deleteWithMsg` 对应的后端常规消息结构。
- 后端错误码/消息不要依赖前端字符串解析来驱动业务分支。

## 禁止

- 禁止返回 `Map<String, Object>` 作为临时错误协议。
- 禁止吞掉异常只写日志。
- 禁止把 Java 堆栈、SQL、密钥、token、身份证号、手机号等敏感信息直接返回给客户端。
