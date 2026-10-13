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
import org.dromara.model.domain.vo.MetroStationVo;
import org.dromara.model.domain.bo.MetroStationBo;
import org.dromara.model.service.IMetroStationService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 车站信息
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/station")
public class MetroStationController extends BaseController {

    private final IMetroStationService metroStationService;

    /**
     * 查询车站信息列表
     */
    @SaCheckPermission("model:station:list")
    @GetMapping("/list")
    public TableDataInfo<MetroStationVo> list(MetroStationBo bo, PageQuery pageQuery) {
        return metroStationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出车站信息列表
     */
    @SaCheckPermission("model:station:export")
    @Log(title = "车站信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MetroStationBo bo, HttpServletResponse response) {
        List<MetroStationVo> list = metroStationService.queryList(bo);
        ExcelUtil.exportExcel(list, "车站信息", MetroStationVo.class, response);
    }

    /**
     * 获取车站信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:station:query")
    @GetMapping("/{id}")
    public R<MetroStationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(metroStationService.queryById(id));
    }

    /**
     * 新增车站信息
     */
    @SaCheckPermission("model:station:add")
    @Log(title = "车站信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MetroStationBo bo) {
        return toAjax(metroStationService.insertByBo(bo));
    }

    /**
     * 修改车站信息
     */
    @SaCheckPermission("model:station:edit")
    @Log(title = "车站信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MetroStationBo bo) {
        return toAjax(metroStationService.updateByBo(bo));
    }

    /**
     * 删除车站信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:station:remove")
    @Log(title = "车站信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(metroStationService.deleteWithValidByIds(List.of(ids), true));
    }
}
