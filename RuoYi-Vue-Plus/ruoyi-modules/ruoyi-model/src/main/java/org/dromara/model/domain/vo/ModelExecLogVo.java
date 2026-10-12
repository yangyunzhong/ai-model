package org.dromara.model.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.model.domain.ModelExecLog;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 模型执行记录视图对象 model_exec_log
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ModelExecLog.class)
public class ModelExecLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 执行编号（如EXE20260921020000001）
     */
    @ExcelProperty(value = "执行编号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=EXE20260921020000001")
    private String execNo;

    /**
     * 模型ID，逻辑关联model_definition.id
     */
    @ExcelProperty(value = "模型ID，逻辑关联model_definition.id")
    private String modelId;

    /**
     * 模型名称（冗余，便于查询/列表展示）
     */
    @ExcelProperty(value = "模型名称", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余，便于查询/列表展示")
    private String modelName;

    /**
     * 任务ID，逻辑关联model_task.id；单次运行/比对时为空
     */
    @ExcelProperty(value = "任务ID，逻辑关联model_task.id；单次运行/比对时为空")
    private String taskId;

    /**
     * 触发方式：定时/单次运行/立即执行/数据比对
     */
    @ExcelProperty(value = "触发方式：定时/单次运行/立即执行/数据比对")
    private String triggerType;

    /**
     * 执行状态：运行中/成功/部分成功/失败/超时
     */
    @ExcelProperty(value = "执行状态：运行中/成功/部分成功/失败/超时")
    private String status;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 耗时（毫秒）
     */
    @ExcelProperty(value = "耗时", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "毫=秒")
    private Long costMs;

    /**
     * 涉及设备数
     */
    @ExcelProperty(value = "涉及设备数")
    private Long deviceCount;

    /**
     * 成功设备数
     */
    @ExcelProperty(value = "成功设备数")
    private Long successCount;

    /**
     * 失败设备数
     */
    @ExcelProperty(value = "失败设备数")
    private Long failCount;

    /**
     * 本次执行入参快照
     */
    @ExcelProperty(value = "本次执行入参快照")
    private String execParams;

    /**
     * 本次使用的脚本版本ID，逻辑关联model_script.id
     */
    @ExcelProperty(value = "本次使用的脚本版本ID，逻辑关联model_script.id")
    private String scriptId;

    /**
     * 错误信息
     */
    @ExcelProperty(value = "错误信息")
    private String errorMsg;

    /**
     * 执行人ID（手动触发时记录）
     */
    @ExcelProperty(value = "执行人ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "手=动触发时记录")
    private String executorId;


}
