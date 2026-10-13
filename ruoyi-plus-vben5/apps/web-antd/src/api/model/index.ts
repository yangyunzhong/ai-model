import type { CatalogVO, CatalogForm, CatalogQuery } from './model';
import type { TaskVO, TaskForm, TaskQuery } from './model';
import type { DefinitionVO, DefinitionForm, DefinitionQuery } from './model';
import type { ComparisonRecordVO, ComparisonRecordForm, ComparisonRecordQuery } from './model';
import type { ComparisonExecuteForm, ComparisonExecuteVO } from './model';
import type { ComparisonDetailVO, ComparisonDetailForm, ComparisonDetailQuery } from './model';
import type { AnomalyEventVO, AnomalyEventForm, AnomalyEventQuery } from './model';
import type { DeviceVO, DeviceQuery } from './model';
import type { ExecLogVO, ExecLogForm, ExecLogQuery } from './model';
import type { ExecLogDetailVO, ExecLogDetailForm, ExecLogDetailQuery } from './model';
import type { LineVO, LineForm, LineQuery } from './model';
import type { StationVO, StationForm, StationQuery } from './model';
import type { ParamVO, ParamForm, ParamQuery } from './model';
import type { ResultVO, ResultForm, ResultQuery } from './model';
import type { ScriptVO, ScriptForm, ScriptQuery } from './model';
import type { TaskParamVO, TaskParamForm, TaskParamQuery } from './model';

import type { ID, IDS } from '#/api/common';
import type { PageResult } from '#/api/common';

import { commonExport } from '#/api/helper';
import { ContentTypeEnum } from '#/api/helper';
import { alovaInstance } from '#/utils/http';

/**
* 查询模型类别列表
* @param params
* @returns 模型类别列表
*/
export function catalogList(params?: CatalogQuery) {
  return alovaInstance.get<PageResult<CatalogVO>>('/model/catalog/list', { params });
}

/**
 * 导出模型类别列表
 * @param params
 * @returns 模型类别列表
 */
export function catalogExport(params?: CatalogQuery) {
  return commonExport('/model/catalog/export', params ?? {});
}

/**
 * 查询模型类别详情
 * @param id id
 * @returns 模型类别详情
 */
export function catalogInfo(id: ID) {
  return alovaInstance.get<CatalogVO>(`/model/catalog/${id}`);
}

/**
 * 新增模型类别
 * @param data
 * @returns void
 */
export function catalogAdd(data: CatalogForm) {
  return alovaInstance.postWithMsg<void>('/model/catalog', data);
}

/**
 * 更新模型类别
 * @param data
 * @returns void
 */
export function catalogUpdate(data: CatalogForm) {
  return alovaInstance.putWithMsg<void>('/model/catalog', data);
}

/**
 * 删除模型类别
 * @param id id
 * @returns void
 */
export function catalogRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/catalog/${id}`);
}

/**
* 查询模型任务列表
* @param params
* @returns 模型任务列表
*/
export function taskList(params?: TaskQuery) {
  return alovaInstance.get<PageResult<TaskVO>>('/model/task/list', { params });
}

/**
 * 导出模型任务列表
 * @param params
 * @returns 模型任务列表
 */
export function taskExport(params?: TaskQuery) {
  return commonExport('/model/task/export', params ?? {});
}

/**
 * 查询模型任务详情
 * @param id id
 * @returns 模型任务详情
 */
export function taskInfo(id: ID) {
  return alovaInstance.get<TaskVO>(`/model/task/${id}`);
}

/**
 * 新增模型任务
 * @param data
 * @returns void
 */
export function taskAdd(data: TaskForm) {
  return alovaInstance.postWithMsg<void>('/model/task', data);
}

/**
 * 更新模型任务
 * @param data
 * @returns void
 */
export function taskUpdate(data: TaskForm) {
  return alovaInstance.putWithMsg<void>('/model/task', data);
}

/**
 * 删除模型任务
 * @param id id
 * @returns void
 */
export function taskRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/task/${id}`);
}

/**
 * 立即执行模型任务（对该品种全部启用设备执行一次）
 * @param id id
 * @returns void
 */
export function taskRunNow(id: ID) {
  return alovaInstance.postWithMsg<void>(`/model/task/runNow/${id}`);
}

/**
 * 开启 / 暂停模型任务（同步定时调度）
 * @param id id
 * @returns void
 */
export function taskToggleStatus(id: ID) {
  return alovaInstance.postWithMsg<void>(`/model/task/toggleStatus/${id}`);
}

/**
* 查询模型定义列表
* @param params
* @returns 模型定义列表
*/
export function definitionList(params?: DefinitionQuery) {
  return alovaInstance.get<PageResult<DefinitionVO>>('/model/definition/list', { params });
}

/**
 * 导出模型定义列表
 * @param params
 * @returns 模型定义列表
 */
export function definitionExport(params?: DefinitionQuery) {
  return commonExport('/model/definition/export', params ?? {});
}

/**
 * 查询模型定义详情
 * @param id id
 * @returns 模型定义详情
 */
export function definitionInfo(id: ID) {
  return alovaInstance.get<DefinitionVO>(`/model/definition/${id}`);
}

/**
 * 新增模型定义
 * @param data
 * @returns string
 */
export function definitionAdd(data: DefinitionForm) {
  return alovaInstance.postWithMsg<string>('/model/definition', data);
}

/**
 * 更新模型定义
 * @param data
 * @returns void
 */
export function definitionUpdate(data: DefinitionForm) {
  return alovaInstance.putWithMsg<void>('/model/definition', data);
}

/**
 * 删除模型定义
 * @param id id
 * @returns void
 */
export function definitionRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/definition/${id}`);
}

/**
 * 发布模型定义（按当前脚本创建/启动运行时容器）
 * 容器创建、依赖安装耗时较长，单独放宽超时，避免全局 10s 超时导致前端误报失败
 * @param id id
 * @returns void
 */
export function definitionPublish(id: ID) {
  return alovaInstance.postWithMsg<void>(`/model/definition/publish/${id}`, undefined, {
    timeout: 180 * 1000,
  });
}

/**
 * 下架模型定义（停止并删除运行时容器）
 * docker stop 优雅等待耗时较长，单独放宽超时，避免全局 10s 超时导致前端误报失败
 * @param id id
 * @returns void
 */
export function definitionOffline(id: ID) {
  return alovaInstance.postWithMsg<void>(`/model/definition/offline/${id}`, undefined, {
    timeout: 120 * 1000,
  });
}

/**
* 查询数据比对记录列表
* @param params
* @returns 数据比对记录列表
*/
export function comparisonRecordList(params?: ComparisonRecordQuery) {
  return alovaInstance.get<PageResult<ComparisonRecordVO>>('/model/comparisonRecord/list', { params });
}

/**
 * 导出数据比对记录列表
 * @param params
 * @returns 数据比对记录列表
 */
export function comparisonRecordExport(params?: ComparisonRecordQuery) {
  return commonExport('/model/comparisonRecord/export', params ?? {});
}

/**
 * 查询数据比对记录详情
 * @param id id
 * @returns 数据比对记录详情
 */
export function comparisonRecordInfo(id: ID) {
  return alovaInstance.get<ComparisonRecordVO>(`/model/comparisonRecord/${id}`);
}

/**
 * 新增数据比对记录
 * @param data
 * @returns void
 */
export function comparisonRecordAdd(data: ComparisonRecordForm) {
  return alovaInstance.postWithMsg<void>('/model/comparisonRecord', data);
}

/**
 * 更新数据比对记录
 * @param data
 * @returns void
 */
export function comparisonRecordUpdate(data: ComparisonRecordForm) {
  return alovaInstance.putWithMsg<void>('/model/comparisonRecord', data);
}

/**
 * 删除数据比对记录
 * @param id id
 * @returns void
 */
export function comparisonRecordRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/comparisonRecord/${id}`);
}

/**
 * 执行数据比对（取各模型历史结果对同一批设备横向比对）
 * @param data 比对参数（设备品种、设备列表、模型列表 2-5 个）
 * @returns 比对矩阵结果
 */
export function comparisonRecordExecute(data: ComparisonExecuteForm) {
  return alovaInstance.post<ComparisonExecuteVO>('/model/comparisonRecord/execute', data);
}

/**
* 查询比对详细结果列表
* @param params
* @returns 比对详细结果列表
*/
export function comparisonDetailList(params?: ComparisonDetailQuery) {
  return alovaInstance.get<PageResult<ComparisonDetailVO>>('/model/comparisonDetail/list', { params });
}

/**
 * 导出比对详细结果列表
 * @param params
 * @returns 比对详细结果列表
 */
export function comparisonDetailExport(params?: ComparisonDetailQuery) {
  return commonExport('/model/comparisonDetail/export', params ?? {});
}

/**
 * 查询比对详细结果详情
 * @param id id
 * @returns 比对详细结果详情
 */
export function comparisonDetailInfo(id: ID) {
  return alovaInstance.get<ComparisonDetailVO>(`/model/comparisonDetail/${id}`);
}

/**
 * 新增比对详细结果
 * @param data
 * @returns void
 */
export function comparisonDetailAdd(data: ComparisonDetailForm) {
  return alovaInstance.postWithMsg<void>('/model/comparisonDetail', data);
}

/**
 * 更新比对详细结果
 * @param data
 * @returns void
 */
export function comparisonDetailUpdate(data: ComparisonDetailForm) {
  return alovaInstance.putWithMsg<void>('/model/comparisonDetail', data);
}

/**
 * 删除比对详细结果
 * @param id id
 * @returns void
 */
export function comparisonDetailRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/comparisonDetail/${id}`);
}


/**
* 查询异物检测事件列表
* @param params
* @returns 异物检测事件列表
*/
export function anomalyEventList(params?: AnomalyEventQuery) {
  return alovaInstance.get<PageResult<AnomalyEventVO>>('/model/anomalyEvent/list', { params });
}

/**
 * 导出异物检测事件列表
 * @param params
 * @returns 异物检测事件列表
 */
export function anomalyEventExport(params?: AnomalyEventQuery) {
  return commonExport('/model/anomalyEvent/export', params ?? {});
}

/**
 * 查询异物检测事件详情
 * @param id id
 * @returns 异物检测事件详情
 */
export function anomalyEventInfo(id: ID) {
  return alovaInstance.get<AnomalyEventVO>(`/model/anomalyEvent/${id}`);
}

/**
 * 新增异物检测事件
 * @param data
 * @returns void
 */
export function anomalyEventAdd(data: AnomalyEventForm) {
  return alovaInstance.postWithMsg<void>('/model/anomalyEvent', data);
}

/**
 * 更新异物检测事件
 * @param data
 * @returns void
 */
export function anomalyEventUpdate(data: AnomalyEventForm) {
  return alovaInstance.putWithMsg<void>('/model/anomalyEvent', data);
}

/**
 * 删除异物检测事件
 * @param id id
 * @returns void
 */
export function anomalyEventRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/anomalyEvent/${id}`);
}

/**
* 查询设备台账主列表
* @param params
* @returns 设备台账主列表
*/
export function deviceList(params?: DeviceQuery) {
  return alovaInstance.get<PageResult<DeviceVO>>('/model/device/list', { params });
}

/**
 * 导出设备台账主列表
 * @param params
 * @returns 设备台账主列表
 */
export function deviceExport(params?: DeviceQuery) {
  return commonExport('/model/device/export', params ?? {});
}

/**
 * 查询设备详情
 * @param id 设备编号
 * @returns 设备详情
 */
export function deviceInfo(id: ID) {
  return alovaInstance.get<DeviceVO>(`/model/device/${id}`);
}

/**
* 查询模型执行记录列表
* @param params
* @returns 模型执行记录列表
*/
export function execLogList(params?: ExecLogQuery) {
  return alovaInstance.get<PageResult<ExecLogVO>>('/model/execLog/list', { params });
}

/**
 * 导出模型执行记录列表
 * @param params
 * @returns 模型执行记录列表
 */
export function execLogExport(params?: ExecLogQuery) {
  return commonExport('/model/execLog/export', params ?? {});
}

/**
 * 查询模型执行记录详情
 * @param id id
 * @returns 模型执行记录详情
 */
export function execLogInfo(id: ID) {
  return alovaInstance.get<ExecLogVO>(`/model/execLog/${id}`);
}

/**
 * 新增模型执行记录
 * @param data
 * @returns void
 */
export function execLogAdd(data: ExecLogForm) {
  return alovaInstance.postWithMsg<void>('/model/execLog', data);
}

/**
 * 更新模型执行记录
 * @param data
 * @returns void
 */
export function execLogUpdate(data: ExecLogForm) {
  return alovaInstance.putWithMsg<void>('/model/execLog', data);
}

/**
 * 删除模型执行记录
 * @param id id
 * @returns void
 */
export function execLogRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/execLog/${id}`);
}

/**
 * 触发模型单次运行（真实调度执行并落库）
 * @param data 运行参数
 * @returns 执行记录ID
 */
export function execLogRunOnce(data: {
  deviceIds?: Array<number | string>;
  modelId: ID;
  params?: Record<string, any>;
  triggerType?: string;
}) {
  return alovaInstance.postWithMsg<string>('/model/execLog/runOnce', data);
}

/**
* 查询模型执行日志明细列表
* @param params
* @returns 模型执行日志明细列表
*/
export function execLogDetailList(params?: ExecLogDetailQuery) {
  return alovaInstance.get<PageResult<ExecLogDetailVO>>('/model/execLogDetail/list', { params });
}

/**
 * 导出模型执行日志明细列表
 * @param params
 * @returns 模型执行日志明细列表
 */
export function execLogDetailExport(params?: ExecLogDetailQuery) {
  return commonExport('/model/execLogDetail/export', params ?? {});
}

/**
 * 查询模型执行日志明细详情
 * @param id id
 * @returns 模型执行日志明细详情
 */
export function execLogDetailInfo(id: ID) {
  return alovaInstance.get<ExecLogDetailVO>(`/model/execLogDetail/${id}`);
}

/**
 * 新增模型执行日志明细
 * @param data
 * @returns void
 */
export function execLogDetailAdd(data: ExecLogDetailForm) {
  return alovaInstance.postWithMsg<void>('/model/execLogDetail', data);
}

/**
 * 更新模型执行日志明细
 * @param data
 * @returns void
 */
export function execLogDetailUpdate(data: ExecLogDetailForm) {
  return alovaInstance.putWithMsg<void>('/model/execLogDetail', data);
}

/**
 * 删除模型执行日志明细
 * @param id id
 * @returns void
 */
export function execLogDetailRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/execLogDetail/${id}`);
}

/**
* 查询线路信息列表
* @param params
* @returns 线路信息列表
*/
export function lineList(params?: LineQuery) {
  return alovaInstance.get<PageResult<LineVO>>('/model/line/list', { params });
}

/**
 * 导出线路信息列表
 * @param params
 * @returns 线路信息列表
 */
export function lineExport(params?: LineQuery) {
  return commonExport('/model/line/export', params ?? {});
}

/**
 * 查询线路信息详情
 * @param id id
 * @returns 线路信息详情
 */
export function lineInfo(id: ID) {
  return alovaInstance.get<LineVO>(`/model/line/${id}`);
}

/**
 * 新增线路信息
 * @param data
 * @returns void
 */
export function lineAdd(data: LineForm) {
  return alovaInstance.postWithMsg<void>('/model/line', data);
}

/**
 * 更新线路信息
 * @param data
 * @returns void
 */
export function lineUpdate(data: LineForm) {
  return alovaInstance.putWithMsg<void>('/model/line', data);
}

/**
 * 删除线路信息
 * @param id id
 * @returns void
 */
export function lineRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/line/${id}`);
}

/**
* 查询车站信息列表
* @param params
* @returns 车站信息列表
*/
export function stationList(params?: StationQuery) {
  return alovaInstance.get<PageResult<StationVO>>('/model/station/list', { params });
}

/**
 * 导出车站信息列表
 * @param params
 * @returns 车站信息列表
 */
export function stationExport(params?: StationQuery) {
  return commonExport('/model/station/export', params ?? {});
}

/**
 * 查询车站信息详情
 * @param id id
 * @returns 车站信息详情
 */
export function stationInfo(id: ID) {
  return alovaInstance.get<StationVO>(`/model/station/${id}`);
}

/**
 * 新增车站信息
 * @param data
 * @returns void
 */
export function stationAdd(data: StationForm) {
  return alovaInstance.postWithMsg<void>('/model/station', data);
}

/**
 * 更新车站信息
 * @param data
 * @returns void
 */
export function stationUpdate(data: StationForm) {
  return alovaInstance.putWithMsg<void>('/model/station', data);
}

/**
 * 删除车站信息
 * @param id id
 * @returns void
 */
export function stationRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/station/${id}`);
}

/**
* 查询模型输入输出参数列表
* @param params
* @returns 模型输入输出参数列表
*/
export function paramList(params?: ParamQuery) {
  return alovaInstance.get<PageResult<ParamVO>>('/model/param/list', { params });
}

/**
 * 导出模型输入输出参数列表
 * @param params
 * @returns 模型输入输出参数列表
 */
export function paramExport(params?: ParamQuery) {
  return commonExport('/model/param/export', params ?? {});
}

/**
 * 查询模型输入输出参数详情
 * @param id id
 * @returns 模型输入输出参数详情
 */
export function paramInfo(id: ID) {
  return alovaInstance.get<ParamVO>(`/model/param/${id}`);
}

/**
 * 新增模型输入输出参数
 * @param data
 * @returns void
 */
export function paramAdd(data: ParamForm) {
  return alovaInstance.postWithMsg<void>('/model/param', data);
}

/**
 * 更新模型输入输出参数
 * @param data
 * @returns void
 */
export function paramUpdate(data: ParamForm) {
  return alovaInstance.putWithMsg<void>('/model/param', data);
}

/**
 * 删除模型输入输出参数
 * @param id id
 * @returns void
 */
export function paramRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/param/${id}`);
}

/**
* 查询模型执行结果明细列表
* @param params
* @returns 模型执行结果明细列表
*/
export function resultList(params?: ResultQuery) {
  return alovaInstance.get<PageResult<ResultVO>>('/model/result/list', { params });
}

/**
 * 导出模型执行结果明细列表
 * @param params
 * @returns 模型执行结果明细列表
 */
export function resultExport(params?: ResultQuery) {
  return commonExport('/model/result/export', params ?? {});
}

/**
 * 查询模型执行结果明细详情
 * @param id id
 * @returns 模型执行结果明细详情
 */
export function resultInfo(id: ID) {
  return alovaInstance.get<ResultVO>(`/model/result/${id}`);
}

/**
 * 新增模型执行结果明细
 * @param data
 * @returns void
 */
export function resultAdd(data: ResultForm) {
  return alovaInstance.postWithMsg<void>('/model/result', data);
}

/**
 * 更新模型执行结果明细
 * @param data
 * @returns void
 */
export function resultUpdate(data: ResultForm) {
  return alovaInstance.putWithMsg<void>('/model/result', data);
}

/**
 * 删除模型执行结果明细
 * @param id id
 * @returns void
 */
export function resultRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/result/${id}`);
}

/**
* 查询模型算法脚本版本列表
* @param params
* @returns 模型算法脚本版本列表
*/
export function scriptList(params?: ScriptQuery) {
  return alovaInstance.get<PageResult<ScriptVO>>('/model/script/list', { params });
}

/**
 * 导出模型算法脚本版本列表
 * @param params
 * @returns 模型算法脚本版本列表
 */
export function scriptExport(params?: ScriptQuery) {
  return commonExport('/model/script/export', params ?? {});
}

/**
 * 查询模型算法脚本版本详情
 * @param id id
 * @returns 模型算法脚本版本详情
 */
export function scriptInfo(id: ID) {
  return alovaInstance.get<ScriptVO>(`/model/script/${id}`);
}

/**
 * 新增模型算法脚本版本
 * @param data
 * @returns void
 */
export function scriptAdd(data: ScriptForm) {
  return alovaInstance.postWithMsg<void>('/model/script', data);
}

/**
 * 更新模型算法脚本版本
 * @param data
 * @returns void
 */
export function scriptUpdate(data: ScriptForm) {
  return alovaInstance.putWithMsg<void>('/model/script', data);
}

/**
 * 删除模型算法脚本版本
 * @param id id
 * @returns void
 */
export function scriptRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/script/${id}`);
}

/**
 * 上传模型算法脚本（存 MinIO）
 * @param file          .py 文件
 * @param modelId       所属模型ID
 * @param scriptVersion 版本号（可空）
 * @param requirements  依赖清单 requirements.txt（可空）
 */
export function scriptUpload(
  file: File,
  modelId: ID,
  scriptVersion?: string,
  requirements?: File,
) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('modelId', String(modelId));
  if (scriptVersion) {
    formData.append('scriptVersion', scriptVersion);
  }
  if (requirements) {
    formData.append('requirements', requirements);
  }
  // 关闭全局自动提示，由调用页面统一提示成功/失败，避免重复弹窗
  return alovaInstance.post<ScriptVO>('/model/script/upload', formData, {
    headers: { 'Content-Type': ContentTypeEnum.FORM_DATA },
    timeout: 60 * 1000,
    successMessageMode: 'none',
    errorMessageMode: 'none',
  });
}

/**
* 查询模型任务运行参数列表
* @param params
* @returns 模型任务运行参数列表
*/
export function taskParamList(params?: TaskParamQuery) {
  return alovaInstance.get<PageResult<TaskParamVO>>('/model/taskParam/list', { params });
}

/**
 * 导出模型任务运行参数列表
 * @param params
 * @returns 模型任务运行参数列表
 */
export function taskParamExport(params?: TaskParamQuery) {
  return commonExport('/model/taskParam/export', params ?? {});
}

/**
 * 查询模型任务运行参数详情
 * @param id id
 * @returns 模型任务运行参数详情
 */
export function taskParamInfo(id: ID) {
  return alovaInstance.get<TaskParamVO>(`/model/taskParam/${id}`);
}

/**
 * 新增模型任务运行参数
 * @param data
 * @returns void
 */
export function taskParamAdd(data: TaskParamForm) {
  return alovaInstance.postWithMsg<void>('/model/taskParam', data);
}

/**
 * 更新模型任务运行参数
 * @param data
 * @returns void
 */
export function taskParamUpdate(data: TaskParamForm) {
  return alovaInstance.putWithMsg<void>('/model/taskParam', data);
}

/**
 * 删除模型任务运行参数
 * @param id id
 * @returns void
 */
export function taskParamRemove(id: ID | IDS) {
  return alovaInstance.deleteWithMsg<void>(`/model/taskParam/${id}`);
}