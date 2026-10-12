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
import org.dromara.model.domain.vo.ModelCatalogVo;
import org.dromara.model.domain.bo.ModelCatalogBo;
import org.dromara.model.service.IModelCatalogService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型类别
 *
 * @author Liu Cheng
 * @date 2026-09-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/catalog")
public class ModelCatalogController extends BaseController {

    private final IModelCatalogService modelCatalogService;

    /**
     * 查询模型类别列表
     */
    @SaCheckPermission("model:catalog:list")
    @GetMapping("/list")
    public TableDataInfo<ModelCatalogVo> list(ModelCatalogBo bo, PageQuery pageQuery) {
        return modelCatalogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型类别列表
     */
    @SaCheckPermission("model:catalog:export")
    @Log(title = "模型类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelCatalogBo bo, HttpServletResponse response) {
        List<ModelCatalogVo> list = modelCatalogService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型类别", ModelCatalogVo.class, response);
    }

    /**
     * 获取模型类别详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:catalog:query")
    @GetMapping("/{id}")
    public R<ModelCatalogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelCatalogService.queryById(id));
    }

    /**
     * 新增模型类别
     */
    @SaCheckPermission("model:catalog:add")
    @Log(title = "模型类别", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelCatalogBo bo) {
        return toAjax(modelCatalogService.insertByBo(bo));
    }

    /**
     * 修改模型类别
     */
    @SaCheckPermission("model:catalog:edit")
    @Log(title = "模型类别", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelCatalogBo bo) {
        return toAjax(modelCatalogService.updateByBo(bo));
    }

    /**
     * 删除模型类别
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:catalog:remove")
    @Log(title = "模型类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelCatalogService.deleteWithValidByIds(List.of(ids), true));
    }
}
