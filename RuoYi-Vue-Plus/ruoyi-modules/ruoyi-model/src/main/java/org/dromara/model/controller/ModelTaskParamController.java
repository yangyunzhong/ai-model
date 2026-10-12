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
import org.dromara.model.domain.vo.ModelTaskParamVo;
import org.dromara.model.domain.bo.ModelTaskParamBo;
import org.dromara.model.service.IModelTaskParamService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型任务运行参数
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/taskParam")
public class ModelTaskParamController extends BaseController {

    private final IModelTaskParamService modelTaskParamService;

    /**
     * 查询模型任务运行参数列表
     */
    @SaCheckPermission("model:taskParam:list")
    @GetMapping("/list")
    public TableDataInfo<ModelTaskParamVo> list(ModelTaskParamBo bo, PageQuery pageQuery) {
        return modelTaskParamService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型任务运行参数列表
     */
    @SaCheckPermission("model:taskParam:export")
    @Log(title = "模型任务运行参数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelTaskParamBo bo, HttpServletResponse response) {
        List<ModelTaskParamVo> list = modelTaskParamService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型任务运行参数", ModelTaskParamVo.class, response);
    }

    /**
     * 获取模型任务运行参数详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:taskParam:query")
    @GetMapping("/{id}")
    public R<ModelTaskParamVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelTaskParamService.queryById(id));
    }

    /**
     * 新增模型任务运行参数
     */
    @SaCheckPermission("model:taskParam:add")
    @Log(title = "模型任务运行参数", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelTaskParamBo bo) {
        return toAjax(modelTaskParamService.insertByBo(bo));
    }

    /**
     * 修改模型任务运行参数
     */
    @SaCheckPermission("model:taskParam:edit")
    @Log(title = "模型任务运行参数", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelTaskParamBo bo) {
        return toAjax(modelTaskParamService.updateByBo(bo));
    }

    /**
     * 删除模型任务运行参数
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:taskParam:remove")
    @Log(title = "模型任务运行参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelTaskParamService.deleteWithValidByIds(List.of(ids), true));
    }
}
