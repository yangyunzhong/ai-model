# 计划：设备数据源从 model_device 切换为 metro_device

## Context

ruoyi-model 模块当前从自建 `model_device` 表（设备台账 CRUD）获取设备数据，用于：设备列表/详情接口、模型执行时解析设备（ModelExecLogServiceImpl）、数据比对（ModelComparisonRecordServiceImpl）。

需求：所有获取设备数据的位置改为从 `metro_device` 表（外部设备主数据，tinyint 类型码、无启停状态）读取，并调整相关字段。

## 已确认的关键事实

- `metro_device`（ai-model 库，当前空表）：`id varchar(32)`、`device_type tinyint(1-扶梯/2-电梯)`、`line_id→metro_line.id`、`station_id→metro_station.id`、`device_variety_code`、`running_status tinyint`、`brand`、`registration_code`、`model_spec`、`location_detail`、`commissioning_date`、`remark`、create_by(varchar)/create_time/update_by/update_time。**无** device_name/status/sort_order/install_position/create_dept。
- `metro_line.name`、`metro_station.name` 可联表取名称。
- `model_catalog.device_type`（业务口径字符串）：自动扶梯/垂直电梯/水泵/屏蔽门 → 映射 `1=自动扶梯`、`2=垂直电梯`；水泵/屏蔽门在 metro_device 无对应类型（查询应得到空集）。
- BaseEntity 含 createDept(Long)/createBy(Long)，与 metro_device 不兼容 → 实体不能继承 BaseEntity。
- 前端 DeviceVO（model.d.ts）已含 metro 全字段；仅 `compare/index.vue` 调用 `deviceList`（且传了 `status:'启用'`）；`deviceAdd/deviceUpdate/deviceRemove` 无页面调用。
- 后端写接口（add/edit/remove）无其他内部调用方。

## 字段映射

| model_device | metro_device | 处理 |
|---|---|---|
| id | id | 直接 |
| device_name | 无 | 计算生成：`品牌+型号` → 注册代码 → id（SQL 与 Java 两侧同逻辑） |
| device_type(varchar) | device_type(tinyint) | `1↔自动扶梯`、`2↔垂直电梯`；无法映射的品种查询结果为空 |
| line_id / station_id | 同名 | 直接，联 metro_line/metro_station 取 name |
| install_position | location_detail | |
| status(启用/停用) | 无 | 删除启停过滤与字段；原"启用设备"= 全部设备 |
| sort_order | 无 | 排序改为 line_id, station_id, id |
| — | running_status/brand/registration_code/model_spec/device_variety_code/commissioning_date | 作为新输出字段 |

## 后端改动（RuoYi-Vue-Plus/ruoyi-modules/ruoyi-model）

1. **domain/ModelDevice.java**：`@TableName("metro_device")`；不再继承 BaseEntity，直接 `implements Serializable`；字段：id、deviceType(Integer)、lineId、stationId、deviceVarietyCode、runningStatus(Integer)、brand、registrationCode、modelSpec、locationDetail、remark、createTime、updateTime；手写 `getDeviceName()`（非表字段计算属性：brand+modelSpec → registrationCode → id）；静态方法 `typeCodeOf(String)`/`typeNameOf(Integer)` 做类型互转。
2. **domain/vo/ModelDeviceVo.java**：字段改为 id、deviceName、deviceType(String，SQL CASE 转换)、lineId、lineName、stationId、stationName、deviceVarietyCode、runningStatus、brand、registrationCode、modelSpec、locationDetail、commissioningDate、remark；移除 status/sortOrder/installPosition；保留 Excel 注解。
3. **domain/bo/ModelDeviceBo.java**：改为纯查询参数（id、deviceName、deviceType、lineId、stationId、deviceVarietyCode、runningStatus、brand、registrationCode、modelSpec、locationDetail、remark），删除 AddGroup/EditGroup 校验注解与 status/sortOrder。
4. **mapper/model/ModelDeviceMapper.xml**：三段 SQL 改为查 `metro_device t` 联 `metro_line`/`metro_station`；SELECT 中生成 device_name（`COALESCE(NULLIF(TRIM(CONCAT_WS(' ',t.brand,t.model_spec)),''),NULLIF(t.registration_code,''),t.id)`）与 device_type（CASE 1→'自动扶梯' 2→'垂直电梯'）；条件 deviceType 用 `CASE #{bo.deviceType} ... ELSE -1 END`；deviceName 模糊匹配 brand/model_spec/registration_code；ORDER BY t.line_id,t.station_id,t.id。
5. **service/IModelDeviceService + impl + controller**：删除 insertByBo/updateByBo/deleteWithValidByIds/validEntityBeforeSave 及 Controller 的 add/edit/remove 端点（metro_device 为外部主数据只读）；保留 list/export/getInfo。
6. **service/impl/ModelExecLogServiceImpl.resolveDevices**：删除 `DEVICE_STATUS_ENABLED` 与 status 过滤；`ModelDevice.typeCodeOf(definition.getDeviceType())` 为 null 时直接返回空列表；排序改 id；`DeviceInfo.deviceType` 用 `ModelDevice.typeNameOf(device.getDeviceType())`。
7. **service/impl/ModelComparisonRecordServiceImpl.execute**：deviceIds 分支排序改 id；deviceType 分支同样转 typeCode（null → 空设备列表）；deviceName 取计算属性（调用处不变）。
8. 注释更新：ModelResult/ModelResultBo/ModelResultVo、ModelAnomalyEvent/Bo/Vo 中"逻辑关联model_device.id" → "逻辑关联metro_device.id"。

## 前端改动（ruoyi-plus-vben5/apps/web-antd）

1. **src/api/model/model.d.ts**：
   - DeviceVO：删除 installPosition、status、sortOrder；deviceType 注释改为 metro_device tinyint 映射口径。
   - DeviceQuery：删除 status。
   - 删除 DeviceForm 接口（写接口移除后无引用）。
   - 6 处注释 model_device.id → metro_device.id。
2. **src/api/model/index.ts**：删除 deviceAdd/deviceUpdate/deviceRemove 函数；import 去掉 DeviceForm。
3. **src/views/model/compare/index.vue**：loadDevices 查询参数去掉 `status: '启用'`。

## 验证

1. 后端编译：`mvn -o -q -pl ruoyi-modules/ruoyi-model compile`（cwd=RuoYi-Vue-Plus）。
2. 前端类型检查：`pnpm -F @vben/web-antd run typecheck`（compare/index.vue 既有 TS 错误与本次无关，忽略）。
3. SQL 语法验证：用 mysql CLI 对空表 metro_device 手工执行 XML 中 device_name 生成表达式与 device_type CASE 映射查询，确认无语法错误。
