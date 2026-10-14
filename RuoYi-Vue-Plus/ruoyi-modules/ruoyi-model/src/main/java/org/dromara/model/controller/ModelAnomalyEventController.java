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
import org.dromara.model.domain.vo.ModelAnomalyAnalysisVo;
import org.dromara.model.domain.vo.ModelAnomalyEventVo;
import org.dromara.model.domain.bo.ModelAnomalyEventBo;
import org.dromara.model.service.IModelAnomalyEventService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 异物检测事件
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/anomalyEvent")
public class ModelAnomalyEventController extends BaseController {

    private final IModelAnomalyEventService modelAnomalyEventService;

    /**
     * 查询异物检测事件列表
     */
    @SaCheckPermission("model:anomalyEvent:list")
    @GetMapping("/list")
    public TableDataInfo<ModelAnomalyEventVo> list(ModelAnomalyEventBo bo, PageQuery pageQuery) {
        return modelAnomalyEventService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出异物检测事件列表
     */
    @SaCheckPermission("model:anomalyEvent:export")
    @Log(title = "异物检测事件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelAnomalyEventBo bo, HttpServletResponse response) {
        List<ModelAnomalyEventVo> list = modelAnomalyEventService.queryList(bo);
        ExcelUtil.exportExcel(list, "异物检测事件", ModelAnomalyEventVo.class, response);
    }

    /**
     * 异物检测分析聚合查询（趋势曲线/实时监测面板/规则配置/偏差指标）
     */
    @SaCheckPermission("model:anomalyEvent:list")
    @GetMapping("/analysis")
    public R<ModelAnomalyAnalysisVo> analysis(ModelAnomalyEventBo bo) {
        return R.ok(modelAnomalyEventService.analysis(bo));
    }

    /**
     * 获取异物检测事件详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:anomalyEvent:query")
    @GetMapping("/{id}")
    public R<ModelAnomalyEventVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelAnomalyEventService.queryById(id));
    }

    /**
     * 新增异物检测事件
     */
    @SaCheckPermission("model:anomalyEvent:add")
    @Log(title = "异物检测事件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelAnomalyEventBo bo) {
        return toAjax(modelAnomalyEventService.insertByBo(bo));
    }

    /**
     * 修改异物检测事件
     */
    @SaCheckPermission("model:anomalyEvent:edit")
    @Log(title = "异物检测事件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelAnomalyEventBo bo) {
        return toAjax(modelAnomalyEventService.updateByBo(bo));
    }

    /**
     * 删除异物检测事件
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:anomalyEvent:remove")
    @Log(title = "异物检测事件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelAnomalyEventService.deleteWithValidByIds(List.of(ids), true));
    }
}
