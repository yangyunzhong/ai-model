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
import org.dromara.model.domain.vo.MetroLineVo;
import org.dromara.model.domain.bo.MetroLineBo;
import org.dromara.model.service.IMetroLineService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 线路信息
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/line")
public class MetroLineController extends BaseController {

    private final IMetroLineService metroLineService;

    /**
     * 查询线路信息列表
     */
    @SaCheckPermission("model:line:list")
    @GetMapping("/list")
    public TableDataInfo<MetroLineVo> list(MetroLineBo bo, PageQuery pageQuery) {
        return metroLineService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出线路信息列表
     */
    @SaCheckPermission("model:line:export")
    @Log(title = "线路信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MetroLineBo bo, HttpServletResponse response) {
        List<MetroLineVo> list = metroLineService.queryList(bo);
        ExcelUtil.exportExcel(list, "线路信息", MetroLineVo.class, response);
    }

    /**
     * 获取线路信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:line:query")
    @GetMapping("/{id}")
    public R<MetroLineVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(metroLineService.queryById(id));
    }

    /**
     * 新增线路信息
     */
    @SaCheckPermission("model:line:add")
    @Log(title = "线路信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MetroLineBo bo) {
        return toAjax(metroLineService.insertByBo(bo));
    }

    /**
     * 修改线路信息
     */
    @SaCheckPermission("model:line:edit")
    @Log(title = "线路信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MetroLineBo bo) {
        return toAjax(metroLineService.updateByBo(bo));
    }

    /**
     * 删除线路信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:line:remove")
    @Log(title = "线路信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(metroLineService.deleteWithValidByIds(List.of(ids), true));
    }
}
