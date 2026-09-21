# 后端日志规范

## 操作日志

- 新增、修改、删除、导入、导出等用户可见写操作优先使用 `@Log`。
- `title` 使用业务中文名称，`businessType` 使用框架枚举：
  - `BusinessType.INSERT`
  - `BusinessType.UPDATE`
  - `BusinessType.DELETE`
  - `BusinessType.IMPORT`
  - `BusinessType.EXPORT`

```java
@SaCheckPermission("demo:demo:export")
@Log(title = "测试单表", businessType = BusinessType.EXPORT)
@PostMapping("/export")
public void export(@Validated TestDemoBo bo, HttpServletResponse response) {
    ...
}
```

## 应记录什么

- 用户主动触发的增删改导入导出。
- 关键业务状态变更。
- 需要审计的权限、租户、流程操作。
- 异常排查所需的业务 ID、租户 ID、任务 ID、流程实例 ID。

## 不应记录什么

- 密码、token、RSA 私钥、公钥明文配置、验证码。
- 身份证、手机号、邮箱等敏感信息的完整明文。
- 大体积请求体、文件内容、Excel 原始内容。

## 应用日志

- 使用现有日志体系和 `logback-plus.xml` 配置。
- 不在正常业务分支大量打印 `info`。
- 排查临时代码不得长期保留高频日志。
- 捕获异常时要保留上下文 ID，但不要吞异常。

## 前后端联调日志

- 接口联调时优先通过明确业务 ID 和接口路径定位问题。
- 不为了调试在返回体中暴露内部异常或 SQL。
