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
import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.model.service.IModelDeviceService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备台账
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/device")
public class ModelDeviceController extends BaseController {

    private final IModelDeviceService modelDeviceService;

    /**
     * 查询设备台账列表
     */
    @SaCheckPermission("model:device:list")
    @GetMapping("/list")
    public TableDataInfo<ModelDeviceVo> list(ModelDeviceBo bo, PageQuery pageQuery) {
        return modelDeviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备台账列表
     */
    @SaCheckPermission("model:device:export")
    @Log(title = "设备台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelDeviceBo bo, HttpServletResponse response) {
        List<ModelDeviceVo> list = modelDeviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备台账", ModelDeviceVo.class, response);
    }

    /**
     * 获取设备台账详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:device:query")
    @GetMapping("/{id}")
    public R<ModelDeviceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelDeviceService.queryById(id));
    }

    /**
     * 新增设备台账
     */
    @SaCheckPermission("model:device:add")
    @Log(title = "设备台账", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelDeviceBo bo) {
        return toAjax(modelDeviceService.insertByBo(bo));
    }

    /**
     * 修改设备台账
     */
    @SaCheckPermission("model:device:edit")
    @Log(title = "设备台账", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelDeviceBo bo) {
        return toAjax(modelDeviceService.updateByBo(bo));
    }

    /**
     * 删除设备台账
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:device:remove")
    @Log(title = "设备台账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelDeviceService.deleteWithValidByIds(List.of(ids), true));
    }
}
