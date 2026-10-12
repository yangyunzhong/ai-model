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
import org.dromara.model.domain.vo.ModelComparisonRecordVo;
import org.dromara.model.domain.bo.ModelComparisonRecordBo;
import org.dromara.model.service.IModelComparisonRecordService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 数据比对记录
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/comparisonRecord")
public class ModelComparisonRecordController extends BaseController {

    private final IModelComparisonRecordService modelComparisonRecordService;

    /**
     * 查询数据比对记录列表
     */
    @SaCheckPermission("model:comparisonRecord:list")
    @GetMapping("/list")
    public TableDataInfo<ModelComparisonRecordVo> list(ModelComparisonRecordBo bo, PageQuery pageQuery) {
        return modelComparisonRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出数据比对记录列表
     */
    @SaCheckPermission("model:comparisonRecord:export")
    @Log(title = "数据比对记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelComparisonRecordBo bo, HttpServletResponse response) {
        List<ModelComparisonRecordVo> list = modelComparisonRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "数据比对记录", ModelComparisonRecordVo.class, response);
    }

    /**
     * 获取数据比对记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:comparisonRecord:query")
    @GetMapping("/{id}")
    public R<ModelComparisonRecordVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelComparisonRecordService.queryById(id));
    }

    /**
     * 新增数据比对记录
     */
    @SaCheckPermission("model:comparisonRecord:add")
    @Log(title = "数据比对记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelComparisonRecordBo bo) {
        return toAjax(modelComparisonRecordService.insertByBo(bo));
    }

    /**
     * 修改数据比对记录
     */
    @SaCheckPermission("model:comparisonRecord:edit")
    @Log(title = "数据比对记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelComparisonRecordBo bo) {
        return toAjax(modelComparisonRecordService.updateByBo(bo));
    }

    /**
     * 删除数据比对记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:comparisonRecord:remove")
    @Log(title = "数据比对记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelComparisonRecordService.deleteWithValidByIds(List.of(ids), true));
    }
}
