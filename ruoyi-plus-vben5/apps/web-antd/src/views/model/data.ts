import { ref } from 'vue';

/** 数据建模模块共享 Mock 数据（模型概览 / 模型管理 / 数据比对 / 模型任务 / 模型类别 五页共用） */

export type ModelStatus = '已发布' | '已下架' | '草稿';
export type RegisterType = '第三方注册' | '本地注册';
export type ModelParamType = '布尔' | '文本' | '数值';
export type TaskStatus = '暂停' | '运行中';
export type ExecTriggerType = '数据比对' | '定时' | '单次运行' | '立即执行';
export type ExecStatus = '部分成功' | '成功' | '失败' | '运行中' | '超时';
export type HealthLevel = '亚健康' | '健康' | '高危';
export type DeviceTypeOption =
  | '供电设备'
  | '水泵'
  | '通风设备'
  | '屏蔽门'
  | '垂直电梯'
  | '自动扶梯';

export interface ModelCategory {
  id: number;
  categoryName: string;
  deviceType: DeviceTypeOption;
  status: '停用' | '启用';
  remark: string;
  createTime: string;
}

export interface ModelRecord {
  id: number;
  modelName: string;
  categoryId: number;
  registerType: RegisterType;
  statPeriod: number;
  status: ModelStatus;
  enabled: boolean;
  description: string;
  registerTime: string;
  lastRunTime: string;
  lastRunStatus: '' | ExecStatus;
}

export interface ModelParam {
  paramKey: string;
  paramName: string;
  paramType: ModelParamType;
  required: boolean;
  defaultValue: string;
  description: string;
}

export interface ModelTaskRecord {
  id: number;
  taskName: string;
  modelId: number;
  cronDesc: string;
  lastRunTime: string;
  nextExecTime: string;
  status: TaskStatus;
  remark: string;
}

export interface ModelExecRecord {
  execNo: string;
  modelId: number;
  taskId: null | number;
  triggerType: ExecTriggerType;
  status: ExecStatus;
  startTime: string;
  endTime: string;
  costMs: number;
  deviceCount: number;
  errorMsg: string;
}

export interface DeviceRecord {
  id: string;
  name: string;
  line: string;
  station: string;
  type: DeviceTypeOption;
}

export interface ModelResultRow {
  deviceId: string;
  deviceName: string;
  line: string;
  station: string;
  deviceType: DeviceTypeOption;
  healthLevel: HealthLevel;
  totalScore: number;
  dimBody: number;
  dimCondition: number;
  dimFault: number;
  dimMaintain: number;
  dimEnv: number;
}

export const modelStatusMeta: Record<ModelStatus, { label: ModelStatus; status: 'default' | 'success' | 'warning' }> = {
  已发布: { label: '已发布', status: 'success' },
  已下架: { label: '已下架', status: 'default' },
  草稿: { label: '草稿', status: 'warning' },
};

export const execStatusMeta: Record<ExecStatus, { label: ExecStatus; status: 'default' | 'error' | 'processing' | 'success' | 'warning' }> = {
  部分成功: { label: '部分成功', status: 'warning' },
  成功: { label: '成功', status: 'success' },
  失败: { label: '失败', status: 'error' },
  运行中: { label: '运行中', status: 'processing' },
  超时: { label: '超时', status: 'error' },
};

export const taskStatusMeta: Record<TaskStatus, { label: TaskStatus; status: 'default' | 'processing' }> = {
  暂停: { label: '暂停', status: 'default' },
  运行中: { label: '运行中', status: 'processing' },
};

export const healthLevelMeta: Record<HealthLevel, { label: HealthLevel; status: 'error' | 'success' | 'warning' }> = {
  亚健康: { label: '亚健康', status: 'warning' },
  健康: { label: '健康', status: 'success' },
  高危: { label: '高危', status: 'error' },
};

export const statPeriodOptions = [30, 60, 90, 120, 180, 270, 360].map((value) => ({
  label: `${value} 天`,
  value: String(value),
}));

export const registerTypeOptions = [
  { label: '本地注册', value: '本地注册' },
  { label: '第三方注册', value: '第三方注册' },
];

export const deviceTypeOptions: { label: DeviceTypeOption; value: DeviceTypeOption }[] = [
  { label: '自动扶梯', value: '自动扶梯' },
  { label: '垂直电梯', value: '垂直电梯' },
  { label: '屏蔽门', value: '屏蔽门' },
  { label: '水泵', value: '水泵' },
  { label: '通风设备', value: '通风设备' },
  { label: '供电设备', value: '供电设备' },
];

export const cronPresetOptions = [
  '每日 02:00',
  '每日 03:00',
  '每日 06:00',
  '每周一 03:00',
  '每周一 06:00',
  '每周三 04:00',
  '每月1日 02:00',
  '自定义 Cron',
].map((item) => ({ label: item, value: item }));

// ==================== 模型类别 ====================
export const categories = ref<ModelCategory[]>([
  {
    id: 1,
    categoryName: '自动扶梯',
    deviceType: '自动扶梯',
    status: '启用',
    remark: '覆盖自动扶梯健康度与振动分析类模型',
    createTime: '2026-06-12 10:20:00',
  },
  {
    id: 2,
    categoryName: '垂直电梯',
    deviceType: '垂直电梯',
    status: '启用',
    remark: '垂直电梯评估与载荷分析类模型',
    createTime: '2026-06-12 10:26:00',
  },
  {
    id: 3,
    categoryName: '屏蔽门',
    deviceType: '屏蔽门',
    status: '启用',
    remark: '站台屏蔽门故障预警类模型',
    createTime: '2026-07-01 09:00:00',
  },
]);

// ==================== 模型列表 ====================
export const models = ref<ModelRecord[]>([
  {
    id: 1,
    modelName: '自动扶梯健康度评估模型v2',
    categoryId: 1,
    registerType: '本地注册',
    statPeriod: 90,
    status: '已发布',
    enabled: true,
    description: '基于90天监测数据，从本体、工况、故障、维保、环境五个维度评估自动扶梯健康度。',
    registerTime: '2026-08-05 14:30:00',
    lastRunTime: '2026-09-21 02:00:00',
    lastRunStatus: '成功',
  },
  {
    id: 2,
    modelName: '自动扶梯振动分析模型',
    categoryId: 1,
    registerType: '本地注册',
    statPeriod: 60,
    status: '草稿',
    enabled: false,
    description: '通过振动频谱特征识别扶梯梯级链与主驱动异常。',
    registerTime: '2026-09-10 11:02:00',
    lastRunTime: '',
    lastRunStatus: '',
  },
  {
    id: 3,
    modelName: '垂直电梯评估模型v3',
    categoryId: 2,
    registerType: '本地注册',
    statPeriod: 90,
    status: '已发布',
    enabled: true,
    description: '综合曳引系统、门系统与运行工况的垂直电梯健康度评估。',
    registerTime: '2026-08-18 16:40:00',
    lastRunTime: '2026-09-15 03:00:00',
    lastRunStatus: '成功',
  },
  {
    id: 4,
    modelName: '垂直电梯载荷分析模型',
    categoryId: 2,
    registerType: '第三方注册',
    statPeriod: 60,
    status: '草稿',
    enabled: false,
    description: '登记自集团算法中台，按载荷分布评估电梯疲劳程度。',
    registerTime: '2026-09-14 10:15:00',
    lastRunTime: '',
    lastRunStatus: '',
  },
  {
    id: 5,
    modelName: '屏蔽门故障预警模型',
    categoryId: 3,
    registerType: '第三方注册',
    statPeriod: 30,
    status: '已下架',
    enabled: false,
    description: '基于开关门曲线与故障码预测屏蔽门近期故障概率。',
    registerTime: '2026-07-20 09:30:00',
    lastRunTime: '2026-08-30 02:00:00',
    lastRunStatus: '超时',
  },
]);

// ==================== 模型参数（8.4 model_param） ====================
const defaultDimOutput: ModelParam[] = [
  { paramKey: 'health_level', paramName: '健康等级', paramType: '文本', required: true, defaultValue: '', description: '健康 / 亚健康 / 高危' },
  { paramKey: 'total_score', paramName: '总分', paramType: '数值', required: true, defaultValue: '', description: '百分制总分' },
  { paramKey: 'dim_body', paramName: '本体得分', paramType: '数值', required: true, defaultValue: '', description: '本体维度（20分）' },
  { paramKey: 'dim_condition', paramName: '工况得分', paramType: '数值', required: true, defaultValue: '', description: '工况维度（25分）' },
  { paramKey: 'dim_fault', paramName: '故障得分', paramType: '数值', required: true, defaultValue: '', description: '故障维度（25分）' },
  { paramKey: 'dim_maintain', paramName: '维保得分', paramType: '数值', required: true, defaultValue: '', description: '维保维度（20分）' },
  { paramKey: 'dim_env', paramName: '环境得分', paramType: '数值', required: true, defaultValue: '', description: '环境维度（10分）' },
];

export const modelParams = ref<Record<number, { input: ModelParam[]; output: ModelParam[] }>>({
  1: {
    input: [
      { paramKey: 'window_days', paramName: '统计窗口天数', paramType: '数值', required: true, defaultValue: '90', description: '回溯统计的监测数据窗口' },
      { paramKey: 'vibration_threshold', paramName: '振动告警阈值', paramType: '数值', required: false, defaultValue: '3.5', description: 'mm/s，超过判定为异常' },
      { paramKey: 'ignore_maintenance', paramName: '忽略维保期数据', paramType: '布尔', required: false, defaultValue: 'false', description: '维保窗口内数据不参与扣分' },
    ],
    output: [...defaultDimOutput],
  },
  2: {
    input: [
      { paramKey: 'window_days', paramName: '统计窗口天数', paramType: '数值', required: true, defaultValue: '60', description: '回溯统计的监测数据窗口' },
    ],
    output: [
      { paramKey: 'health_level', paramName: '健康等级', paramType: '文本', required: true, defaultValue: '', description: '健康 / 亚健康 / 高危' },
      { paramKey: 'total_score', paramName: '总分', paramType: '数值', required: true, defaultValue: '', description: '百分制总分' },
    ],
  },
  3: {
    input: [
      { paramKey: 'window_days', paramName: '统计窗口天数', paramType: '数值', required: true, defaultValue: '90', description: '回溯统计的监测数据窗口' },
      { paramKey: 'load_ratio_threshold', paramName: '载荷比阈值', paramType: '数值', required: false, defaultValue: '0.85', description: '满载率超过阈值判定高频运行' },
    ],
    output: [...defaultDimOutput],
  },
  4: {
    input: [
      { paramKey: 'window_days', paramName: '统计窗口天数', paramType: '数值', required: true, defaultValue: '60', description: '回溯统计的监测数据窗口' },
    ],
    output: [
      { paramKey: 'health_level', paramName: '健康等级', paramType: '文本', required: true, defaultValue: '', description: '健康 / 亚健康 / 高危' },
    ],
  },
  5: {
    input: [
      { paramKey: 'window_days', paramName: '统计窗口天数', paramType: '数值', required: true, defaultValue: '30', description: '回溯统计的监测数据窗口' },
    ],
    output: [
      { paramKey: 'health_level', paramName: '健康等级', paramType: '文本', required: true, defaultValue: '', description: '健康 / 亚健康 / 高危' },
      { paramKey: 'total_score', paramName: '总分', paramType: '数值', required: true, defaultValue: '', description: '百分制总分' },
    ],
  },
});

export function getModelParams(modelId: number) {
  return modelParams.value[modelId] ?? { input: [], output: [] };
}

export function setModelParams(modelId: number, params: { input: ModelParam[]; output: ModelParam[] }) {
  modelParams.value[modelId] = params;
}

// ==================== 设备清单（线路 → 车站） ====================
export const devices = ref<DeviceRecord[]>([
  { id: 'X-01', name: '1号线人民广场站上行扶梯', line: '1号线', station: '人民广场站', type: '自动扶梯' },
  { id: 'X-02', name: '1号线人民广场站下行扶梯', line: '1号线', station: '人民广场站', type: '自动扶梯' },
  { id: 'X-03', name: '1号线火车站站上行扶梯', line: '1号线', station: '火车站站', type: '自动扶梯' },
  { id: 'X-04', name: '2号线体育中心站上行扶梯', line: '2号线', station: '体育中心站', type: '自动扶梯' },
  { id: 'X-05', name: '2号线科技园站下行扶梯', line: '2号线', station: '科技园站', type: '自动扶梯' },
  { id: 'X-06', name: '3号线机场站上行扶梯', line: '3号线', station: '机场站', type: '自动扶梯' },
  { id: 'T-01', name: '1号线人民广场站客梯', line: '1号线', station: '人民广场站', type: '垂直电梯' },
  { id: 'T-02', name: '1号线火车站站货梯', line: '1号线', station: '火车站站', type: '垂直电梯' },
  { id: 'T-03', name: '2号线体育中心站客梯', line: '2号线', station: '体育中心站', type: '垂直电梯' },
  { id: 'T-04', name: '2号线科技园站客梯', line: '2号线', station: '科技园站', type: '垂直电梯' },
  { id: 'T-05', name: '3号线东湖站客梯', line: '3号线', station: '东湖站', type: '垂直电梯' },
  { id: 'T-06', name: '3号线机场站货梯', line: '3号线', station: '机场站', type: '垂直电梯' },
]);

export function getDevicesByType(type: DeviceTypeOption) {
  return devices.value.filter((item) => item.type === type);
}

export function getCategoryDeviceType(categoryId: number): DeviceTypeOption | '' {
  return categories.value.find((item) => item.id === categoryId)?.deviceType ?? '';
}

// ==================== 模型结果（查看结果 / 数据比对 共用） ====================
const deviceBaseScore: Record<string, number> = {
  'X-01': 92,
  'X-02': 74,
  'X-03': 55,
  'X-04': 95,
  'X-05': 81,
  'X-06': 63,
  'T-01': 88,
  'T-02': 47,
  'T-03': 91,
  'T-04': 69,
  'T-05': 58,
  'T-06': 84,
};

const modelScoreOffset: Record<number, number> = {
  1: 0,
  3: -6,
  5: 4,
};

function toLevel(score: number): HealthLevel {
  if (score >= 90) return '健康';
  if (score >= 60) return '亚健康';
  return '高危';
}

export function getModelResultRows(modelId: number): ModelResultRow[] {
  const deviceType = getCategoryDeviceType(modelId);
  const offset = modelScoreOffset[modelId] ?? 0;

  return devices.value
    .filter((item) => !deviceType || item.type === deviceType)
    .map((device) => {
      const total = Math.max(35, Math.min(98, (deviceBaseScore[device.id] ?? 70) + offset));
      const ratio = total / 100;

      return {
        deviceId: device.id,
        deviceName: device.name,
        line: device.line,
        station: device.station,
        deviceType: device.type,
        healthLevel: toLevel(total),
        totalScore: total,
        dimBody: Math.round(20 * ratio),
        dimCondition: Math.round(25 * ratio),
        dimFault: Math.round(25 * ratio),
        dimMaintain: Math.round(20 * ratio),
        dimEnv: Math.round(10 * ratio),
      };
    });
}

// ==================== 模型任务（8.5 model_task） ====================
export const tasks = ref<ModelTaskRecord[]>([
  {
    id: 1,
    taskName: '扶梯健康度评估任务',
    modelId: 1,
    cronDesc: '每日 02:00',
    lastRunTime: '2026-09-21 02:00:00',
    nextExecTime: '2026-09-22 02:00:00',
    status: '运行中',
    remark: '自动扶梯健康度日常评估',
  },
  {
    id: 2,
    taskName: '垂直电梯评估任务',
    modelId: 3,
    cronDesc: '每周一 03:00',
    lastRunTime: '2026-09-15 03:00:00',
    nextExecTime: '2026-09-22 03:00:00',
    status: '运行中',
    remark: '',
  },
]);

export function getTaskByModelId(modelId: number) {
  return tasks.value.find((item) => item.modelId === modelId);
}

export function createTaskForModel(modelId: number, cronDesc = '每日 02:00') {
  const model = models.value.find((item) => item.id === modelId);

  if (!model || getTaskByModelId(modelId)) {
    return;
  }

  tasks.value.push({
    id: nextTaskId(),
    taskName: model.modelName.replace(/模型$/, '') + '任务',
    modelId,
    cronDesc,
    lastRunTime: '',
    nextExecTime: '2026-09-23 02:00:00',
    status: '运行中',
    remark: '',
  });
}

function nextTaskId() {
  return tasks.value.reduce((max, item) => Math.max(max, item.id), 0) + 1;
}

// ==================== 执行记录（8.6 model_exec_log） ====================
export const execRecords = ref<ModelExecRecord[]>([
  {
    execNo: 'EXE20260921020000001',
    modelId: 1,
    taskId: 1,
    triggerType: '定时',
    status: '成功',
    startTime: '2026-09-21 02:00:00',
    endTime: '2026-09-21 02:03:41',
    costMs: 221_000,
    deviceCount: 6,
    errorMsg: '',
  },
  {
    execNo: 'EXE20260920020000001',
    modelId: 1,
    taskId: 1,
    triggerType: '定时',
    status: '失败',
    startTime: '2026-09-20 02:00:00',
    endTime: '2026-09-20 02:01:12',
    costMs: 72_000,
    deviceCount: 6,
    errorMsg: '脚本执行异常：KeyError: \'vibration_threshold\'，已自动重试2次仍失败',
  },
  {
    execNo: 'EXE20260922091200001',
    modelId: 1,
    taskId: null,
    triggerType: '单次运行',
    status: '运行中',
    startTime: '2026-09-22 09:12:00',
    endTime: '',
    costMs: 0,
    deviceCount: 6,
    errorMsg: '',
  },
  {
    execNo: 'EXE20260915030000001',
    modelId: 3,
    taskId: 2,
    triggerType: '定时',
    status: '成功',
    startTime: '2026-09-15 03:00:00',
    endTime: '2026-09-15 03:04:18',
    costMs: 258_000,
    deviceCount: 6,
    errorMsg: '',
  },
  {
    execNo: 'EXE20260908030000001',
    modelId: 3,
    taskId: 2,
    triggerType: '定时',
    status: '部分成功',
    startTime: '2026-09-08 03:00:00',
    endTime: '2026-09-08 03:03:52',
    costMs: 232_000,
    deviceCount: 6,
    errorMsg: '输出缺少声明字段：dim_env，已按部分成功入库',
  },
  {
    execNo: 'EXE20260830020000001',
    modelId: 5,
    taskId: null,
    triggerType: '定时',
    status: '超时',
    startTime: '2026-08-30 02:00:00',
    endTime: '2026-08-30 02:30:00',
    costMs: 1_800_000,
    deviceCount: 0,
    errorMsg: '执行超过30分钟被终止（沙箱超时）',
  },
  {
    execNo: 'EXE20260921093000001',
    modelId: 1,
    taskId: null,
    triggerType: '数据比对',
    status: '成功',
    startTime: '2026-09-21 09:30:12',
    endTime: '2026-09-21 09:31:05',
    costMs: 53_000,
    deviceCount: 4,
    errorMsg: '',
  },
  {
    execNo: 'EXE20260921160000001',
    modelId: 3,
    taskId: null,
    triggerType: '立即执行',
    status: '成功',
    startTime: '2026-09-21 16:00:00',
    endTime: '2026-09-21 16:03:22',
    costMs: 202_000,
    deviceCount: 6,
    errorMsg: '',
  },
]);

let execSeq = Date.now() % 1000;

export function generateExecNo() {
  execSeq += 1;
  const now = new Date();
  const pad = (value: number, length = 2) => String(value).padStart(length, '0');
  const stamp = `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
  return `EXE${stamp}${String(execSeq).padStart(3, '0')}`;
}

export function addExecRecord(record: Pick<ModelExecRecord, 'modelId' | 'taskId' | 'triggerType' | 'status' | 'deviceCount'> & Partial<ModelExecRecord>) {
  const startTime = record.startTime || formatDateTime(new Date());
  execRecords.value.unshift({
    costMs: 0,
    endTime: '',
    errorMsg: '',
    execNo: generateExecNo(),
    startTime,
    ...record,
  });
}

export function formatDateTime(date: Date) {
  const pad = (value: number) => String(value).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

export function getModelExecsOfTask(taskId: number) {
  return execRecords.value.filter((item) => item.taskId === taskId);
}

export function getExecLogLines(execNo: string): string[] {
  return [
    `[INFO] 批次 ${execNo} 进入执行队列`,
    '[INFO] 准备 Python 3.10 沙箱环境（pandas 2.2.1 / numpy 1.26.4 / scikit-learn 1.4.2）',
    '[INFO] 注入设备监测数据：拉取统计窗口内 12 台设备指标',
    '[INFO] 调用入口函数 execute(context) 开始计算',
    '[DEBUG] window=90d devices=12 params={"vibration_threshold":3.5}',
    '[INFO] 计算完成，解析输出参数并写入结果表',
    `[INFO] 批次 ${execNo} 执行结束`,
  ];
}

// ==================== 模型生命周期操作 ====================
function nextModelId() {
  return models.value.reduce((max, item) => Math.max(max, item.id), 0) + 1;
}

export interface SaveModelPayload {
  id?: number;
  modelName: string;
  categoryId: number;
  registerType: RegisterType;
  statPeriod: number;
  description: string;
  inputParams: ModelParam[];
  outputParams: ModelParam[];
  scriptUploaded?: boolean;
}

export function saveModelDraft(payload: SaveModelPayload) {
  if (payload.id) {
    const model = models.value.find((item) => item.id === payload.id);

    if (model) {
      Object.assign(model, {
        modelName: payload.modelName,
        categoryId: payload.categoryId,
        registerType: payload.registerType,
        statPeriod: payload.statPeriod,
        description: payload.description,
      });
      setModelParams(model.id, { input: payload.inputParams, output: payload.outputParams });
    }

    return payload.id;
  }

  const id = nextModelId();
  models.value.push({
    id,
    modelName: payload.modelName,
    categoryId: payload.categoryId,
    registerType: payload.registerType,
    statPeriod: payload.statPeriod,
    status: '草稿',
    enabled: false,
    description: payload.description,
    registerTime: formatDateTime(new Date()),
    lastRunTime: '',
    lastRunStatus: '',
  });
  setModelParams(id, { input: payload.inputParams, output: payload.outputParams });
  return id;
}

export function publishModel(id: number) {
  const model = models.value.find((item) => item.id === id);

  if (!model) {
    return;
  }

  model.status = '已发布';
  model.enabled = true;
  createTaskForModel(id);
}

export function offlineModel(id: number) {
  const model = models.value.find((item) => item.id === id);

  if (!model) {
    return;
  }

  model.status = '已下架';
  model.enabled = false;

  const task = getTaskByModelId(id);

  if (task) {
    task.status = '暂停';
  }
}

export function deleteModel(id: number) {
  models.value = models.value.filter((item) => item.id !== id);
}

export function toggleModelEnabled(id: number, enabled: boolean) {
  const model = models.value.find((item) => item.id === id);

  if (!model) {
    return;
  }

  model.enabled = enabled;

  if (enabled) {
    createTaskForModel(id);
  } else {
    const task = getTaskByModelId(id);

    if (task) {
      task.status = '暂停';
    }
  }
}

// ==================== 任务操作 ====================
export function toggleTaskStatus(id: number) {
  const task = tasks.value.find((item) => item.id === id);

  if (task) {
    task.status = task.status === '运行中' ? '暂停' : '运行中';
  }
}

export function updateTask(id: number, patch: Partial<ModelTaskRecord>) {
  const task = tasks.value.find((item) => item.id === id);

  if (task) {
    Object.assign(task, patch);
  }
}

export function removeTask(id: number) {
  tasks.value = tasks.value.filter((item) => item.id !== id);
}

export function runTaskNow(id: number) {
  const task = tasks.value.find((item) => item.id === id);

  if (!task) {
    return;
  }

  const deviceType = getCategoryDeviceType(task.modelId);
  addExecRecord({
    modelId: task.modelId,
    taskId: task.id,
    triggerType: '立即执行',
    status: '运行中',
    deviceCount: deviceType ? getDevicesByType(deviceType).length : 0,
  });
  task.lastRunTime = formatDateTime(new Date());
}

// ==================== 类别操作 ====================
export function isCategoryNameDuplicated(name: string, excludeId?: number) {
  return categories.value.some(
    (item) => item.categoryName === name.trim() && item.id !== excludeId,
  );
}

export function addCategory(payload: Omit<ModelCategory, 'id' | 'createTime'>) {
  const now = new Date();
  categories.value.push({
    ...payload,
    createTime: formatDateTime(now),
    id: categories.value.reduce((max, item) => Math.max(max, item.id), 0) + 1,
  });
}

export function updateCategory(id: number, patch: Partial<ModelCategory>) {
  const category = categories.value.find((item) => item.id === id);

  if (category) {
    Object.assign(category, patch);
  }
}

export function removeCategory(id: number) {
  categories.value = categories.value.filter((item) => item.id !== id);
}

export function countModelsByCategory(categoryId: number) {
  return models.value.filter((item) => item.categoryId === categoryId).length;
}

// ==================== 异物检测分析 ====================
export type AnomalyMonitorStatus = '关注' | '异常' | '正常';

export interface AnomalyFilter {
  device: string;
  line: string;
  station: string;
  type: '' | DeviceTypeOption;
}

export interface AnomalyEventRecord {
  abnormalValue: string;
  deviation: string;
  deviceId: string;
  deviceName: string;
  id: string;
  line: string;
  normalValue: string;
  param: string;
  station: string;
  time: string;
  timestamp: number;
}

export interface AnomalyMonitorParam {
  name: string;
  range: string;
  status: AnomalyMonitorStatus;
  value: string;
}

export interface AnomalyTrendPoint {
  current: number;
  time: string;
  voltage: number;
}

export interface AnomalyAnalysisResult {
  anomalyIndex: number;
  anomalyPercent: number;
  chartLabel: string;
  events: AnomalyEventRecord[];
  monitorParams: AnomalyMonitorParam[];
  seedKey: string;
  trend: AnomalyTrendPoint[];
}

/** 异物检测仅覆盖自动扶梯 / 垂直电梯两类设备品种 */
export const anomalyDeviceTypeOptions: { label: string; value: DeviceTypeOption }[] = [
  { label: '自动扶梯', value: '自动扶梯' },
  { label: '垂直电梯', value: '垂直电梯' },
];

export const anomalyTrendPointCount = 30;

function hashString(value: string) {
  let hash = 0;

  for (let index = 0; index < value.length; index += 1) {
    hash = (hash << 5) - hash + value.charCodeAt(index);
    hash |= 0;
  }

  return Math.abs(hash);
}

function pseudoRandom(seed: number, index: number) {
  const value = Math.sin((seed + index + 1) * 12.9898) * 43758.5453;

  return value - Math.floor(value);
}

function formatClockTime(date: Date) {
  const pad = (value: number) => String(value).padStart(2, '0');

  return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export function getAnomalyLineOptions() {
  return [...new Set(devices.value.map((item) => item.line))].map((line) => ({
    label: line,
    value: line,
  }));
}

export function getAnomalyStationOptions(line: string) {
  const scoped = line
    ? devices.value.filter((item) => item.line === line)
    : devices.value;

  return [...new Set(scoped.map((item) => item.station))].map((station) => ({
    label: station,
    value: station,
  }));
}

function getAnomalyScopedDevices(
  filter: Pick<AnomalyFilter, 'line' | 'station' | 'type'>,
) {
  return devices.value.filter((item) => {
    if (item.type !== '自动扶梯' && item.type !== '垂直电梯') {
      return false;
    }
    if (filter.line && item.line !== filter.line) {
      return false;
    }
    if (filter.station && item.station !== filter.station) {
      return false;
    }
    if (filter.type && item.type !== filter.type) {
      return false;
    }

    return true;
  });
}

export function getAnomalyDeviceOptions(
  filter: Pick<AnomalyFilter, 'line' | 'station' | 'type'>,
) {
  return getAnomalyScopedDevices(filter).map((item) => ({
    label: `${item.name}（${item.id}）`,
    value: item.id,
  }));
}

export function getAnomalyAnalysis(
  filter: AnomalyFilter,
  tick = 0,
): AnomalyAnalysisResult {
  const targetDevice = filter.device
    ? devices.value.find((item) => item.id === filter.device)
    : undefined;
  const seedKey = targetDevice
    ? targetDevice.name
    : filter.station || filter.line || '全部线路';
  const baseSeed = hashString(seedKey);
  const baseRandom = (index: number) => pseudoRandom(baseSeed, index);
  const liveRandom = (index: number) =>
    pseudoRandom(baseSeed + tick * 104_729, index);

  const scopedDevices = getAnomalyScopedDevices(filter);
  const eventDevices = targetDevice ? [targetDevice] : scopedDevices;
  const chartLabel = targetDevice
    ? targetDevice.name
    : `${filter.station || filter.line || '全部线路'}全部设备`;

  const baseCurrent = 30 + baseRandom(1) * 15;
  const baseVibration = 1.5 + baseRandom(2) * 2.5;
  const baseTemp = 50 + baseRandom(3) * 35;
  const abnormalCurrent = baseCurrent * (1.02 + baseRandom(4) * 0.22);
  const abnormalVibration = baseVibration * (1.03 + baseRandom(5) * 0.28);
  const currentPercent = Math.round((abnormalCurrent / baseCurrent - 1) * 100);
  const vibrationPercent = Math.round(
    (abnormalVibration / baseVibration - 1) * 100,
  );
  const tempRise = Math.round(3 + baseRandom(7) * 10);

  const paramSnapshots = [
    {
      abnormal: `${abnormalCurrent.toFixed(1)}A`,
      deviation: `+${currentPercent}%`,
      normal: `${baseCurrent.toFixed(1)}A`,
      param: '电流',
    },
    {
      abnormal: `${abnormalVibration.toFixed(1)}mm/s`,
      deviation: `+${vibrationPercent}%`,
      normal: `${baseVibration.toFixed(1)}mm/s`,
      param: '振动',
    },
    {
      abnormal: `${abnormalCurrent.toFixed(1)}A/${abnormalVibration.toFixed(1)}`,
      deviation: `+${Math.round((currentPercent + vibrationPercent) / 2)}%`,
      normal: `${baseCurrent.toFixed(1)}A/${baseVibration.toFixed(1)}`,
      param: '电流+振动',
    },
    {
      abnormal: `${(baseTemp + tempRise).toFixed(0)}°C`,
      deviation: `+${tempRise}°C`,
      normal: `${baseTemp.toFixed(0)}°C`,
      param: '温度',
    },
  ];

  const now = Date.now();
  const nowHour = new Date(now).getHours();
  const isPeak =
    (nowHour >= 7 && nowHour <= 9) || (nowHour >= 17 && nowHour <= 19);
  const deviceCount = eventDevices.length || 1;
  const eventCap = filter.line ? (isPeak ? 5 : 3) : isPeak ? 8 : 5;
  const eventTotal = Math.max(
    isPeak ? 2 : 1,
    Math.min(
      eventCap,
      Math.floor(
        deviceCount * (isPeak ? 0.5 : 0.3) +
          liveRandom(8) * (isPeak ? 1.5 : 0.8) +
          (isPeak ? 1 : 0),
      ),
    ),
  );

  const rawEvents: AnomalyEventRecord[] = [];

  for (let index = 0; index < eventTotal; index += 1) {
    const device = eventDevices[index % eventDevices.length];
    const snapshot = paramSnapshots[index % paramSnapshots.length];

    if (!device || !snapshot) {
      continue;
    }

    const minutesAgo = 1 + Math.floor(liveRandom(20 + index) * 120);
    const timestamp = now - minutesAgo * 60_000;

    rawEvents.push({
      abnormalValue: snapshot.abnormal,
      deviation: snapshot.deviation,
      deviceId: device.id,
      deviceName: device.name,
      id: `${device.id}-${timestamp}`,
      line: device.line,
      normalValue: snapshot.normal,
      param: snapshot.param,
      station: device.station,
      time: formatDateTime(new Date(timestamp)),
      timestamp,
    });
  }

  rawEvents.sort((a, b) => b.timestamp - a.timestamp);

  const countPerDevice: Record<string, number> = {};
  const events = rawEvents.filter((item) => {
    const count = countPerDevice[item.deviceId] ?? 0;

    if (count >= 10) {
      return false;
    }
    countPerDevice[item.deviceId] = count + 1;

    return true;
  });

  const anomalyIndex = 9 + Math.floor(baseRandom(19) * 12);
  const anomalyPercent = Math.round(20 + baseRandom(20) * 50);

  // 实时监测值：tick 变化时轻微抖动，用于页面 30 秒自动刷新
  const liveCurrent = 30 + liveRandom(101) * 15;
  const liveVibration = 1.5 + liveRandom(102) * 2.5;
  const liveTemp = 50 + liveRandom(103) * 35;

  const trend: AnomalyTrendPoint[] = Array.from(
    { length: anomalyTrendPointCount },
    (_, index) => {
      const distance = Math.abs(index - anomalyIndex);
      const weight = distance <= 2 ? 1 - distance / 3 : 0;
      const currentSpike = weight * (anomalyPercent / 100);
      const voltageSpike = weight * (anomalyPercent / 400);

      return {
        current: Number(
          (
            baseCurrent *
            (1 + (liveRandom(31 + index) - 0.5) * 0.05 + currentSpike)
          ).toFixed(1),
        ),
        time: formatClockTime(new Date(now - (29 - index) * 60_000)),
        voltage: Number(
          (
            380 *
            (1 + (liveRandom(61 + index) - 0.5) * 0.01 + voltageSpike)
          ).toFixed(1),
        ),
      };
    },
  );

  const deviceType = filter.type || scopedDevices[0]?.type || '自动扶梯';
  const monitorParams: AnomalyMonitorParam[] =
    deviceType === '垂直电梯'
      ? [
          {
            name: '电流',
            range: '22-35A',
            status: '正常',
            value: `${(liveCurrent * 0.85).toFixed(1)}A`,
          },
          { name: '电压', range: '370-390V', status: '正常', value: '380V' },
          {
            name: '振动',
            range: '0-2.5mm/s',
            status: '正常',
            value: `${liveVibration.toFixed(1)}mm/s`,
          },
          {
            name: '温度',
            range: '35-75°C',
            status: '正常',
            value: `${liveTemp.toFixed(0)}°C`,
          },
        ]
      : [
          {
            name: '电流',
            range: '30-42A',
            status: '正常',
            value: `${liveCurrent.toFixed(1)}A`,
          },
          { name: '电压', range: '370-390V', status: '正常', value: '380V' },
          {
            name: '振动',
            range: '0-3.0mm/s',
            status: '正常',
            value: `${liveVibration.toFixed(1)}mm/s`,
          },
          {
            name: '温度',
            range: '40-80°C',
            status: '正常',
            value: `${liveTemp.toFixed(0)}°C`,
          },
        ];

  if (currentPercent >= 20) {
    const currentParam = monitorParams.find((item) => item.name === '电流');

    if (currentParam) {
      currentParam.status = '关注';
    }
  }
  if (vibrationPercent >= 25) {
    const vibrationParam = monitorParams.find((item) => item.name === '振动');

    if (vibrationParam) {
      vibrationParam.status = '关注';
    }
  }
  if (abnormalCurrent > 42 && abnormalVibration > 3) {
    const currentParam = monitorParams.find((item) => item.name === '电流');
    const vibrationParam = monitorParams.find((item) => item.name === '振动');

    if (currentParam) {
      currentParam.status = '异常';
    }
    if (vibrationParam) {
      vibrationParam.status = '异常';
    }
  }

  return {
    anomalyIndex,
    anomalyPercent,
    chartLabel,
    events,
    monitorParams,
    seedKey,
    trend,
  };
}

export const anomalyStatusMeta: Record<
  AnomalyMonitorStatus,
  { label: AnomalyMonitorStatus; status: 'error' | 'success' | 'warning' }
> = {
  关注: { label: '关注', status: 'warning' },
  异常: { label: '异常', status: 'error' },
  正常: { label: '正常', status: 'success' },
};

/** 「全部记录」弹窗使用的历史异常记录（近 7 天） */
export function getAnomalyAllEvents(
  filter: AnomalyFilter,
  limit = 90,
): AnomalyEventRecord[] {
  const targetDevice = filter.device
    ? devices.value.find((item) => item.id === filter.device)
    : undefined;
  const eventDevices = targetDevice
    ? [targetDevice]
    : getAnomalyScopedDevices(filter);

  if (eventDevices.length === 0) {
    return [];
  }

  const seedKey = targetDevice
    ? targetDevice.name
    : filter.station || filter.line || '全部线路';
  const seed = hashString(seedKey);
  const baseCurrent = 30 + pseudoRandom(seed, 1) * 15;
  const baseVibration = 1.5 + pseudoRandom(seed, 2) * 2.5;
  const baseTemp = 50 + pseudoRandom(seed, 3) * 35;
  const now = Date.now();
  const records: AnomalyEventRecord[] = [];

  for (let index = 0; index < limit; index += 1) {
    const device = eventDevices[index % eventDevices.length];

    if (!device) {
      continue;
    }

    const currentRise = 1.02 + pseudoRandom(seed + 37, index) * 0.22;
    const vibrationRise = 1.03 + pseudoRandom(seed + 53, index) * 0.28;
    const tempRise = 3 + pseudoRandom(seed + 71, index) * 10;
    const abnormalCurrent = baseCurrent * currentRise;
    const abnormalVibration = baseVibration * vibrationRise;
    const paramType = index % 4;

    let snapshot: { abnormal: string; deviation: string; normal: string; param: string };

    if (paramType === 0) {
      snapshot = {
        abnormal: `${abnormalCurrent.toFixed(1)}A`,
        deviation: `+${Math.round((currentRise - 1) * 100)}%`,
        normal: `${baseCurrent.toFixed(1)}A`,
        param: '电流',
      };
    } else if (paramType === 1) {
      snapshot = {
        abnormal: `${abnormalVibration.toFixed(1)}mm/s`,
        deviation: `+${Math.round((vibrationRise - 1) * 100)}%`,
        normal: `${baseVibration.toFixed(1)}mm/s`,
        param: '振动',
      };
    } else if (paramType === 2) {
      snapshot = {
        abnormal: `${abnormalCurrent.toFixed(1)}A/${abnormalVibration.toFixed(1)}`,
        deviation: `+${Math.round((currentRise + vibrationRise - 2) * 50)}%`,
        normal: `${baseCurrent.toFixed(1)}A/${baseVibration.toFixed(1)}`,
        param: '电流+振动',
      };
    } else {
      snapshot = {
        abnormal: `${(baseTemp + tempRise).toFixed(0)}°C`,
        deviation: `+${Math.round(tempRise)}°C`,
        normal: `${baseTemp.toFixed(0)}°C`,
        param: '温度',
      };
    }

    const minutesAgo =
      5 + Math.floor(pseudoRandom(seed + 89, index) * 7 * 24 * 60);
    const timestamp = now - minutesAgo * 60_000;

    records.push({
      abnormalValue: snapshot.abnormal,
      deviation: snapshot.deviation,
      deviceId: device.id,
      deviceName: device.name,
      id: `${device.id}-all-${timestamp}`,
      line: device.line,
      normalValue: snapshot.normal,
      param: snapshot.param,
      station: device.station,
      time: formatDateTime(new Date(timestamp)),
      timestamp,
    });
  }

  return records.sort((a, b) => b.timestamp - a.timestamp);
}
