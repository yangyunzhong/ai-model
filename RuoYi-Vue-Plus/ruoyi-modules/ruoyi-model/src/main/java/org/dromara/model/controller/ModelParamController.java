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
import org.dromara.model.domain.vo.ModelParamVo;
import org.dromara.model.domain.bo.ModelParamBo;
import org.dromara.model.service.IModelParamService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型输入输出参数
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/param")
public class ModelParamController extends BaseController {

    private final IModelParamService modelParamService;

    /**
     * 查询模型输入输出参数列表
     */
    @SaCheckPermission("model:param:list")
    @GetMapping("/list")
    public TableDataInfo<ModelParamVo> list(ModelParamBo bo, PageQuery pageQuery) {
        return modelParamService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型输入输出参数列表
     */
    @SaCheckPermission("model:param:export")
    @Log(title = "模型输入输出参数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelParamBo bo, HttpServletResponse response) {
        List<ModelParamVo> list = modelParamService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型输入输出参数", ModelParamVo.class, response);
    }

    /**
     * 获取模型输入输出参数详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:param:query")
    @GetMapping("/{id}")
    public R<ModelParamVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelParamService.queryById(id));
    }

    /**
     * 新增模型输入输出参数
     */
    @SaCheckPermission("model:param:add")
    @Log(title = "模型输入输出参数", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelParamBo bo) {
        return toAjax(modelParamService.insertByBo(bo));
    }

    /**
     * 修改模型输入输出参数
     */
    @SaCheckPermission("model:param:edit")
    @Log(title = "模型输入输出参数", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelParamBo bo) {
        return toAjax(modelParamService.updateByBo(bo));
    }

    /**
     * 删除模型输入输出参数
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:param:remove")
    @Log(title = "模型输入输出参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelParamService.deleteWithValidByIds(List.of(ids), true));
    }
}
