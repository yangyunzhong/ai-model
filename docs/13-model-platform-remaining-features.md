# 模型管理平台 · 剩余功能实现清单

> 本文档用于固化「模型管理平台」原型确认后，从 Mock 原型走向正式实现所需的**全部剩余功能**。
> 面向对象：后端开发、前端开发、测试。
> 定位：这是一份**功能待办清单（Backlog）+ 实现约束**，不是最终的 API 契约与数据库设计，具体字段/表结构实现以代码生成器产物和评审结论为准。

---

## 一、文档说明

### 1.1 背景

原型阶段已完成前端 Mock MVP（可运行、可预览、可交互），并已确认真实后端脚手架。当前处于**原型 → 开发交接**阶段。

### 1.2 现状基线（已核验）

| 维度 | 现状 |
| --- | --- |
| 后端脚手架 | `ruoyi-modules/ruoyi-model` 已生成 **14 张表**的标准五件套（Controller / IXxxService / XxxServiceImpl / Mapper / Mapper.xml）+ Entity / BO / VO |
| 后端业务逻辑 | **零业务逻辑**。ServiceImpl 方法体全部为单行转发，`validEntityBeforeSave`、`deleteWithValidByIds` 均为 `//TODO` |
| 后端接口 | 14 个资源各 6 个标准 CRUD 接口，路径前缀 `/model/<resource>`，权限点 `model:<resource>:<action>` |
| 前端接口层 | `src/api/model/index.ts` 已定义 **14 组 × 6 = 84 个** 请求函数，除 `catalog` 外**零页面引用** |
| 前端页面 | `src/views/model/` 下 6 个页面全部读取本地 Mock（`views/model/data.ts`，1251 行），仅**模型类别（category）**已接入真实后端 |
| 菜单权限 | `sys_menu` 仅 6 个顶级菜单，**无按钮级权限** |

**结论：需要完成的工作 = 「14 张表后端业务逻辑补齐」+「6 个前端页面从 Mock 切换到真实接口」+「新增 1 个业务编排层」。**

### 1.3 已完成的接口

- 模型类别（catalog）：`catalogList` / `catalogAdd` / `catalogUpdate` / `catalogRemove` 已接入真实后端。

---

## 二、已确认的技术决策

以下 4 项决策已确认，实现时必须遵守：

| 编号 | 决策项 | 结论 |
| --- | --- | --- |
| D1 | **执行方式** | 先做**业务编排 + 模拟结果**。阶段 3 只打通数据链路，算法调用抽象为 `ModelExecutor` 接口，实现类返回**模拟值**，真实算法后续替换 |
| D2 | **任务调度** | **复用 SnailJob**。`ruoyi-model` 需引入 `ruoyi-common-job` 依赖；`model_task` 表需**新增 `job_id` 字段**并保存作业 ID |
| D3 | **脚本存储** | **复用 `ruoyi-common-oss`（MinIO）**。依赖已在 `pom.xml` 声明，直接注入 `OssClient` 完成上传 |
| D4 | **设备主表** | **保留 `model_device` 精简表**，废弃并清理 `metro_device` 残留 |

---

## 三、总体分层架构

```
前端 6 页面 + 12 弹窗
        │  HTTP（84 个 api/model 函数）
        ▼
Controller 层   /model/<resource>   14 个（已生成，需补业务语义）
        │
        ▼
业务编排层（★新增，核心）  ModelExecOrchestrator / ModelExecutor / SnailJob 执行器
        │
        ▼
Service 层   IXxxService 14 个（需补 validEntityBeforeSave 等）
        │
        ▼
Mapper 层 + Mapper.xml（14 个，补自定义查询）
        │
        ▼
MySQL 14 张表
        │
        ▼
外部能力：SnailJob（调度）· ruoyi-common-oss（脚本）· ruoyi-common-sse（进度推送）
```

**核心判断：当前缺口是「业务编排层」。** 14 张表的 CRUD 覆盖不了「注册 → 发布 → 定时执行 → 生成结果 → 比对 → 异常预警」的业务闭环，必须新增编排层。

---

## 四、数据库变更清单（DDL 变更）

> 除以下变更外，14 张表结构沿用现有代码生成器产物。表名按实体名 snake_case 推导，最终以 Entity 注解决定。

### 4.1 必须变更项

| 表 | 变更 | 原因 | 关联决策 |
| --- | --- | --- | --- |
| `model_task` | **新增字段 `job_id BIGINT NULL`**（SnailJob 作业 ID），并加注释 | 任务保存/暂停/恢复需操作 SnailJob 作业 | D2 |
| `model_definition` | 枚举字段值**英文化**：`status`（`published`/`offline`/`draft`）、`register_type`（`local`/`third_party`） | 与前端枚举对齐 | — |
| `model_catalog` | `status` 枚举**英文化**：`enabled`/`disabled`（现为「启用/停用」字符串） | 与前端枚举对齐；前端 `statusMetaOf` 仍按中文判断，需同步 | — |
| `model_result` | `health_level` 枚举**英文化**：`healthy`/`subhealthy`/`danger` | 与前端 `healthLevelMeta` 对齐 | — |
| `model_anomaly_event` | `status` 枚举**英文化**：`watch`/`abnormal`/`normal` | 与前端 `anomalyStatusMeta` 对齐 | — |
| `model_exec_log` | 参数列 `params`（与 `BaseEntity.params` 冲突）**重命名为 `exec_params`** | 当前编译报错根因，见 4.3 | — |

### 4.2 需要确认/清理项

| 项 | 说明 |
| --- | --- |
| `metro_device` | 按 D4 废弃清理（`MetroDeviceController` 已删除，需确认表与 Mapper 一并清理） |
| `metro_line` | **保留**（线路主数据被概览/比对/异物检测复用） |
| 历史数据 | `model_catalog` 现有 4 行，其余 13 张表均为 0 行，枚举英文化无历史数据迁移风险 |

### 4.3 编译阻塞修复（必须先做）

`mvn clean install -DskipTests` 当前失败：`ModelExecLog` / `ModelExecLogBo` / `ModelExecLogVo` 的 `String params` 与 `BaseEntity.params`（`Map<String,Object>`）返回类型冲突。修复方案（4 处）：

1. `ModelExecLog.java`（约 95 行）字段改 `execParams` + `@TableField("params")`
2. `ModelExecLogBo.java`（约 98 行）同上
3. `ModelExecLogVo.java`（约 118 行）同上
4. `ModelExecLogServiceImpl.java`（约 89 行）改用 `getExecParams()` / `ModelExecLog::getExecParams`

> 其他 15 个 ServiceImpl 中的 `Map<String,Object> params = bo.getParams();` 是框架标准写法，**不改**。

---

## 五、后端功能清单

### 5.1 通用：14 张表的 CRUD 补齐

每个资源需完成以下通用事项：

- [ ] `ServiceImpl.validEntityBeforeSave(entity)`：唯一性校验（名称/编码唯一）、外键存在性校验、默认值填充
- [ ] `ServiceImpl.deleteWithValidByIds(ids, isValid)`：关联数据存在性校验（如类别下存在模型禁止删除；模型下有任务/结果时的处理策略）
- [ ] `queryList(bo)`：补充分页/条件过滤（关键字、状态、分类、时间范围、外键）
- [ ] `Mapper.xml`：补充自定义联表查询（如 catalog 携带 modelCount、result 携带设备/线路/车站信息）
- [ ] `buildQueryWrapper(bo)`：按业务需要补充条件

### 5.2 逐资源明细

| # | 资源 | 路径 | 表 | 需补的业务语义 |
| --- | --- | --- | --- | --- |
| 1 | catalog | `/model/catalog` | `model_catalog` | 类别名唯一；删除前校验是否被模型引用；`device_type` 关联设备类型；列表补 `modelCount` 统计（见接口 ①） |
| 2 | definition | `/model/definition` | `model_definition` | 模型名全局唯一；`catalog_id` 必须存在且启用；**发布/下架/草稿状态机**（见 5.3）；`enabled` 开关；注册时间/最近执行时间维护 |
| 3 | param | `/model/param` | `model_param` | 按 `model_id` + `direction`（输入/输出）维护；`param_key` 在模型内唯一；排序号维护；模型保存时批量覆盖写 |
| 4 | task | `/model/task` | `model_task` | 一个模型一个任务（或一对多）；保存同步创建/更新 SnailJob 作业并回写 `job_id`；**暂停/恢复**联动 SnailJob；`cron_expression` 校验 |
| 5 | taskParam | `/model/taskParam` | `model_task_param` | 任务级参数键值对；随任务保存批量覆盖写 |
| 6 | execLog | `/model/execLog` | `model_exec_log` | 执行批次主记录；`exec_no` 唯一生成；状态机（运行中→成功/失败/部分成功/超时）；耗时/设备数统计；参数列 `exec_params` |
| 7 | execLogDetail | `/model/execLogDetail` | `model_exec_log_detail` | 按批次号/设备维度的执行明细；日志文本获取（见接口 ③） |
| 8 | result | `/model/result` | `model_result` | 每个（模型 × 设备 × 批次）一条结果；健康等级 + 总分 + 5 个维度分；按模型/批次/设备查询（见接口 ②） |
| 9 | comparisonRecord | `/model/comparisonRecord` | `model_comparison_record` | 一次比对的主记录（设备品种、设备数、参与模型集合、状态） |
| 10 | comparisonDetail | `/model/comparisonDetail` | `model_comparison_detail` | 比对矩阵明细（设备 × 模型 的等级与得分），支撑前端矩阵与结论一致性统计 |
| 11 | anomalyEvent | `/model/anomalyEvent` | `model_anomaly_event` | 异常事件（线路/车站/设备/检测参数/正常值/异常值/偏差/状态）；定时刷新查询（见接口 ④） |
| 12 | device | `/model/device` | `model_device` | 设备主数据（名称/编码/品种/线路/车站/状态）；按品种/线路查询；供比对页设备树使用 |
| 13 | line | `/model/line` | `metro_line` | 线路主数据；供筛选级联使用 |
| 14 | script | `/model/script` | `model_script` | 脚本元数据（文件名/路径/大小/版本/校验和）；**上传走 OSS**（见接口 ⑤）；入口函数 `execute` 校验 |

### 5.3 需新增的业务接口（非标准 CRUD）

> 标准 6 个 CRUD 无法覆盖业务闭环，需新增以下接口。路径建议沿用 `/model/...` 前缀。

| 编号 | 接口 | 方法/路径（建议） | 用途 | 关联页面 |
| --- | --- | --- | --- | --- |
| ① | 类别模型数统计 | `GET /model/catalog/listWithCount` | 列表返回 `modelCount`（现前端硬编码「—」） | 模型类别 |
| ② | 模型结果查询 | `GET /model/result/listByModel` | 按 `modelId`（+批次/设备）返回结果行，供概览高危统计、结果抽屉、比对 | 概览 / 管理 / 比对 |
| ③ | 执行日志文本 | `GET /model/execLogDetail/log` | 按 `execNo` 返回日志行数组 | 任务执行记录弹窗 |
| ④ | 异常分析聚合 | `GET /model/anomalyEvent/analysis` | 按线路/车站/品种/设备返回趋势点 + 事件列表 + 实时监测参数 | 异物检测 |
| ⑤ | 脚本上传 | `POST /model/script/upload` | 上传 `.py` / `requirements.txt` 至 OSS 并落 `model_script` | 模型注册弹窗 |
| ⑥ | 模型发布 | `POST /model/definition/publish` | 草稿/下架 → 已发布，创建默认任务 | 管理 / 注册弹窗 |
| ⑦ | 模型下架 | `POST /model/definition/offline` | 已发布 → 已下架，暂停关联任务 | 管理 |
| ⑧ | 模型启停 | `POST /model/definition/toggleEnabled` | 已发布模型的开/关自动任务 | 管理 |
| ⑨ | 单次运行 | `POST /model/execLog/runOnce` | 按模型 + 运行参数提交一次执行（异步） | 管理 / 运行弹窗 |
| ⑩ | 立即执行任务 | `POST /model/task/runNow` | 触发任务对应 SnailJob 作业立即执行 | 任务 |
| ⑪ | 任务启停 | `POST /model/task/toggleStatus` | 暂停/恢复任务 + 联动 SnailJob 作业 | 任务 |
| ⑫ | 发起比对 | `POST /model/comparisonRecord/execute` | 设备集合 × 模型集合执行比对，写主记录 + 明细，返回矩阵 | 数据比对 |
| ⑬ | 概览统计 | `GET /model/overview/stat` | 6 张统计卡数据（见 7.4） | 模型概览 |
| ⑭ | 概览卡片列表 | `GET /model/overview/models` | 带筛选（分类/关键字/注册方式/状态/设备）的模型卡片列表 | 模型概览 |
| ⑮ | 结果导出 | `POST /model/result/export` | 结果矩阵导出（复用标准 export 亦可） | 管理 / 比对 |

### 5.4 执行编排层（★核心新增）

新增模块内组件（建议 `org.dromara.model.orchestration`）：

| 组件 | 职责 |
| --- | --- |
| `ModelExecutor`（接口） | `ExecuteResult execute(ModelExecContext ctx)`，算法执行抽象 |
| `MockModelExecutor`（实现） | 阶段 3 返回**模拟结果**（按设备生成随机但稳定的健康等级与维度分），永不抛错 |
| `ModelExecOrchestrator` | 编排一次执行：生成 `exec_no` → 落 `model_exec_log`（运行中）→ 逐设备调用 `ModelExecutor` → 落 `model_exec_log_detail` + `model_result` → 回写主记录状态/耗时/设备数 |
| `ModelTaskJob`（`@JobExecutor`） | SnailJob 作业实现类，按 `jobId` 找到任务并调用编排器 |
| `ModelComparisonOrchestrator` | 编排一次比对：取设备集合 × 模型集合结果 → 写 `model_comparison_record` + `model_comparison_detail` → 返回矩阵 |

**执行链路：**

```
前端「立即执行/单次运行」→ 接口 ⑩/⑨
        → SnailJob 触发（或直接异步）→ ModelTaskJob(@JobExecutor)
        → ModelExecOrchestrator.execute()
        → MockModelExecutor.execute()（阶段3返回模拟值）
        → model_exec_log + model_exec_log_detail + model_result
        →（可选）SSE 推送进度 → 前端刷新
```

**SnailJob 接入要点：**

1. `ruoyi-model/pom.xml` 新增依赖 `ruoyi-common-job`
2. `ModelTaskJob` 用 `@JobExecutor(name = "modelTaskExecutor")` 注册
3. 任务保存时通过 SnailJob client 创建/更新作业，返回值写入 `model_task.job_id`
4. 暂停/恢复/删除任务时同步操作 SnailJob 作业
5. 任务参数通过 `model_task_param` 传入作业上下文

---

## 六、前端功能点清单（逐页面 / 逐弹窗）

> 所有页面当前均从 `views/model/data.ts` 取 Mock 数据。实现时改为调用 `src/api/model/index.ts`（84 个函数已就绪），并同步删除或保留 Mock 作为降级。

### 6.0 通用改造项（每个页面都要做）

- [ ] 数据源从 `../data` 切换到 `#/api/model`
- [ ] 字段命名映射（见第七章）——Mock 字段名与后端不一致
- [ ] 枚举中文 → 英文（见第七章），前端 meta 展示层做映射
- [ ] 加载态、空态、错误态对接（`PlatformTable` 的 `loading`、`Empty`）
- [ ] 分页对接后端 `total`（类别页已做，其余页面补）
- [ ] 操作成功/失败提示改用真实响应，移除 `window.message.success` 假提示

### 6.1 模型类别 category（唯一已接真实接口，需补齐）

文件：`views/model/category/index.vue`、`modal/category-modal.vue`

| 功能点 | 现状 | 待实现 |
| --- | --- | --- |
| 列表分页查询 | ✅ 已接 `catalogList` | 保留 |
| 新增/编辑 | ✅ 已接 `catalogAdd`/`catalogUpdate` | 保留 |
| 删除 | ✅ 已接 `catalogRemove` | 删除前校验是否被模型引用（后端 5.1） |
| 模型数量列 | ❌ 硬编码「—」 | 接接口 ①，显示真实 `modelCount` |
| 状态列 | 中文判断 `status === '停用'` | 枚举英文化后改判 `disabled` |
| 状态值 | 表单提交「启用/停用」中文 | 改为 `enabled`/`disabled` |

### 6.2 模型管理 manage

文件：`views/model/manage/index.vue` + `modal/{model-register-modal, model-run-modal, model-result-drawer, model-detail-drawer}.vue`

**列表页功能点：**

| 功能点 | 说明 | 后端接口 |
| --- | --- | --- |
| 模型列表 | 列：模型名称 / 分类 / 统计周期 / 状态 / 注册时间 / 注册方式 / 最近运行 / 操作 | `definitionList` |
| 筛选回填 | `route.query` 的 `categoryId` / `keyword` / `status`（概览跳转带入） | 前端 |
| 状态机操作 | 草稿→编辑/发布/删除；已发布→启停/单次运行/下架/查看结果；已下架→重新发布/删除 | 见下表 |
| 分页 | 接后端分页 | 前端 |

**状态机与接口映射：**

| 操作 | 前置状态 | 后置状态 | 接口 |
| --- | --- | --- | --- |
| 发布 | 草稿 / 已下架 | 已发布 | ⑥ `publish` |
| 下架 | 已发布 | 已下架 | ⑦ `offline` |
| 开启/停止自动任务 | 已发布 | 已发布（enabled 切换） | ⑧ `toggleEnabled` |
| 单次运行 | 已发布 | — | ⑨ `runOnce` |
| 删除 | 草稿 / 已下架 | — | `definitionRemove` |
| 编辑 | 草稿 | — | `definitionUpdate` |
| 查看结果 | 已发布 | — | ② `result/listByModel` |

**注册/编辑模型弹窗（`model-register-modal.vue`）功能点：**

| 区块 | 字段 | 待实现 |
| --- | --- | --- |
| 一、基本信息 | 模型名称（唯一）、模型分类（仅启用）、注册方式、统计周期（天）、模型简介 | 模型名唯一校验走后端；分类下拉接 `catalogList` |
| 二、参数配置 | **输入参数**行：参数标识 / 显示名 / 类型 / 必填 / 默认值；**输出参数**行：参数标识 / 显示名 / 类型 / 说明 | 保存时批量写 `model_param`（接口 `param`），方向 `input`/`output` |
| 三、算法脚本 | 拖拽上传 `.py`（≤50MB，需校验 `execute` 入口）、`requirements.txt`（可选）、文件列表 | 接接口 ⑤ 上传 OSS |
| 底部操作 | 「确定（存草稿）」「确定并开启」 | 草稿=`definitionAdd`；开启=保存 + ⑥ publish |
| 编辑回显 | 打开时按 `modelId` 拉参数与脚本 | 接口 `param` + `script` |

**单次运行弹窗（`model-run-modal.vue`）功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 按模型输入参数渲染表单（`布尔`→下拉、`数值`→number、`文本`→text） | 参数来自 `param` 接口（`direction=input`） |
| 必填校验 | 前端校验 |
| 提交执行 | 接口 ⑨ `runOnce`，携带参数对象 |
| 设备范围 | 默认该分类关联设备类型下全部设备（后端按 `catalog.device_type` 解析） |

**查看结果抽屉（`model-result-drawer.vue`）功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 视图切换「按线路层级 / 按设备品种层级」 | 前端排序 |
| 结果列：设备名称/线路/车站 + 健康等级/总分/5 维度分 | 接口 ② `result/listByModel` |
| 健康等级标签 | `healthLevelMeta` 映射（英文枚举） |

**模型详情抽屉（`model-detail-drawer.vue`）功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 基本信息（分类/注册方式/统计周期/状态/最近运行/简介） | `definition/{id}` |
| 输入/输出参数列表 | 接口 `param` |
| 最近执行记录（3 条） | `execLogList` 按 `modelId` 过滤 |

### 6.3 模型任务 task

文件：`views/model/task/index.vue` + `modal/{task-config-modal, task-exec-drawer, task-log-modal}.vue`

**列表页功能点：**

| 功能点 | 说明 | 后端接口 |
| --- | --- | --- |
| 任务列表 | 列：任务名称 / 模型 / 执行周期 / 上次执行 / 下次执行 / 状态 / 操作 | `taskList`（联表带模型名） |
| 筛选回填 | `route.query.keyword`、`route.query.taskId`（自动打开执行记录抽屉） | 前端 |
| 配置 | 打开配置弹窗 | `task/{id}` |
| 暂停/恢复 | `toggleTaskStatus` | 接口 ⑪ |
| 立即执行 | `runNow` | 接口 ⑩ |
| 执行记录 | 打开执行记录抽屉 | `execLogList` 按 `taskId` |
| 删除 | 删除任务 + 同步删 SnailJob 作业 | `taskRemove` |

**配置任务弹窗（`task-config-modal.vue`）功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 任务名称 | 校验非空 |
| 执行周期 | 预设选项 + 「自定义 Cron」；`validateCron` 5 位校验（前端已实现） |
| 运行参数 | 按模型输入参数渲染；留空使用默认值 |
| 备注 | ≤200 字符 |
| 保存 | `taskUpdate` + 同步 SnailJob 作业；周期写入 `cron_expression` |

**执行记录抽屉（`task-exec-drawer.vue`）功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 执行记录列表 | 列：批次号 / 触发方式 / 状态 / 开始时间 / 耗时 / 设备数 / 日志 | `execLogList` 按 `taskId` |
| 日志 | 打开日志弹窗 | 接口 ③ `execLogDetail/log` |

**执行日志弹窗（`task-log-modal.vue`）功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 日志文本 `<pre>` 展示 | 接口 ③ 返回行数组 |

### 6.4 模型概览 overview

文件：`views/model/overview/index.vue` + `modal/model-detail-drawer.vue`

**6 张统计卡（需接接口 ⑬ `overview/stat`）：**

| 卡片 | 当前计算逻辑（Mock） | 后端实现 |
| --- | --- | --- |
| 模型总数 | `models.length` | COUNT(model_definition) |
| 模型分类数 | `categories.length` | COUNT(model_catalog) |
| 本地注册数 | 按 `registerType==='本地注册'` | COUNT WHERE register_type='local' |
| 第三方注册数 | 按 `registerType==='第三方注册'` | COUNT WHERE register_type='third_party' |
| 近 7 天运行失败 | execRecords 中 `失败` 且 7 天内 | COUNT(model_exec_log WHERE status='failed' AND start_time>=NOW()-7d) |
| 高危设备数 | 已发布模型结果中 `healthLevel==='高危'` 的设备去重 | COUNT(DISTINCT device_id) FROM model_result JOIN definition ON status='published' WHERE health_level='danger' |

**其余功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 卡片网格（3 列）与筛选（分类/关键字/注册方式/状态） | 接口 ⑭ `overview/models` |
| 设备过滤 `deviceFilterIds`（`route.query.deviceId` 逗号分割） | 前端 + 后端过滤 |
| 「去比对」跳转 `/model-comparison/task?deviceType&modelId` | 保留路由 |
| 「查看结果」跳转 `/model-manage/list?categoryId&keyword` | 保留路由 |
| 模型详情抽屉 | 同 6.2 详情抽屉 |

### 6.5 数据比对 compare

文件：`views/model/compare/index.vue`（1100 行，纯 Mock，最重）

**比对配置区功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 设备品种选择 | `deviceTypeOptions` |
| 选择设备（弹窗，线路 → 车站 → 设备三级树） | 接口 `device` / `line`，按品种过滤 |
| 全选/全不选、线路级联勾选、半选态 | 前端 |
| 比对模型多选（仅已发布 + 品种匹配，2~5 个） | 接口 ② / `definitionList` |
| 跳转回填 | `route.query.deviceType` / `modelId` |
| 重置 | 前端 |

**比对执行区功能点：**

| 功能点 | 待实现 |
| --- | --- |
| 比对进度动画（3 步：加载脚本 / 注入数据 / 计算评分） | 前端（可对接真实进度） |
| 比对矩阵（设备 × 模型，含健康等级、总分、5 维度、极差） | 接口 ⑫ `comparisonRecord/execute` |
| 单元格高低分标记（best/worst）、结论不一致高亮 | 前端（后端返回明细） |
| 相对基准模型的总分增减（↑/↓） | 前端计算 |
| 比对结论汇总（一致/不一致台数） | 前端 |
| 各模型健康分布统计 | 前端 |
| 落库 | 后端写 `comparison_record` + `comparison_detail` |

> **待确认**：比对数据来自「实时重新执行」还是「取历史 `model_result`」。见第十一章。

### 6.6 异物检测 anomaly

文件：`views/model/anomaly/index.vue`（514 行，纯 Mock）

| 功能点 | 现状 | 待实现 |
| --- | --- | --- |
| 自动刷新 | 前端 `setInterval` 30s + `tick` | 改为接口轮询或 SSE 推送 |
| 级联筛选 | 线路 → 车站 → 设备品种 → 设备（清空下级） | 接口 `line` / `device` |
| 趋势图 | ECharts 双 Y 轴（电流/电压）+ 异常点标记 | 接口 ④ `analysis` 返回趋势点 |
| 异常事件表格（单设备） | 检测时间/设备/参数/正常值/异常值/偏差 | 接口 ④ |
| 全部记录弹窗 | 多线路/车站 + 日期区间筛选 | 接口 `anomalyEventList` + 时间范围 |
| 实时监测参数面板 | `analysis.monitorParams` 静态 | 接口 ④ |
| 异常状态标签 | `anomalyStatusMeta`（关注/异常/正常） | 英文化映射 |
| 预警机制说明 | 静态文案（电流>20%、振动>30%、同时异常、提前 15 分钟） | 保留 |

---

## 七、字段与枚举映射表（Mock → 真实后端）

> 前端切换真实接口时必须同步改造，否则页面读取不到值。

### 7.1 字段命名映射

| 前端 Mock 字段 | 后端字段 | 涉及页面 |
| --- | --- | --- |
| `categoryId` | `catalogId` | manage / compare / task / overview |
| `categoryName` | `catalogName` | category / 各详情 |
| `statPeriod` | `statisticsCycleDays` | manage / 注册弹窗 / 详情 |
| `cronDesc` | `cronExpression` | task / 配置弹窗 |
| `lastRunTime` | `lastExecuteTime` | task / manage / 详情 |
| `nextExecTime` | `nextExecuteTime` | task |
| `enabled`（boolean） | `status`（枚举） | manage / overview 详情 |
| `healthLevel`（中文） | `health_level`（英文） | overview / result / compare |
| `deviceType` | `device_type` | 全站 |

### 7.2 枚举映射

| 枚举 | 前端 Mock 值 | 后端值 |
| --- | --- | --- |
| `ModelStatus` | 已发布 / 已下架 / 草稿 | `published` / `offline` / `draft` |
| `RegisterType` | 第三方注册 / 本地注册 | `third_party` / `local` |
| `ModelParamType` | 布尔 / 文本 / 数值 | `boolean` / `text` / `number` |
| `TaskStatus` | 暂停 / 运行中 | `paused` / `running` |
| `ExecTriggerType` | 数据比对 / 定时 / 单次运行 / 立即执行 | `comparison` / `scheduled` / `manual` / `immediate` |
| `ExecStatus` | 部分成功 / 成功 / 失败 / 运行中 / 超时 | `partial` / `success` / `failed` / `running` / `timeout` |
| `HealthLevel` | 健康 / 亚健康 / 高危 | `healthy` / `subhealthy` / `danger` |
| `AnomalyMonitorStatus` | 关注 / 异常 / 正常 | `watch` / `abnormal` / `normal` |
| 类别状态 | 启用 / 停用 | `enabled` / `disabled` |

> **实现建议**：前端保留一份 `xxxMeta` 映射表，key 用英文值，`label` 用中文，`status` 用平台标签色；页面只依赖 meta，不硬编码中文。

---

## 八、权限与菜单清单

### 8.1 菜单

现有 6 个顶级菜单，需补齐为完整菜单树并在 `sys_menu` 落库：

| 菜单 | 路由 |
| --- | --- |
| 模型概览 | `/model-overview` |
| 模型管理 | `/model-manage` |
| 模型任务 | `/model-task` |
| 数据比对 | `/model-comparison` |
| 模型类别 | `/model-category` |
| 异物检测 | `/model-anomaly` |

### 8.2 按钮权限点（14 资源 × 6 动作）

标准权限点已由代码生成器在 `@SaCheckPermission` 中声明，需在 `sys_menu` 补齐：

```
model:<resource>:list   model:<resource>:query
model:<resource>:add    model:<resource>:edit
model:<resource>:remove model:<resource>:export
```

其中 `<resource>` ∈ {catalog, definition, param, task, taskParam, execLog, execLogDetail, result, comparisonRecord, comparisonDetail, anomalyEvent, device, line, script}。

### 8.3 新增业务接口权限点

| 接口 | 建议权限点 |
| --- | --- |
| 模型发布/下架/启停 | `model:definition:edit` |
| 单次运行 | `model:execLog:add` |
| 任务立即执行/启停 | `model:task:edit` |
| 发起比对 | `model:comparisonRecord:add` |
| 脚本上传 | `model:script:add` |
| 概览统计 | `model:overview:list`（新增） |

---

## 九、实施顺序

| 阶段 | 内容 | 前置 | 产出 |
| --- | --- | --- | --- |
| **P0 编译修复** | 修 `ModelExecLog` params 冲突（4.3） | 无 | 后端可编译 |
| **P1 基础数据** | catalog / definition / param / device / line 的 CRUD 业务逻辑 + 枚举英文化 | P0 | 基础主数据可用 |
| **P2 前端切接口（基础）** | 类别补齐 + 管理页列表/注册弹窗/详情抽屉切真实接口 | P1 | 模型注册闭环可用 |
| **P3 执行编排** | 引入 `ruoyi-common-job`；`model_task` 加 `job_id`；`ModelExecutor` + `ModelExecOrchestrator` + `ModelTaskJob`（模拟结果）；result / execLog / execLogDetail 落库 | P1 | 定时/单次执行闭环 |
| **P4 前端切接口（任务/结果）** | 任务页 + 3 弹窗 + 结果抽屉 + 概览统计 | P3 | 任务与结果页面真实化 |
| **P5 脚本与比对** | 脚本 OSS 上传；比对编排 + 前端比对页 | P3 | 比对闭环 |
| **P6 异常检测与收尾** | 异物检测接口 + 前端；权限菜单；导出；全链路验收 | P2~P5 | 全功能上线 |

---

## 十、验收标准

- [ ] `mvn clean install -DskipTests` 通过
- [ ] 14 张表 CRUD 接口全部具备业务语义（唯一性、关联校验、条件查询）
- [ ] 前端 6 页面 + 12 弹窗无 `../data` Mock 引用（或仅保留降级）
- [ ] 字段/枚举映射无残留中文枚举
- [ ] 注册 → 发布 → 定时执行 → 生成结果 → 比对 → 异常预警 全链路可跑通
- [ ] `sys_menu` 菜单与按钮权限完整，按角色可见
- [ ] `metro_device` 残留清理完成

---

## 十一、待确认问题

| # | 问题 | 影响 |
| --- | --- | --- |
| Q1 | `ModelExecLog` 的 `params → execParams`（4.3）是否确认执行 | P0 编译阻塞 |
| Q2 | 数据比对的数据来源：**重新执行** 还是 **取两条历史 `model_result` 对比** | 接口 ⑫ 设计 |
| Q3 | `health_level` / `status` 英文化 SQL 是否已落库 | P1 枚举对齐 |
| Q4 | 模型与任务是否严格一对一 | task 表与接口设计 |
| Q5 | 一个模型被删除时，其任务/结果/比对明细的处理策略（级联删 / 保留 / 阻止） | 5.1 删除校验 |
| Q6 | 脚本执行方式：平台内进程执行 / 远程执行器（阶段 3 用 Mock 规避） | P5 及后续 |
| Q7 | 执行进度是否需要 SSE 实时推送，还是前端轮询即可 | 接口 ⑨/⑩ 设计 |
| Q8 | 异物检测的「实时监测参数」是否为真实采集，还是仅异常事件聚合 | 接口 ④ 设计 |
