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
import org.dromara.model.domain.vo.ModelComparisonDetailVo;
import org.dromara.model.domain.bo.ModelComparisonDetailBo;
import org.dromara.model.service.IModelComparisonDetailService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 比对详细结果
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/comparisonDetail")
public class ModelComparisonDetailController extends BaseController {

    private final IModelComparisonDetailService modelComparisonDetailService;

    /**
     * 查询比对详细结果列表
     */
    @SaCheckPermission("model:comparisonDetail:list")
    @GetMapping("/list")
    public TableDataInfo<ModelComparisonDetailVo> list(ModelComparisonDetailBo bo, PageQuery pageQuery) {
        return modelComparisonDetailService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出比对详细结果列表
     */
    @SaCheckPermission("model:comparisonDetail:export")
    @Log(title = "比对详细结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelComparisonDetailBo bo, HttpServletResponse response) {
        List<ModelComparisonDetailVo> list = modelComparisonDetailService.queryList(bo);
        ExcelUtil.exportExcel(list, "比对详细结果", ModelComparisonDetailVo.class, response);
    }

    /**
     * 获取比对详细结果详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:comparisonDetail:query")
    @GetMapping("/{id}")
    public R<ModelComparisonDetailVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelComparisonDetailService.queryById(id));
    }

    /**
     * 新增比对详细结果
     */
    @SaCheckPermission("model:comparisonDetail:add")
    @Log(title = "比对详细结果", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelComparisonDetailBo bo) {
        return toAjax(modelComparisonDetailService.insertByBo(bo));
    }

    /**
     * 修改比对详细结果
     */
    @SaCheckPermission("model:comparisonDetail:edit")
    @Log(title = "比对详细结果", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelComparisonDetailBo bo) {
        return toAjax(modelComparisonDetailService.updateByBo(bo));
    }

    /**
     * 删除比对详细结果
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:comparisonDetail:remove")
    @Log(title = "比对详细结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelComparisonDetailService.deleteWithValidByIds(List.of(ids), true));
    }
}
