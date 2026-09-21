# 后端目录结构

> 后端以 `RuoYi-Vue-Plus/` 为正式实现参考。新增业务优先按现有多模块结构落位。

## 顶层结构

```text
RuoYi-Vue-Plus/
├── ruoyi-admin/        # 启动入口、Web 登录认证、全局资源配置
├── ruoyi-common/       # 通用能力包：web、mybatis、security、tenant、excel、log 等
├── ruoyi-modules/      # 业务模块：system、demo、workflow、generator、job 等
├── ruoyi-extend/       # 扩展服务：monitor-admin、snailjob-server 等
└── script/             # 部署、SQL、Docker 等脚本
```

## 业务模块结构

常规业务模块应参考以下形态：

```text
ruoyi-modules/<module>/
└── src/main/java/org/dromara/<module>/
    ├── controller/          # REST Controller
    ├── domain/              # Entity
    │   ├── bo/              # 请求/查询/表单对象
    │   └── vo/              # 响应/导出对象
    ├── mapper/              # MyBatis-Plus Mapper
    └── service/
        ├── I XxxService.java
        └── impl/            # ServiceImpl
```

## 类职责

- `Controller`：只做 HTTP 映射、权限、日志、参数校验、调用 Service、包装返回。
- `Service`：承载业务规则、保存前校验、事务边界和编排逻辑。
- `Mapper`：数据访问，继承 `BaseMapperPlus<Entity, Vo>`，复杂查询再写自定义方法/XML。
- `Entity`：数据库表映射，使用 `@TableName` 等 MyBatis-Plus 注解。
- `Bo`：请求入参，配合 `AddGroup`、`EditGroup`、`QueryGroup` 做分组校验。
- `Vo`：接口返回和导出字段，导出/字典转换放在 VO 上。

## 命名约定

- Controller：`XxxController`
- Service 接口：`IXxxService`
- Service 实现：`XxxServiceImpl`
- Mapper：`XxxMapper`
- Entity：`Xxx`
- 请求对象：`XxxBo`
- 返回对象：`XxxVo`
- 权限码：`模块:资源:动作`，例如 `demo:demo:list`、`demo:demo:add`。
- REST 路径：按模块资源组织，例如 `/demo/demo`、`/system/user`。

## 新增模块原则

1. 业务功能优先放入 `ruoyi-modules/<业务模块>`。
2. 通用能力才进入 `ruoyi-common/<能力包>`，进入前必须确认复用场景和兼容影响。
3. 启动、登录、验证码、认证策略等 Web 入口相关内容才放入 `ruoyi-admin`。
4. 代码生成器生成的 CRUD 代码要按本规范二次整理，不能把生成结果当成最终设计。

## 参考样例

- 常规 CRUD：`ruoyi-modules/ruoyi-demo/.../TestDemoController.java`
- 分页查询：`TestDemoServiceImpl.queryPageList`
- 数据权限 Mapper：`TestDemoMapper`
- 工作流复杂业务：`ruoyi-modules/ruoyi-workflow/...`
