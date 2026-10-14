import type { PageQuery, BaseEntity } from '#/api/common';

export interface CatalogVO {
  /**
   * 主键（支持UUID或自定义编码）
   */
  id: string | number;

  /**
   * 类别名称（如：自动扶梯、垂直电梯）
   */
  catalogName: string;

  /**
   * 关联设备类型（自动扶梯/垂直电梯等）
   */
  deviceType: string;

  /**
   * 备注说明
   */
  description: string;

  /**
   * 状态（0：停用 1：正常）
   */
  status: string;

  /**
   * 创建时间
   */
  createTime: string;

  /**
   * 该类别下的模型数量（后端统计）
   */
  modelCount: number;

}

export interface CatalogForm extends BaseEntity {
  /**
   * 主键（支持UUID或自定义编码）
   */
  id?: string | number;

  /**
   * 类别名称（如：自动扶梯、垂直电梯）
   */
  catalogName?: string;

  /**
   * 关联设备类型（自动扶梯/垂直电梯等）
   */
  deviceType?: string;

  /**
   * 备注说明
   */
  description?: string;

  /**
   * 状态（0：停用 1：正常）
   */
  status?: string;

}

export interface CatalogQuery extends PageQuery {
  /**
   * 类别名称（如：自动扶梯、垂直电梯）
   */
  catalogName?: string;

  /**
   * 关联设备类型（自动扶梯/垂直电梯等）
   */
  deviceType?: string;

  /**
   * 备注说明
   */
  description?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface TaskVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 关联模型ID，逻辑关联model_definition.id
   */
  modelId: string | number;

  /**
   * 任务名称
   */
  taskName: string;

  /**
   * cron表达式（如 0 2 * * *）
   */
  cronExpression: string;

  /**
   * cron描述（如“每日 02:00”）
   */
  cronDescription: string;

  /**
   * 任务状态
   */
  status: string;

  /**
   * 调度类型：cron=周期调度，once=一次性指定时间
   */
  scheduleType: string;

  /**
   * 最近一次执行时间
   */
  lastExecuteTime: string;

  /**
   * 下次执行时间（可计算，可选）
   */
  nextExecuteTime: string;

  /**
   * 备注
   */
  remark: string;

}

export interface TaskForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 关联模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 任务名称
   */
  taskName?: string;

  /**
   * cron表达式（如 0 2 * * *）
   */
  cronExpression?: string;

  /**
   * cron描述（如“每日 02:00”）
   */
  cronDescription?: string;

  /**
   * 任务状态
   */
  status?: string;

  /**
   * 调度类型：cron=周期调度，once=一次性指定时间
   */
  scheduleType?: string;

  /**
   * 最近一次执行时间
   */
  lastExecuteTime?: string;

  /**
   * 下次执行时间（可计算，可选）
   */
  nextExecuteTime?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface TaskQuery extends PageQuery {
  /**
   * 关联模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 任务名称
   */
  taskName?: string;

  /**
   * cron表达式（如 0 2 * * *）
   */
  cronExpression?: string;

  /**
   * cron描述（如“每日 02:00”）
   */
  cronDescription?: string;

  /**
   * 任务状态
   */
  status?: string;

  /**
   * 最近一次执行时间
   */
  lastExecuteTime?: string;

  /**
   * 下次执行时间（可计算，可选）
   */
  nextExecuteTime?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface DefinitionVO {
  /**
   * 主键（支持UUID或自定义编码）
   */
  id: string | number;

  /**
   * 模型名称
   */
  modelName: string;

  /**
   * 所属类别ID，逻辑关联model_catalog.id
   */
  catalogId: string | number;

  /**
   * 适用设备类型（冗余，便于查询）
   */
  deviceType: string;

  /**
   * 统计周期（天）
   */
  statisticsCycleDays: number;

  /**
   * 状态：草稿/已发布/已归档/已下架
   */
  status: string;

  /**
   * 注册方式：本地注册/第三方注册
   */
  registerType: string;

  /**
   * 注册时间
   */
  registerTime: string;

  /**
   * 模型简介/说明
   */
  description: string;

  /**
   * 算法脚本存储路径
   */
  scriptPath: string;

  /**
   * 运行时容器ID
   */
  containerId?: string;

  /**
   * 运行时容器名称
   */
  containerName?: string;

  /**
   * 运行时容器状态：running/missing/disabled
   */
  containerStatus?: string;

  /**
   * 运行时镜像
   */
  runtimeImage?: string;

  /**
   * 最近发布时间
   */
  lastPublishTime?: string;

}

export interface DefinitionForm extends BaseEntity {
  /**
   * 主键（支持UUID或自定义编码）
   */
  id?: string | number;

  /**
   * 模型名称
   */
  modelName?: string;

  /**
   * 所属类别ID，逻辑关联model_catalog.id
   */
  catalogId?: string | number;

  /**
   * 适用设备类型（冗余，便于查询）
   */
  deviceType?: string;

  /**
   * 统计周期（天）
   */
  statisticsCycleDays?: number;

  /**
   * 状态：草稿/已发布/已归档/已下架
   */
  status?: string;

  /**
   * 注册方式：本地注册/第三方注册
   */
  registerType?: string;

  /**
   * 注册时间
   */
  registerTime?: string;

  /**
   * 模型简介/说明
   */
  description?: string;

  /**
   * 算法脚本存储路径
   */
  scriptPath?: string;

}

export interface DefinitionQuery extends PageQuery {
  /**
   * 模型名称
   */
  modelName?: string;

  /**
   * 所属类别ID，逻辑关联model_catalog.id
   */
  catalogId?: string | number;

  /**
   * 适用设备类型（冗余，便于查询）
   */
  deviceType?: string;

  /**
   * 统计周期（天）
   */
  statisticsCycleDays?: number;

  /**
   * 状态：草稿/已发布/已归档/已下架
   */
  status?: string;

  /**
   * 注册方式：本地注册/第三方注册
   */
  registerType?: string;

  /**
   * 注册时间
   */
  registerTime?: string;

  /**
   * 模型简介/说明
   */
  description?: string;

  /**
   * 算法脚本存储路径
   */
  scriptPath?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ComparisonRecordVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 操作用户ID（逻辑关联用户表）
   */
  userId: string | number;

  /**
   * 参与比对的设备ID列表（存储设备编码数组）
   */
  deviceIds: string | number;

  /**
   * 参与比对的模型ID列表
   */
  modelIds: string | number;

  /**
   * 设备品种（过滤条件）
   */
  deviceType: string;

  /**
   * 比对状态
   */
  status: string;

  /**
   * 结果摘要（如各模型健康分布统计）
   */
  resultSummary: string;

  /**
   * 比对发起时间
   */
  executedAt: string;

  /**
   * 完成时间
   */
  completedAt: string;

}

export interface ComparisonRecordForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 操作用户ID（逻辑关联用户表）
   */
  userId?: string | number;

  /**
   * 参与比对的设备ID列表（存储设备编码数组）
   */
  deviceIds?: string | number;

  /**
   * 参与比对的模型ID列表
   */
  modelIds?: string | number;

  /**
   * 设备品种（过滤条件）
   */
  deviceType?: string;

  /**
   * 比对状态
   */
  status?: string;

  /**
   * 结果摘要（如各模型健康分布统计）
   */
  resultSummary?: string;

  /**
   * 比对发起时间
   */
  executedAt?: string;

  /**
   * 完成时间
   */
  completedAt?: string;

}

export interface ComparisonRecordQuery extends PageQuery {
  /**
   * 操作用户ID（逻辑关联用户表）
   */
  userId?: string | number;

  /**
   * 参与比对的设备ID列表（存储设备编码数组）
   */
  deviceIds?: string | number;

  /**
   * 参与比对的模型ID列表
   */
  modelIds?: string | number;

  /**
   * 设备品种（过滤条件）
   */
  deviceType?: string;

  /**
   * 比对状态
   */
  status?: string;

  /**
   * 结果摘要（如各模型健康分布统计）
   */
  resultSummary?: string;

  /**
   * 比对发起时间
   */
  executedAt?: string;

  /**
   * 完成时间
   */
  completedAt?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ComparisonDetailVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 比对记录ID，逻辑关联model_comparison_record.id
   */
  recordId: string | number;

  /**
   * 设备编码
   */
  deviceId: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId: string | number;

  /**
   * 健康度得分
   */
  healthScore: number;

  /**
   * 健康等级（healthy/subhealthy/danger）
   */
  healthLevel: string;

  /**
   * 各维度得分（如本体/工况/故障等）
   */
  dimensionScores: string;

}

export interface ComparisonDetailForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 比对记录ID，逻辑关联model_comparison_record.id
   */
  recordId?: string | number;

  /**
   * 设备编码
   */
  deviceId?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 健康度得分
   */
  healthScore?: number;

  /**
   * 健康等级（healthy/subhealthy/danger）
   */
  healthLevel?: string;

  /**
   * 各维度得分（如本体/工况/故障等）
   */
  dimensionScores?: string;

}

export interface ComparisonDetailQuery extends PageQuery {
  /**
   * 比对记录ID，逻辑关联model_comparison_record.id
   */
  recordId?: string | number;

  /**
   * 设备编码
   */
  deviceId?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 健康度得分
   */
  healthScore?: number;

  /**
   * 健康等级（healthy/subhealthy/danger）
   */
  healthLevel?: string;

  /**
   * 各维度得分（如本体/工况/故障等）
   */
  dimensionScores?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ComparisonExecuteForm {
  /**
   * 设备品种（按品种筛选设备）
   */
  deviceType?: string;

  /**
   * 参与比对的设备ID列表
   */
  deviceIds?: Array<string | number>;

  /**
   * 参与比对的模型ID列表（至少2个，最多5个）
   */
  modelIds?: Array<string | number>;
}

export interface ComparisonExecuteModel {
  /**
   * 模型ID
   */
  modelId: string | number;

  /**
   * 模型名称
   */
  modelName: string;
}

export interface ComparisonExecuteDevice {
  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 设备名称
   */
  deviceName: string;

  /**
   * 线路ID
   */
  lineId?: string | number;

  /**
   * 车站ID
   */
  stationId?: string | number;
}

export interface ComparisonExecuteCell {
  /**
   * 设备ID
   */
  deviceId: string | number;

  /**
   * 设备名称
   */
  deviceName: string;

  /**
   * 模型ID
   */
  modelId: string | number;

  /**
   * 健康度得分
   */
  healthScore?: number;

  /**
   * 健康等级（healthy/subhealthy/danger）
   */
  healthLevel?: string;

  /**
   * 本体得分/20
   */
  dimBody?: number;

  /**
   * 工况得分/25
   */
  dimCondition?: number;

  /**
   * 故障得分/25
   */
  dimFault?: number;

  /**
   * 维保得分/20
   */
  dimMaintain?: number;

  /**
   * 环境得分/10
   */
  dimEnv?: number;
}

export interface ComparisonExecuteSummary {
  /**
   * 参与比对的设备总数
   */
  total: number;

  /**
   * 各模型结果一致的设备数
   */
  consistent: number;

  /**
   * 各模型结果存在差异的设备数
   */
  divergent: number;
}

export interface ComparisonExecuteVO {
  /**
   * 比对记录ID
   */
  recordId: string | number;

  /**
   * 设备品种
   */
  deviceType: string;

  /**
   * 参与比对的模型列表
   */
  models: ComparisonExecuteModel[];

  /**
   * 参与比对的设备列表
   */
  devices: ComparisonExecuteDevice[];

  /**
   * 比对明细（设备 × 模型）
   */
  details: ComparisonExecuteCell[];

  /**
   * 结果摘要
   */
  summary: ComparisonExecuteSummary;
}

export interface AnomalyEventVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 设备ID，逻辑关联metro_device.id
   */
  deviceId: string | number;

  /**
   * 设备名称（冗余）
   */
  deviceName: string;

  /**
   * 设备类型：自动扶梯/垂直电梯
   */
  deviceType: string;

  /**
   * 线路ID，逻辑关联metro_line.id（冗余）
   */
  lineId: string | number;

  /**
   * 车站ID，逻辑关联metro_station.id（冗余）
   */
  stationId: string | number;

  /**
   * 线路名称（联表 metro_line.name）
   */
  lineName: string;

  /**
   * 车站名称（联表 metro_station.name）
   */
  stationName: string;

  /**
   * 检测参数名（如电流/电压）
   */
  paramName: string;

  /**
   * 参数单位（如A/V）
   */
  unit: string;

  /**
   * 正常值
   */
  normalValue: number;

  /**
   * 异常值
   */
  abnormalValue: number;

  /**
   * 偏差（百分比）
   */
  deviationPercent: number;

  /**
   * 监测状态：正常/关注/异常
   */
  monitorStatus: string;

  /**
   * 事件发生时间
   */
  eventTime: string;

  /**
   * 来源执行记录ID，逻辑关联model_exec_log.id
   */
  execId: string | number;

  /**
   * 备注
   */
  remark: string;

}

export interface AnomalyEventForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 设备ID，逻辑关联metro_device.id
   */
  deviceId?: string | number;

  /**
   * 设备名称（冗余）
   */
  deviceName?: string;

  /**
   * 设备类型：自动扶梯/垂直电梯
   */
  deviceType?: string;

  /**
   * 线路ID，逻辑关联metro_line.id（冗余）
   */
  lineId?: string | number;

  /**
   * 车站ID，逻辑关联metro_station.id（冗余）
   */
  stationId?: string | number;

  /**
   * 检测参数名（如电流/电压）
   */
  paramName?: string;

  /**
   * 参数单位（如A/V）
   */
  unit?: string;

  /**
   * 正常值
   */
  normalValue?: number;

  /**
   * 异常值
   */
  abnormalValue?: number;

  /**
   * 偏差（百分比）
   */
  deviationPercent?: number;

  /**
   * 监测状态：正常/关注/异常
   */
  monitorStatus?: string;

  /**
   * 事件发生时间
   */
  eventTime?: string;

  /**
   * 来源执行记录ID，逻辑关联model_exec_log.id
   */
  execId?: string | number;

  /**
   * 备注
   */
  remark?: string;

}

export interface AnomalyEventQuery extends PageQuery {
  /**
   * 设备ID，逻辑关联metro_device.id
   */
  deviceId?: string | number;

  /**
   * 设备名称（冗余）
   */
  deviceName?: string;

  /**
   * 设备类型：自动扶梯/垂直电梯
   */
  deviceType?: string;

  /**
   * 线路ID，逻辑关联metro_line.id（冗余）
   */
  lineId?: string | number;

  /**
   * 车站ID，逻辑关联metro_station.id（冗余）
   */
  stationId?: string | number;

  /**
   * 检测参数名（如电流/电压）
   */
  paramName?: string;

  /**
   * 参数单位（如A/V）
   */
  unit?: string;

  /**
   * 正常值
   */
  normalValue?: number;

  /**
   * 异常值
   */
  abnormalValue?: number;

  /**
   * 偏差（百分比）
   */
  deviationPercent?: number;

  /**
   * 监测状态：正常/关注/异常
   */
  monitorStatus?: string;

  /**
   * 事件发生时间
   */
  eventTime?: string;

  /**
   * 来源执行记录ID，逻辑关联model_exec_log.id
   */
  execId?: string | number;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface AnomalyAnalysisTrendPoint {
  /**
   * 监测时间（HH:mm）
   */
  time: string;

  /**
   * 电流（A）
   */
  current: number;
}

export interface AnomalyAnalysisMonitorParam {
  /**
   * 参数名（电流/电压/振动/温度）
   */
  name: string;

  /**
   * 当前值（带单位）
   */
  value: string;

  /**
   * 正常范围（文案）
   */
  range: string;

  /**
   * 监测状态：正常/关注/异常
   */
  status: string;
}

export interface AnomalyAnalysisForeignRule {
  /**
   * 设备类型：1-自动扶梯，2-垂直电梯
   */
  deviceType?: number;

  /**
   * 电流波动阈值（百分比）
   */
  currentThreshold?: number;

  /**
   * 振动突增阈值（百分比）
   */
  vibrationThreshold?: number;

  /**
   * 电流+振动同时异常时的告警级别说明
   */
  alertLevel?: string;

  /**
   * 提前预警分钟数
   */
  advanceMinutes?: number;
}

export interface AnomalyAnalysisVO {
  /**
   * 趋势曲线展示的设备ID
   */
  deviceId?: string;

  /**
   * 趋势曲线展示的设备名称（指定设备时为该设备名，否则为范围内第一台设备）
   */
  deviceName?: string;

  /**
   * 电流/电压趋势曲线（按时间升序，最近 30 个监测点）
   */
  trend?: AnomalyAnalysisTrendPoint[];

  /**
   * 电流峰值偏差百分比
   */
  anomalyPercent?: number;

  /**
   * 电流峰值在趋势序列中的索引
   */
  anomalyIndex?: number;

  /**
   * 实时监测参数面板
   */
  monitorParams?: AnomalyAnalysisMonitorParam[];

  /**
   * 异物检测规则配置（来自 metro_foreign_object_rule）
   */
  rules?: AnomalyAnalysisForeignRule[];
}

export interface DeviceVO {
  /**
    * 主键（设备编号）
    */
  id: string | number;

  /**
   * 设备名称（计算生成：品牌+型号，为空时回退注册代码/设备ID）
   */
  deviceName: string;

  /**
   * 设备类型（metro_device.device_type 映射：1-自动扶梯，2-垂直电梯）
   */
  deviceType: string;

  /**
   * 线路ID，关联line.id
   */
  lineId: string | number;

  /**
   * 车站ID，关联station.id
   */
  stationId: string | number;

  /**
   * 线路名称（联表 metro_line.name）
   */
  lineName: string;

  /**
   * 车站名称（联表 metro_station.name）
   */
  stationName: string;

  /**
   * 设备品种ID，关联base_config
   */
  deviceVarietyCode: string;

  /**
   * 运行状态
   */
  runningStatus: number;

  /**
   * 品牌名称
   */
  brand: string;

  /**
   * 设备注册代码（特种设备登记号）
   */
  registrationCode: string;

  /**
   * 型号规格
   */
  modelSpec: string;

  /**
   * 设备使用地点详细描述
   */
  locationDetail: string;

  /**
   * 接口物业情况
   */
  propertySituation: string;

  /**
   * 制造单位编码，关联base_config
   */
  manufacturerCode: string;

  /**
   * 安装单位编码，关联base_config
   */
  installCompanyCode: string;

  /**
   * 维保单位编码，关联base_config
   */
  maintenanceCompanyCode: string;

  /**
   * 维保合同终止日期
   */
  maintenanceContractEnd: string;

  /**
   * 制造日期
   */
  manufactureDate: string;

  /**
   * 设计使用年限（年）
   */
  designLifeYears: number;

  /**
   * 设备投入使用日期
   */
  commissioningDate: string;

  /**
   * 检验机构名称
   */
  inspectionAgency: string;

  /**
   * 最新检验（检测）日期
   */
  latestInspectionDate: string;

  /**
   * 下次检验（检测）日期
   */
  nextInspectionDate: string;

  /**
   * 负荷试验日期
   */
  loadTestDate: string;

  /**
   * 使用证（检验合格证）编号
   */
  certificateNo: string;

  /**
   * 厂家编号
   */
  manufacturerSerialNo: string;

  /**
   * 内部编号（综合监控设备编码）
   */
  internalNo: string;

  /**
   * 出厂编号
   */
  factorySerialNo: string;

  /**
   * 提升高度（米），仅扶梯
   */
  liftingHeight: number;

  /**
   * 梯级宽度（毫米），仅扶梯
   */
  stepWidth: string | number;

  /**
   * 扶梯倾角（度），仅扶梯
   */
  inclinationAngle: number;

  /**
   * 输送能力（人/小时），仅扶梯
   */
  capacity: number;

  /**
   * 电梯站数，仅电梯
   */
  elevatorStops: number;

  /**
   * 电梯层数，仅电梯
   */
  elevatorFloors: number;

  /**
   * 额定载荷（千克），仅电梯
   */
  ratedLoad: number;

  /**
   * 设计运行速度（米/秒）
   */
  speed: number;

  /**
   * 电机功率（千瓦）
   */
  motorPower: number;

  /**
   * 额定功率
   */
  ratedPower: number;

  /**
   * 额定电压
   */
  ratedVoltage: number;

  /**
   * 振动值设计阈值
   */
  vibrationThreshold: number;

  /**
   * 电机温度设计阈值
   */
  motorTempThreshold: number;

  /**
   * 减速箱温度设计阈值
   */
  gearboxTempThreshold: number;

  /**
   * 修理单位
   */
  repairCompany: string;

  /**
   * 改造单位
   */
  refitCompany: string;

  /**
   * 改造日期
   */
  refitDate: string;

  /**
   * 行政区编码，关联base_config
   */
  districtCode: string;

  /**
   * 街道编码，关联base_config
   */
  streetCode: string;

  /**
   * 责任班组
   */
  dutyTeam: string;

  /**
   * 安全管理人员
   */
  safetyManager: string;

  /**
   * 登记机关
   */
  registrationAuthority: string;

  /**
   * 使用单位
   */
  usingUnit: string;

  /**
   * 备注
   */
  remark: string;

  /**
   * 附件（存储路径或JSON）
   */
  attachment: string;

  /**
   * 维保频率（厂家建议）：半月/月度/季度/半年/年度
   */
  maintenanceFrequency: string;

  /**
   * 维保频率说明（如厂家要求每季度校准参数）
   */
  maintenanceFrequencyDesc: string;

  /**
   * 部件总数量
   */
  totalParts: number;

  /**
   * 环境等级（温湿度、积水、粉尘）：优良/一般/较差/恶劣
   */
  environmentGrade: string;

  /**
   * 最新健康度得分（冗余）
   */
  healthScore: number;

  /**
   * 最新健康等级（冗余）
   */
  healthGradeCode: string;

}

export interface DeviceQuery extends PageQuery {
  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 设备类型，取值与model_catalog.device_type保持一致（如自动扶梯/垂直电梯）
   */
  deviceType?: string;

  /**
   * 线路ID，关联line.id
   */
  lineId?: string | number;

  /**
   * 车站ID，关联station.id
   */
  stationId?: string | number;

  /**
   * 设备品种ID，关联base_config
   */
  deviceVarietyCode?: string;

  /**
   * 运行状态
   */
  runningStatus?: number;

  /**
   * 品牌名称
   */
  brand?: string;

  /**
   * 设备注册代码（特种设备登记号）
   */
  registrationCode?: string;

  /**
   * 型号规格
   */
  modelSpec?: string;

  /**
   * 设备使用地点详细描述
   */
  locationDetail?: string;

  /**
   * 接口物业情况
   */
  propertySituation?: string;

  /**
   * 制造单位编码，关联base_config
   */
  manufacturerCode?: string;

  /**
   * 安装单位编码，关联base_config
   */
  installCompanyCode?: string;

  /**
   * 维保单位编码，关联base_config
   */
  maintenanceCompanyCode?: string;

  /**
   * 维保合同终止日期
   */
  maintenanceContractEnd?: string;

  /**
   * 制造日期
   */
  manufactureDate?: string;

  /**
   * 设计使用年限（年）
   */
  designLifeYears?: number;

  /**
   * 设备投入使用日期
   */
  commissioningDate?: string;

  /**
   * 检验机构名称
   */
  inspectionAgency?: string;

  /**
   * 最新检验（检测）日期
   */
  latestInspectionDate?: string;

  /**
   * 下次检验（检测）日期
   */
  nextInspectionDate?: string;

  /**
   * 负荷试验日期
   */
  loadTestDate?: string;

  /**
   * 使用证（检验合格证）编号
   */
  certificateNo?: string;

  /**
   * 厂家编号
   */
  manufacturerSerialNo?: string;

  /**
   * 内部编号（综合监控设备编码）
   */
  internalNo?: string;

  /**
   * 出厂编号
   */
  factorySerialNo?: string;

  /**
   * 提升高度（米），仅扶梯
   */
  liftingHeight?: number;

  /**
   * 梯级宽度（毫米），仅扶梯
   */
  stepWidth?: string | number;

  /**
   * 扶梯倾角（度），仅扶梯
   */
  inclinationAngle?: number;

  /**
   * 输送能力（人/小时），仅扶梯
   */
  capacity?: number;

  /**
   * 电梯站数，仅电梯
   */
  elevatorStops?: number;

  /**
   * 电梯层数，仅电梯
   */
  elevatorFloors?: number;

  /**
   * 额定载荷（千克），仅电梯
   */
  ratedLoad?: number;

  /**
   * 设计运行速度（米/秒）
   */
  speed?: number;

  /**
   * 电机功率（千瓦）
   */
  motorPower?: number;

  /**
   * 额定功率
   */
  ratedPower?: number;

  /**
   * 额定电压
   */
  ratedVoltage?: number;

  /**
   * 振动值设计阈值
   */
  vibrationThreshold?: number;

  /**
   * 电机温度设计阈值
   */
  motorTempThreshold?: number;

  /**
   * 减速箱温度设计阈值
   */
  gearboxTempThreshold?: number;

  /**
   * 修理单位
   */
  repairCompany?: string;

  /**
   * 改造单位
   */
  refitCompany?: string;

  /**
   * 改造日期
   */
  refitDate?: string;

  /**
   * 行政区编码，关联base_config
   */
  districtCode?: string;

  /**
   * 街道编码，关联base_config
   */
  streetCode?: string;

  /**
   * 责任班组
   */
  dutyTeam?: string;

  /**
   * 安全管理人员
   */
  safetyManager?: string;

  /**
   * 登记机关
   */
  registrationAuthority?: string;

  /**
   * 使用单位
   */
  usingUnit?: string;

  /**
   * 附件（存储路径或JSON）
   */
  attachment?: string;

  /**
   * 维保频率（厂家建议）：半月/月度/季度/半年/年度
   */
  maintenanceFrequency?: string;

  /**
   * 维保频率说明（如厂家要求每季度校准参数）
   */
  maintenanceFrequencyDesc?: string;

  /**
   * 部件总数量
   */
  totalParts?: number;

  /**
   * 环境等级（温湿度、积水、粉尘）：优良/一般/较差/恶劣
   */
  environmentGrade?: string;

  /**
   * 最新健康度得分（冗余）
   */
  healthScore?: number;

  /**
   * 最新健康等级（冗余）
   */
  healthGradeCode?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ExecLogVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 执行编号（如EXE20260921020000001）
   */
  execNo: string;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId: string | number;

  /**
   * 模型名称（冗余，便于查询/列表展示）
   */
  modelName: string;

  /**
   * 任务ID，逻辑关联model_task.id；单次运行/比对时为空
   */
  taskId: string | number;

  /**
   * 触发方式：定时/单次运行/立即执行/数据比对
   */
  triggerType: string;

  /**
   * 执行状态：运行中/成功/部分成功/失败/超时
   */
  status: string;

  /**
   * 开始时间
   */
  startTime: string;

  /**
   * 结束时间
   */
  endTime: string;

  /**
   * 耗时（毫秒）
   */
  costMs: number;

  /**
   * 涉及设备数
   */
  deviceCount: number;

  /**
   * 成功设备数
   */
  successCount: number;

  /**
   * 失败设备数
   */
  failCount: number;

  /**
   * 本次执行入参快照
   */
  execParams: string;

  /**
   * 本次使用的脚本版本ID，逻辑关联model_script.id
   */
  scriptId: string | number;

  /**
   * 错误信息
   */
  errorMsg: string;

  /**
   * 执行人ID（手动触发时记录）
   */
  executorId: string | number;

}

export interface ExecLogForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 执行编号（如EXE20260921020000001）
   */
  execNo?: string;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 模型名称（冗余，便于查询/列表展示）
   */
  modelName?: string;

  /**
   * 任务ID，逻辑关联model_task.id；单次运行/比对时为空
   */
  taskId?: string | number;

  /**
   * 触发方式：定时/单次运行/立即执行/数据比对
   */
  triggerType?: string;

  /**
   * 执行状态：运行中/成功/部分成功/失败/超时
   */
  status?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 耗时（毫秒）
   */
  costMs?: number;

  /**
   * 涉及设备数
   */
  deviceCount?: number;

  /**
   * 成功设备数
   */
  successCount?: number;

  /**
   * 失败设备数
   */
  failCount?: number;

  /**
   * 本次执行入参快照
   */
  execParams?: string;

  /**
   * 本次使用的脚本版本ID，逻辑关联model_script.id
   */
  scriptId?: string | number;

  /**
   * 错误信息
   */
  errorMsg?: string;

  /**
   * 执行人ID（手动触发时记录）
   */
  executorId?: string | number;

}

export interface ExecLogQuery extends PageQuery {
  /**
   * 执行编号（如EXE20260921020000001）
   */
  execNo?: string;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 模型名称（冗余，便于查询/列表展示）
   */
  modelName?: string;

  /**
   * 任务ID，逻辑关联model_task.id；单次运行/比对时为空
   */
  taskId?: string | number;

  /**
   * 触发方式：定时/单次运行/立即执行/数据比对
   */
  triggerType?: string;

  /**
   * 执行状态：运行中/成功/部分成功/失败/超时
   */
  status?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 耗时（毫秒）
   */
  costMs?: number;

  /**
   * 涉及设备数
   */
  deviceCount?: number;

  /**
   * 成功设备数
   */
  successCount?: number;

  /**
   * 失败设备数
   */
  failCount?: number;

  /**
   * 本次执行入参快照
   */
  execParams?: string;

  /**
   * 本次使用的脚本版本ID，逻辑关联model_script.id
   */
  scriptId?: string | number;

  /**
   * 错误信息
   */
  errorMsg?: string;

  /**
   * 执行人ID（手动触发时记录）
   */
  executorId?: string | number;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ExecLogDetailVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 执行记录ID，逻辑关联model_exec_log.id
   */
  execId: string | number;

  /**
   * 日志行序号（同一exec内自增）
   */
  seq: number;

  /**
   * 日志时间
   */
  logTime: string;

  /**
   * 日志级别
   */
  level: string;

  /**
   * 日志内容
   */
  message: string;

}

export interface ExecLogDetailForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 执行记录ID，逻辑关联model_exec_log.id
   */
  execId?: string | number;

  /**
   * 日志行序号（同一exec内自增）
   */
  seq?: number;

  /**
   * 日志时间
   */
  logTime?: string;

  /**
   * 日志级别
   */
  level?: string;

  /**
   * 日志内容
   */
  message?: string;

}

export interface ExecLogDetailQuery extends PageQuery {
  /**
   * 执行记录ID，逻辑关联model_exec_log.id
   */
  execId?: string | number;

  /**
   * 日志行序号（同一exec内自增）
   */
  seq?: number;

  /**
   * 日志时间
   */
  logTime?: string;

  /**
   * 日志级别
   */
  level?: string;

  /**
   * 日志内容
   */
  message?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface LineVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 线路名称，如“1号线”
   */
  name: string;

  /**
   * 线路业务编码，如“LINE_01”
   */
  code: string;

  /**
   * 所属客运公司ID
   */
  companyId: string | number;

  /**
   * 排序权重，数字越小越靠前
   */
  sortOrder: number;

}

export interface LineForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 线路名称，如“1号线”
   */
  name?: string;

  /**
   * 线路业务编码，如“LINE_01”
   */
  code?: string;

  /**
   * 所属客运公司ID
   */
  companyId?: string | number;

  /**
   * 排序权重，数字越小越靠前
   */
  sortOrder?: number;

}

export interface LineQuery extends PageQuery {
  /**
   * 线路名称，如“1号线”
   */
  name?: string;

  /**
   * 线路业务编码，如“LINE_01”
   */
  code?: string;

  /**
   * 所属客运公司ID
   */
  companyId?: string | number;

  /**
   * 排序权重，数字越小越靠前
   */
  sortOrder?: number;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface StationVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 所属线路ID，逻辑关联metro_line.id
   */
  lineId: string | number;

  /**
   * 所属线路名称（联表 metro_line.name）
   */
  lineName: string;

  /**
   * 车站名称，如“罗湖站”
   */
  name: string;

  /**
   * 车站业务编码
   */
  code: string;

  /**
   * 车站序号
   */
  stationNo: number;

  /**
   * 车站类型
   */
  stationType: string;

  /**
   * 是否换乘站:0否，1是
   */
  isChange: string;

  /**
   * 行政区编码，关联base_config
   */
  districtCode: string;

  /**
   * 街道编码，关联base_config
   */
  streetCode: string;

  /**
   * 排序权重
   */
  sortOrder: number;

}

export interface StationForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 所属线路ID，逻辑关联metro_line.id
   */
  lineId?: string | number;

  /**
   * 车站名称，如“罗湖站”
   */
  name?: string;

  /**
   * 车站业务编码
   */
  code?: string;

  /**
   * 车站序号
   */
  stationNo?: number;

  /**
   * 车站类型
   */
  stationType?: string;

  /**
   * 是否换乘站:0否，1是
   */
  isChange?: string;

  /**
   * 行政区编码，关联base_config
   */
  districtCode?: string;

  /**
   * 街道编码，关联base_config
   */
  streetCode?: string;

  /**
   * 排序权重
   */
  sortOrder?: number;

}

export interface StationQuery extends PageQuery {
  /**
   * 所属线路ID，逻辑关联metro_line.id
   */
  lineId?: string | number;

  /**
   * 车站名称，如“罗湖站”
   */
  name?: string;

  /**
   * 车站业务编码
   */
  code?: string;

  /**
   * 车站序号
   */
  stationNo?: number;

  /**
   * 车站类型
   */
  stationType?: string;

  /**
   * 是否换乘站:0否，1是
   */
  isChange?: string;

  /**
   * 行政区编码，关联base_config
   */
  districtCode?: string;

  /**
   * 街道编码，关联base_config
   */
  streetCode?: string;

  /**
   * 排序权重
   */
  sortOrder?: number;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ParamVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId: string | number;

  /**
   * 参数方向：输入/输出
   */
  paramDirection: string;

  /**
   * 参数标识（如 window_days）
   */
  paramKey: string;

  /**
   * 参数显示名
   */
  paramName: string;

  /**
   * 参数类型：数值/文本/布尔
   */
  paramType: string;

  /**
   * 是否必填（1是 0否）
   */
  required: string;

  /**
   * 默认值
   */
  defaultValue: string;

  /**
   * 参数说明
   */
  description: string;

  /**
   * 排序号
   */
  sortOrder: number;

}

export interface ParamForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 参数方向：输入/输出
   */
  paramDirection?: string;

  /**
   * 参数标识（如 window_days）
   */
  paramKey?: string;

  /**
   * 参数显示名
   */
  paramName?: string;

  /**
   * 参数类型：数值/文本/布尔
   */
  paramType?: string;

  /**
   * 是否必填（1是 0否）
   */
  required?: string;

  /**
   * 默认值
   */
  defaultValue?: string;

  /**
   * 参数说明
   */
  description?: string;

  /**
   * 排序号
   */
  sortOrder?: number;

}

export interface ParamQuery extends PageQuery {
  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 参数方向：输入/输出
   */
  paramDirection?: string;

  /**
   * 参数标识（如 window_days）
   */
  paramKey?: string;

  /**
   * 参数显示名
   */
  paramName?: string;

  /**
   * 参数类型：数值/文本/布尔
   */
  paramType?: string;

  /**
   * 是否必填（1是 0否）
   */
  required?: string;

  /**
   * 默认值
   */
  defaultValue?: string;

  /**
   * 参数说明
   */
  description?: string;

  /**
   * 排序号
   */
  sortOrder?: number;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ResultVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 执行记录ID，逻辑关联model_exec_log.id
   */
  execId: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId: string | number;

  /**
   * 设备ID，逻辑关联metro_device.id
   */
  deviceId: string | number;

  /**
   * 设备名称（冗余）
   */
  deviceName: string;

  /**
   * 线路ID，逻辑关联metro_line.id（冗余）
   */
  lineId: string | number;

  /**
   * 车站ID，逻辑关联metro_station.id（冗余）
   */
  stationId: string | number;

  /**
   * 线路名称（联表 metro_line.name）
   */
  lineName: string;

  /**
   * 车站名称（联表 metro_station.name）
   */
  stationName: string;

  /**
   * 设备类型（冗余）
   */
  deviceType: string;

  /**
   * 健康总分（0-100）
   */
  healthScore: number;

  /**
   * 健康等级 (healthy/subhealthy/danger)
   */
  healthLevel: string;

  /**
   * 维度分-本体
   */
  dimBody: number;

  /**
   * 维度分-状态
   */
  dimCondition: number;

  /**
   * 维度分-故障
   */
  dimFault: number;

  /**
   * 维度分-维护
   */
  dimMaintain: number;

  /**
   * 维度分-环境
   */
  dimEnv: number;

  /**
   * 输出参数结果（JSON 字符串，key = 出参 paramKey）
   */
  outputs?: string;

}

export interface ResultForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 执行记录ID，逻辑关联model_exec_log.id
   */
  execId?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 设备ID，逻辑关联metro_device.id
   */
  deviceId?: string | number;

  /**
   * 设备名称（冗余）
   */
  deviceName?: string;

  /**
   * 线路ID，逻辑关联metro_line.id（冗余）
   */
  lineId?: string | number;

  /**
   * 车站ID，逻辑关联metro_station.id（冗余）
   */
  stationId?: string | number;

  /**
   * 设备类型（冗余）
   */
  deviceType?: string;

  /**
   * 健康总分（0-100）
   */
  healthScore?: number;

  /**
   * 健康等级 (healthy/subhealthy/danger)
   */
  healthLevel?: string;

  /**
   * 维度分-本体
   */
  dimBody?: number;

  /**
   * 维度分-状态
   */
  dimCondition?: number;

  /**
   * 维度分-故障
   */
  dimFault?: number;

  /**
   * 维度分-维护
   */
  dimMaintain?: number;

  /**
   * 维度分-环境
   */
  dimEnv?: number;

}

export interface ResultQuery extends PageQuery {
  /**
   * 执行记录ID，逻辑关联model_exec_log.id
   */
  execId?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 设备ID，逻辑关联metro_device.id
   */
  deviceId?: string | number;

  /**
   * 设备名称（冗余）
   */
  deviceName?: string;

  /**
   * 线路ID，逻辑关联metro_line.id（冗余）
   */
  lineId?: string | number;

  /**
   * 车站ID，逻辑关联metro_station.id（冗余）
   */
  stationId?: string | number;

  /**
   * 设备类型（冗余）
   */
  deviceType?: string;

  /**
   * 健康总分（0-100）
   */
  healthScore?: number;

  /**
   * 健康等级 (healthy/subhealthy/danger)
   */
  healthLevel?: string;

  /**
   * 维度分-本体
   */
  dimBody?: number;

  /**
   * 维度分-状态
   */
  dimCondition?: number;

  /**
   * 维度分-故障
   */
  dimFault?: number;

  /**
   * 维度分-维护
   */
  dimMaintain?: number;

  /**
   * 维度分-环境
   */
  dimEnv?: number;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface ScriptVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId: string | number;

  /**
   * 存储文件名
   */
  fileName: string;

  /**
   * 上传时的原始文件名
   */
  originalName: string;

  /**
   * 文件后缀
   */
  fileSuffix: string;

  /**
   * 文件大小（字节）
   */
  fileSize: number;

  /**
   * OSS对象存储ID，逻辑关联sys_oss.oss_id
   */
  ossId: string | number;

  /**
   * 脚本访问地址（未启用OSS时的兜底存储路径）
   */
  scriptUrl: string;

  /**
   * 脚本SHA-256校验值
   */
  sha256: string;

  /**
   * 入口函数名，平台约定为execute
   */
  entryFunction: string;

  /**
   * 脚本版本号
   */
  scriptVersion: string;

  /**
   * requirements.txt的OSS ID
   */
  requirementsOssId: string | number;

  /**
   * 校验状态：待校验/通过/失败
   */
  checkStatus: string;

  /**
   * 校验结果说明
   */
  checkMessage: string;

  /**
   * 是否当前生效版本（1是 0否）
   */
  isCurrent: string;

}

export interface ScriptForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 存储文件名
   */
  fileName?: string;

  /**
   * 上传时的原始文件名
   */
  originalName?: string;

  /**
   * 文件后缀
   */
  fileSuffix?: string;

  /**
   * 文件大小（字节）
   */
  fileSize?: number;

  /**
   * OSS对象存储ID，逻辑关联sys_oss.oss_id
   */
  ossId?: string | number;

  /**
   * 脚本访问地址（未启用OSS时的兜底存储路径）
   */
  scriptUrl?: string;

  /**
   * 脚本SHA-256校验值
   */
  sha256?: string;

  /**
   * 入口函数名，平台约定为execute
   */
  entryFunction?: string;

  /**
   * 脚本版本号
   */
  scriptVersion?: string;

  /**
   * requirements.txt的OSS ID
   */
  requirementsOssId?: string | number;

  /**
   * 校验状态：待校验/通过/失败
   */
  checkStatus?: string;

  /**
   * 校验结果说明
   */
  checkMessage?: string;

  /**
   * 是否当前生效版本（1是 0否）
   */
  isCurrent?: string;

}

export interface ScriptQuery extends PageQuery {
  /**
   * 模型ID，逻辑关联model_definition.id
   */
  modelId?: string | number;

  /**
   * 存储文件名
   */
  fileName?: string;

  /**
   * 上传时的原始文件名
   */
  originalName?: string;

  /**
   * 文件后缀
   */
  fileSuffix?: string;

  /**
   * 文件大小（字节）
   */
  fileSize?: number;

  /**
   * OSS对象存储ID，逻辑关联sys_oss.oss_id
   */
  ossId?: string | number;

  /**
   * 脚本访问地址（未启用OSS时的兜底存储路径）
   */
  scriptUrl?: string;

  /**
   * 脚本SHA-256校验值
   */
  sha256?: string;

  /**
   * 入口函数名，平台约定为execute
   */
  entryFunction?: string;

  /**
   * 脚本版本号
   */
  scriptVersion?: string;

  /**
   * requirements.txt的OSS ID
   */
  requirementsOssId?: string | number;

  /**
   * 校验状态：待校验/通过/失败
   */
  checkStatus?: string;

  /**
   * 校验结果说明
   */
  checkMessage?: string;

  /**
   * 是否当前生效版本（1是 0否）
   */
  isCurrent?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}

export interface TaskParamVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 任务ID，逻辑关联model_task.id
   */
  taskId: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id（冗余便于查询）
   */
  modelId: string | number;

  /**
   * 参数标识，对应model_param.param_key
   */
  paramKey: string;

  /**
   * 参数值（留空则使用model_param.default_value）
   */
  paramValue: string;

}

export interface TaskParamForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 任务ID，逻辑关联model_task.id
   */
  taskId?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id（冗余便于查询）
   */
  modelId?: string | number;

  /**
   * 参数标识，对应model_param.param_key
   */
  paramKey?: string;

  /**
   * 参数值（留空则使用model_param.default_value）
   */
  paramValue?: string;

}

export interface TaskParamQuery extends PageQuery {
  /**
   * 任务ID，逻辑关联model_task.id
   */
  taskId?: string | number;

  /**
   * 模型ID，逻辑关联model_definition.id（冗余便于查询）
   */
  modelId?: string | number;

  /**
   * 参数标识，对应model_param.param_key
   */
  paramKey?: string;

  /**
   * 参数值（留空则使用model_param.default_value）
   */
  paramValue?: string;

  /**
    * 日期范围参数
    */
  params?: any;
}