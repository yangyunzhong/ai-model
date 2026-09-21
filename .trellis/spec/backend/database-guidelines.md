# 数据库与查询规范

## ORM 与分页

- 默认使用 MyBatis-Plus，不引入 PageHelper 或手写分页工具。
- Mapper 优先继承 `BaseMapperPlus<Entity, Vo>`。
- 分页入参使用 `PageQuery`，返回使用 `TableDataInfo<Vo>`。
- 查询条件优先在 Service 中通过 `Wrappers.lambdaQuery()` / `LambdaQueryWrapper` 构造。
- Entity 到 VO 的查询优先使用 `selectVoById`、`selectVoList`、`selectVoPage`。

```java
LambdaQueryWrapper<TestDemo> lqw = Wrappers.lambdaQuery();
lqw.eq(bo.getDeptId() != null, TestDemo::getDeptId, bo.getDeptId());
lqw.like(StringUtils.isNotBlank(bo.getTestKey()), TestDemo::getTestKey, bo.getTestKey());
Page<TestDemoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
return TableDataInfo.build(result);
```

## BO 参数

- 时间范围、扩展查询条件可沿用 `bo.getParams()` 中的 `beginXxx` / `endXxx` 模式。
- 字符串判断使用框架工具，如 `StringUtils.isNotBlank`。
- 查询 BO 使用 `@Validated(QueryGroup.class)`，新增/编辑使用 `AddGroup` / `EditGroup`。

## 数据权限

- 涉及部门、用户、组织范围的查询，优先在 Mapper 方法上使用 `@DataPermission` 和 `@DataColumn`。
- 不在业务方法里硬编码“当前用户能看哪些部门”的 SQL 拼接。
- 自定义 Mapper 方法如果返回业务列表，也要确认是否需要数据权限注解。

```java
@DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
})
Page<TestDemoVo> customPageList(@Param("page") Page<TestDemo> page, @Param("ew") Wrapper<TestDemo> wrapper);
```

## 多租户

- 默认尊重框架多租户插件，不绕过 tenant 过滤。
- 需要跨租户处理时使用框架能力，例如 `TenantHelper.dynamic(...)`，并在代码中说明业务原因。
- 登录、初始化、同步套餐等特殊场景可以参考 `ruoyi-admin` 与 `ruoyi-system` 现有实现。

## 新增与更新

- BO 转 Entity 使用 `MapstructUtils.convert`。
- 保存前业务校验放在 Service 的 `validEntityBeforeSave` 或明确的私有校验方法。
- 新增后如需回填 ID，按现有模式在 insert 成功后 `bo.setId(add.getId())`。

## 删除

- 删除接口接收 ID 数组时使用 `@NotEmpty(message = "主键不能为空")`。
- 删除前需要业务权限/引用校验时放在 Service，不在 Controller 里查库。
- 批量删除使用框架提供的批量能力，如 `deleteByIds`，不要循环单条删除。

## SQL 与 XML

- 简单查询优先 Lambda wrapper。
- 复杂关联或生成器已有 XML 时再写 Mapper XML。
- 手写 XML 必须确认租户、数据权限、逻辑删除、排序和分页是否仍由框架处理。

## 禁止

- 禁止字符串拼接 SQL。
- 禁止把分页参数散落为 `pageNum`、`pageSize` 手工计算 offset。
- 禁止绕过 `BaseMapperPlus` 直接返回 Entity 给 Controller。
- 禁止在 Controller 中直接调用 Mapper。
