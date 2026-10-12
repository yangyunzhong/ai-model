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
import org.dromara.model.domain.vo.ModelResultVo;
import org.dromara.model.domain.bo.ModelResultBo;
import org.dromara.model.service.IModelResultService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型执行结果明细
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/result")
public class ModelResultController extends BaseController {

    private final IModelResultService modelResultService;

    /**
     * 查询模型执行结果明细列表
     */
    @SaCheckPermission("model:result:list")
    @GetMapping("/list")
    public TableDataInfo<ModelResultVo> list(ModelResultBo bo, PageQuery pageQuery) {
        return modelResultService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型执行结果明细列表
     */
    @SaCheckPermission("model:result:export")
    @Log(title = "模型执行结果明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelResultBo bo, HttpServletResponse response) {
        List<ModelResultVo> list = modelResultService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型执行结果明细", ModelResultVo.class, response);
    }

    /**
     * 获取模型执行结果明细详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:result:query")
    @GetMapping("/{id}")
    public R<ModelResultVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelResultService.queryById(id));
    }

    /**
     * 新增模型执行结果明细
     */
    @SaCheckPermission("model:result:add")
    @Log(title = "模型执行结果明细", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelResultBo bo) {
        return toAjax(modelResultService.insertByBo(bo));
    }

    /**
     * 修改模型执行结果明细
     */
    @SaCheckPermission("model:result:edit")
    @Log(title = "模型执行结果明细", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelResultBo bo) {
        return toAjax(modelResultService.updateByBo(bo));
    }

    /**
     * 删除模型执行结果明细
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:result:remove")
    @Log(title = "模型执行结果明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelResultService.deleteWithValidByIds(List.of(ids), true));
    }
}
