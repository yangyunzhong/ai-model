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
import org.dromara.model.domain.vo.ModelExecLogDetailVo;
import org.dromara.model.domain.bo.ModelExecLogDetailBo;
import org.dromara.model.service.IModelExecLogDetailService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型执行日志明细
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/execLogDetail")
public class ModelExecLogDetailController extends BaseController {

    private final IModelExecLogDetailService modelExecLogDetailService;

    /**
     * 查询模型执行日志明细列表
     */
    @SaCheckPermission("model:execLogDetail:list")
    @GetMapping("/list")
    public TableDataInfo<ModelExecLogDetailVo> list(ModelExecLogDetailBo bo, PageQuery pageQuery) {
        return modelExecLogDetailService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型执行日志明细列表
     */
    @SaCheckPermission("model:execLogDetail:export")
    @Log(title = "模型执行日志明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelExecLogDetailBo bo, HttpServletResponse response) {
        List<ModelExecLogDetailVo> list = modelExecLogDetailService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型执行日志明细", ModelExecLogDetailVo.class, response);
    }

    /**
     * 获取模型执行日志明细详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:execLogDetail:query")
    @GetMapping("/{id}")
    public R<ModelExecLogDetailVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelExecLogDetailService.queryById(id));
    }

    /**
     * 新增模型执行日志明细
     */
    @SaCheckPermission("model:execLogDetail:add")
    @Log(title = "模型执行日志明细", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelExecLogDetailBo bo) {
        return toAjax(modelExecLogDetailService.insertByBo(bo));
    }

    /**
     * 修改模型执行日志明细
     */
    @SaCheckPermission("model:execLogDetail:edit")
    @Log(title = "模型执行日志明细", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelExecLogDetailBo bo) {
        return toAjax(modelExecLogDetailService.updateByBo(bo));
    }

    /**
     * 删除模型执行日志明细
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:execLogDetail:remove")
    @Log(title = "模型执行日志明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelExecLogDetailService.deleteWithValidByIds(List.of(ids), true));
    }
}
