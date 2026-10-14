package org.dromara.model.service.impl;

import com.aizuda.snailjob.client.job.core.enums.TriggerTypeEnum;
import com.aizuda.snailjob.client.job.core.openapi.SnailJobOpenApi;
import com.aizuda.snailjob.common.core.enums.StatusEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelRunOnceBo;
import org.dromara.model.domain.bo.ModelTaskBo;
import org.dromara.model.domain.vo.ModelTaskVo;
import org.dromara.model.domain.ModelTask;
import org.dromara.model.mapper.ModelTaskMapper;
import org.dromara.model.service.IModelExecLogService;
import org.dromara.model.service.IModelTaskService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模型任务Service业务层处理
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelTaskServiceImpl implements IModelTaskService {

    /**
     * 任务状态：暂停 / 运行中
     */
    private static final String STATUS_PAUSED = "暂停";
    private static final String STATUS_RUNNING = "运行中";

    /**
     * 调度类型：周期调度（cron 表达式）/ 一次性调度（指定时间点）
     */
    private static final String SCHEDULE_TYPE_CRON = "cron";
    private static final String SCHEDULE_TYPE_ONCE = "once";

    /**
     * 一次性调度触发时间格式（与前端 DatePicker/TimePicker 提交格式一致）
     */
    private static final String ONCE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * SnailJob 模型任务执行器名称，需与 {@code ModelTaskJobExecutor} 上的 @JobExecutor(name) 保持一致
     */
    private static final String JOB_EXECUTOR_NAME = "modelTaskExecutor";

    /**
     * 立即执行触发方式
     */
    private static final String TRIGGER_TYPE_IMMEDIATE = "立即执行";

    private final ModelTaskMapper baseMapper;
    private final IModelExecLogService execLogService;

    /**
     * 查询模型任务
     *
     * @param id 主键
     * @return 模型任务
     */
    @Override
    public ModelTaskVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型任务列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型任务分页列表
     */
    @Override
    public TableDataInfo<ModelTaskVo> queryPageList(ModelTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelTask> lqw = buildQueryWrapper(bo);
        Page<ModelTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型任务列表
     *
     * @param bo 查询条件
     * @return 模型任务列表
     */
    @Override
    public List<ModelTaskVo> queryList(ModelTaskBo bo) {
        LambdaQueryWrapper<ModelTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelTask> buildQueryWrapper(ModelTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelTask::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelTask::getModelId, bo.getModelId());
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), ModelTask::getTaskName, bo.getTaskName());
        lqw.eq(StringUtils.isNotBlank(bo.getCronExpression()), ModelTask::getCronExpression, bo.getCronExpression());
        lqw.eq(StringUtils.isNotBlank(bo.getCronDescription()), ModelTask::getCronDescription, bo.getCronDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ModelTask::getStatus, bo.getStatus());
        lqw.eq(bo.getLastExecuteTime() != null, ModelTask::getLastExecuteTime, bo.getLastExecuteTime());
        lqw.eq(bo.getNextExecuteTime() != null, ModelTask::getNextExecuteTime, bo.getNextExecuteTime());
        return lqw;
    }

    /**
     * 新增模型任务
     *
     * @param bo 模型任务
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelTaskBo bo) {
        ModelTask add = MapstructUtils.convert(bo, ModelTask.class);
        normalizeSchedule(add);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            createJobIfNeeded(add);
        }
        return flag;
    }

    /**
     * 修改模型任务
     *
     * @param bo 模型任务
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelTaskBo bo) {
        ModelTask update = MapstructUtils.convert(bo, ModelTask.class);
        normalizeSchedule(update);
        validEntityBeforeSave(update);
        // 显式 set 两个调度字段，保证 cron <-> once 切换时另一侧字段被清空
        // （MyBatis-Plus 默认 NOT_NULL 策略不会更新 null 字段）
        boolean flag = baseMapper.update(null, Wrappers.<ModelTask>lambdaUpdate()
                .eq(ModelTask::getId, update.getId())
                .set(ModelTask::getTaskName, update.getTaskName())
                .set(ModelTask::getCronExpression, update.getCronExpression())
                .set(ModelTask::getCronDescription, update.getCronDescription())
                .set(ModelTask::getScheduleType, update.getScheduleType())
                .set(ModelTask::getNextExecuteTime, update.getNextExecuteTime())
                .set(ModelTask::getRemark, update.getRemark())) > 0;
        if (flag) {
            createJobIfNeeded(baseMapper.selectById(bo.getId()));
        }
        return flag;
    }

    /**
     * 规范化调度类型：once 只保留执行时间，cron 只保留表达式
     */
    private void normalizeSchedule(ModelTask task) {
        if (SCHEDULE_TYPE_ONCE.equals(task.getScheduleType())) {
            task.setScheduleType(SCHEDULE_TYPE_ONCE);
            task.setCronExpression(null);
        } else {
            task.setScheduleType(SCHEDULE_TYPE_CRON);
            // 周期任务按 cron 重算"下次执行时间"，避免页面一直显示旧值或空值
            task.setNextExecuteTime(nextCronTime(task.getCronExpression()));
        }
    }

    /**
     * 解析 cron 表达式：5 位 unix（分 时 日 月 周）自动补秒位，兼容 6 位（秒 分 时 日 月 周）格式。
     * 表达式为空或非法时返回 null。
     *
     * @param cronExpression cron 表达式
     * @return 解析结果，无法解析时为 null
     */
    private CronExpression parseCron(String cronExpression) {
        if (StringUtils.isBlank(cronExpression)) {
            return null;
        }
        try {
            String trimmed = cronExpression.trim();
            // 5 位 unix cron 补秒位转 Spring 6 位格式解析
            String springCron = trimmed.split("\\s+").length == 5 ? "0 " + trimmed : trimmed;
            return CronExpression.parse(springCron);
        } catch (Exception e) {
            log.warn("cron 表达式解析失败, expression => {}", cronExpression, e);
            return null;
        }
    }

    /**
     * 按 unix cron（5 位：分 时 日 月 周）计算下一次执行时间，表达式无效时返回 null
     */
    private Date nextCronTime(String cronExpression) {
        CronExpression cron = parseCron(cronExpression);
        if (cron == null) {
            return null;
        }
        LocalDateTime next = cron.next(LocalDateTime.now());
        return next == null ? null : Date.from(next.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 保存前的数据校验：按调度类型校验周期配置
     */
    private void validEntityBeforeSave(ModelTask entity){
        if (SCHEDULE_TYPE_ONCE.equals(entity.getScheduleType())) {
            if (entity.getNextExecuteTime() == null) {
                throw new ServiceException("一次性执行必须指定执行时间");
            }
            if (entity.getNextExecuteTime().before(new Date())) {
                throw new ServiceException("一次性执行时间不能早于当前时间");
            }
        } else if (StringUtils.isBlank(entity.getCronExpression())) {
            throw new ServiceException("周期执行必须配置 cron 表达式");
        } else if (parseCron(entity.getCronExpression()) == null) {
            throw new ServiceException("cron 表达式不合法：" + entity.getCronExpression()
                + "，请使用 5 位格式（分 时 日 月 周），例如每天 00:00 填写 0 0 * * *");
        }
    }

    /**
     * 校验并批量删除模型任务信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        Set<Long> jobIds = baseMapper.selectList(Wrappers.<ModelTask>lambdaQuery()
                .in(ModelTask::getId, ids))
            .stream()
            .map(ModelTask::getJobId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (!jobIds.isEmpty()) {
            try {
                SnailJobOpenApi.deleteJob(jobIds).execute();
            } catch (Exception e) {
                log.warn("删除 SnailJob 作业失败（不影响任务删除）, jobIds => {}, 原因 => {}", jobIds, e.getMessage());
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 注册模型后自动创建初始任务
     *
     * <p>初始任务仅关联模型ID，任务名称、执行周期、最近执行、下次执行均为空，状态默认「暂停」。</p>
     *
     * @param modelId 关联模型ID
     */
    @Override
    public void createInitialTask(String modelId) {
        if (StringUtils.isBlank(modelId)) {
            return;
        }
        Long count = baseMapper.selectCount(Wrappers.<ModelTask>lambdaQuery()
            .eq(ModelTask::getModelId, modelId));
        if (count != null && count > 0) {
            return;
        }
        ModelTask task = new ModelTask();
        task.setModelId(modelId);
        task.setStatus(STATUS_PAUSED);
        baseMapper.insert(task);
        log.info("模型注册后自动创建初始任务, modelId => {}, taskId => {}", modelId, task.getId());
    }

    /**
     * 立即执行模型任务：对该模型适用设备类型下的全部启用设备执行一次计算并落库
     *
     * @param id 任务主键
     * @return 是否执行成功
     */
    @Override
    public Boolean runNow(String id) {
        ModelTask task = baseMapper.selectById(id);
        if (task == null) {
            throw new ServiceException("模型任务不存在：" + id);
        }
        if (!hasScheduleConfigured(task)) {
            throw new ServiceException("请先配置执行周期，再立即执行");
        }
        if (StringUtils.isBlank(task.getModelId())) {
            throw new ServiceException("模型任务未关联模型：" + id);
        }
        // 不传 deviceIds：由 resolveDevices 按模型适用设备类型取全部启用设备
        ModelRunOnceBo bo = new ModelRunOnceBo();
        bo.setModelId(task.getModelId());
        bo.setTaskId(task.getId());
        bo.setTriggerType(TRIGGER_TYPE_IMMEDIATE);
        execLogService.runOnce(bo);

        ModelTask patch = new ModelTask();
        patch.setId(task.getId());
        patch.setLastExecuteTime(new Date());
        baseMapper.updateById(patch);
        return true;
    }

    /**
     * 任务触发完成后的收尾：回写最近执行时间。
     * 周期任务滚动计算下一次执行时间；一次性任务额外将状态置为「暂停」，
     * 平台侧不再重复调度；SnailJob 侧 POINT_IN_TIME 作业触发一次后由服务端自动完结。
     *
     * @param taskId 任务主键
     */
    @Override
    public void finishOnceTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return;
        }
        ModelTask task = baseMapper.selectById(taskId);
        if (task == null) {
            return;
        }
        ModelTask patch = new ModelTask();
        patch.setId(task.getId());
        patch.setLastExecuteTime(new Date());
        // 周期任务：滚动计算下一次执行时间，供页面「下次执行」展示
        if (SCHEDULE_TYPE_CRON.equals(task.getScheduleType())) {
            patch.setNextExecuteTime(nextCronTime(task.getCronExpression()));
            baseMapper.updateById(patch);
            return;
        }
        if (!STATUS_RUNNING.equals(task.getStatus())) {
            baseMapper.updateById(patch);
            return;
        }
        patch.setStatus(STATUS_PAUSED);
        baseMapper.updateById(patch);
        log.info("一次性模型任务执行完成，已自动暂停, taskId => {}", taskId);
    }

    /**
     * 切换任务启停状态，并同步 SnailJob 作业状态
     *
     * @param id 任务主键
     * @return 是否切换成功
     */
    @Override
    public Boolean toggleStatus(String id) {
        ModelTask task = baseMapper.selectById(id);
        if (task == null) {
            throw new ServiceException("模型任务不存在：" + id);
        }
        String next = STATUS_RUNNING.equals(task.getStatus()) ? STATUS_PAUSED : STATUS_RUNNING;
        // 未配置执行周期（cron / 一次性时间）的任务不允许开启，避免空调度
        if (STATUS_RUNNING.equals(next) && !hasScheduleConfigured(task)) {
            throw new ServiceException("请先配置执行周期，再开启任务");
        }
        ModelTask patch = new ModelTask();
        patch.setId(task.getId());
        patch.setStatus(next);
        // 开启时刷新"下次执行时间"：cron 型按表达式重算，避免页面显示旧值或空值
        if (STATUS_RUNNING.equals(next)) {
            Date nextExecuteTime = SCHEDULE_TYPE_ONCE.equals(task.getScheduleType())
                ? task.getNextExecuteTime() : nextCronTime(task.getCronExpression());
            if (nextExecuteTime != null) {
                patch.setNextExecuteTime(nextExecuteTime);
                task.setNextExecuteTime(nextExecuteTime);
            }
        }
        baseMapper.updateById(patch);
        task.setStatus(next);
        syncJobStatus(task);
        return true;
    }

    /**
     * 任务是否已配置调度周期（cron 表达式或一次性执行时间）
     */
    private boolean hasScheduleConfigured(ModelTask task) {
        if (task == null) {
            return false;
        }
        if (SCHEDULE_TYPE_ONCE.equals(task.getScheduleType())) {
            return task.getNextExecuteTime() != null;
        }
        return StringUtils.isNotBlank(task.getCronExpression());
    }

    /**
     * 创建或更新 SnailJob 作业（需已配置执行周期：cron 表达式或一次性时间点）
     *
     * <p>周期调度使用 CRON 触发；一次性调度使用 POINT_IN_TIME 触发（triggerInterval 传毫秒时间戳）。
     * SnailJob 服务端不可用时仅记录日志，不阻断任务保存。</p>
     */
    private void createJobIfNeeded(ModelTask task) {
        if (task == null || !hasScheduleConfigured(task)) {
            return;
        }
        boolean once = SCHEDULE_TYPE_ONCE.equals(task.getScheduleType());
        TriggerTypeEnum triggerType = once ? TriggerTypeEnum.POINT_IN_TIME : TriggerTypeEnum.CRON;
        // SnailJob 的 CRON 为 6 位（秒 分 时 日 月 周），平台约定 5 位需补秒位后下发
        String triggerInterval = once
            ? String.valueOf(task.getNextExecuteTime().getTime())
            : toQuartzCron(task.getCronExpression());
        StatusEnum status = toSnailStatus(task.getStatus());
        try {
            if (task.getJobId() != null && updateJob(task, status, triggerType, triggerInterval)) {
                return;
            }
            Long jobId = SnailJobOpenApi.addClusterJob()
                .setJobName(resolveJobName(task))
                .setExecutorInfo(JOB_EXECUTOR_NAME)
                .setTriggerType(triggerType)
                .setTriggerInterval(triggerInterval)
                .setJobStatus(status)
                .setDescription(resolveJobDescription(task))
                .addArgsStr("modelId", task.getModelId())
                .addArgsStr("taskId", task.getId())
                .execute();
            if (jobId != null) {
                ModelTask patch = new ModelTask();
                patch.setId(task.getId());
                patch.setJobId(jobId);
                baseMapper.updateById(patch);
                task.setJobId(jobId);
            }
            log.info("已创建 SnailJob 作业, taskId => {}, jobId => {}, triggerType => {}, triggerInterval => {}",
                task.getId(), jobId, triggerType, triggerInterval);
        } catch (Exception e) {
            log.warn("同步 SnailJob 作业失败（不影响任务保存）, taskId => {}, 原因 => {}", task.getId(), e.getMessage());
        }
    }

    /**
     * 更新已存在的 SnailJob 作业，失败返回 false 以便上层回退为重新创建
     */
    private boolean updateJob(ModelTask task, StatusEnum status, TriggerTypeEnum triggerType, String triggerInterval) {
        try {
            SnailJobOpenApi.updateClusterJob(task.getJobId())
                .setJobName(resolveJobName(task))
                .setExecutorInfo(JOB_EXECUTOR_NAME)
                .setTriggerType(triggerType)
                .setTriggerInterval(triggerInterval)
                .setJobStatus(status)
                .setDescription(resolveJobDescription(task))
                .execute();
            log.info("已更新 SnailJob 作业, taskId => {}, jobId => {}, triggerType => {}, triggerInterval => {}",
                task.getId(), task.getJobId(), triggerType, triggerInterval);
            return true;
        } catch (Exception e) {
            log.warn("更新 SnailJob 作业失败，将尝试重新创建, taskId => {}, jobId => {}, 原因 => {}",
                task.getId(), task.getJobId(), e.getMessage());
            return false;
        }
    }

    /**
     * 同步既有作业的启停状态（无作业时按需创建）
     */
    private void syncJobStatus(ModelTask task) {
        if (task.getJobId() == null) {
            createJobIfNeeded(task);
            return;
        }
        try {
            SnailJobOpenApi.updateJobStatus(task.getJobId())
                .setStatus(toSnailStatus(task.getStatus()))
                .execute();
            log.info("已同步 SnailJob 作业状态, taskId => {}, jobId => {}, status => {}",
                task.getId(), task.getJobId(), task.getStatus());
        } catch (Exception e) {
            log.warn("同步 SnailJob 作业状态失败, taskId => {}, jobId => {}, 原因 => {}",
                task.getId(), task.getJobId(), e.getMessage());
        }
    }

    /**
     * 平台任务状态映射为 SnailJob 作业状态
     */
    private StatusEnum toSnailStatus(String status) {
        return STATUS_RUNNING.equals(status) ? StatusEnum.YES : StatusEnum.NO;
    }

    /**
     * 平台 5 位 cron（分 时 日 月 周）转换为 SnailJob 要求的 Quartz 6 位 cron（秒 分 时 日 月 周）。
     * Quartz 规定 day-of-month 与 day-of-week 不能同时指定（均为 * 也不行），需将其中一个置为 ?
     */
    private static String toQuartzCron(String cron) {
        String trimmed = StringUtils.trimToEmpty(cron);
        if (StringUtils.countMatches(trimmed, ' ') != 4) {
            return trimmed;
        }
        String[] parts = ("0 " + trimmed).split(" ");
        if ("*".equals(parts[5])) {
            parts[5] = "?";
        } else {
            parts[3] = "?";
        }
        return String.join(" ", parts);
    }

    /**
     * 作业名称：优先使用任务名称
     */
    private String resolveJobName(ModelTask task) {
        return StringUtils.isNotBlank(task.getTaskName())
            ? "模型任务-" + task.getTaskName()
            : "模型任务-" + task.getId();
    }

    /**
     * 作业描述
     */
    private String resolveJobDescription(ModelTask task) {
        return "模型管理平台定时任务，模型ID：" + task.getModelId();
    }
}
