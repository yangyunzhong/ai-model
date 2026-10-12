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
import org.dromara.model.domain.vo.ModelTaskVo;
import org.dromara.model.domain.bo.ModelTaskBo;
import org.dromara.model.service.IModelTaskService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型任务
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/task")
public class ModelTaskController extends BaseController {

    private final IModelTaskService modelTaskService;

    /**
     * 查询模型任务列表
     */
    @SaCheckPermission("model:task:list")
    @GetMapping("/list")
    public TableDataInfo<ModelTaskVo> list(ModelTaskBo bo, PageQuery pageQuery) {
        return modelTaskService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型任务列表
     */
    @SaCheckPermission("model:task:export")
    @Log(title = "模型任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelTaskBo bo, HttpServletResponse response) {
        List<ModelTaskVo> list = modelTaskService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型任务", ModelTaskVo.class, response);
    }

    /**
     * 获取模型任务详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:task:query")
    @GetMapping("/{id}")
    public R<ModelTaskVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelTaskService.queryById(id));
    }

    /**
     * 新增模型任务
     */
    @SaCheckPermission("model:task:add")
    @Log(title = "模型任务", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelTaskBo bo) {
        return toAjax(modelTaskService.insertByBo(bo));
    }

    /**
     * 修改模型任务
     */
    @SaCheckPermission("model:task:edit")
    @Log(title = "模型任务", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelTaskBo bo) {
        return toAjax(modelTaskService.updateByBo(bo));
    }

    /**
     * 删除模型任务
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:task:remove")
    @Log(title = "模型任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelTaskService.deleteWithValidByIds(List.of(ids), true));
    }
}
