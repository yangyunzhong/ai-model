package org.dromara.model.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.model.domain.vo.ModelExecLogVo;
import org.dromara.model.domain.bo.ModelExecLogBo;
import org.dromara.model.domain.bo.ModelRunOnceBo;
import org.dromara.model.service.IModelExecLogService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型执行记录
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/execLog")
public class ModelExecLogController extends BaseController {

    private final IModelExecLogService modelExecLogService;

    /**
     * 查询模型执行记录列表
     */
    @SaCheckPermission("model:execLog:list")
    @GetMapping("/list")
    public TableDataInfo<ModelExecLogVo> list(ModelExecLogBo bo, PageQuery pageQuery) {
        return modelExecLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型执行记录列表
     */
    @SaCheckPermission("model:execLog:export")
    @Log(title = "模型执行记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelExecLogBo bo, HttpServletResponse response) {
        List<ModelExecLogVo> list = modelExecLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型执行记录", ModelExecLogVo.class, response);
    }

    /**
     * 获取模型执行记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:execLog:query")
    @GetMapping("/{id}")
    public R<ModelExecLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelExecLogService.queryById(id));
    }

    /**
     * 新增模型执行记录
     */
    @SaCheckPermission("model:execLog:add")
    @Log(title = "模型执行记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelExecLogBo bo) {
        return toAjax(modelExecLogService.insertByBo(bo));
    }

    /**
     * 修改模型执行记录
     */
    @SaCheckPermission("model:execLog:edit")
    @Log(title = "模型执行记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelExecLogBo bo) {
        return toAjax(modelExecLogService.updateByBo(bo));
    }

    /**
     * 删除模型执行记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:execLog:remove")
    @Log(title = "模型执行记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelExecLogService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 单次运行模型：按当前生效脚本对目标设备执行一次并落库结果
     */
    @SaCheckPermission("model:execLog:run")
    @Log(title = "模型执行记录", businessType = BusinessType.OTHER)
    @RepeatSubmit()
    @PostMapping("/runOnce")
    public R<String> runOnce(@Validated @RequestBody ModelRunOnceBo bo) {
        return R.ok(modelExecLogService.runOnce(bo));
    }
}
