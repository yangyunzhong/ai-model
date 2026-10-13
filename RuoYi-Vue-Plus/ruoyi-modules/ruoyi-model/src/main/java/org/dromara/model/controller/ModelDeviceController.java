package org.dromara.model.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.model.service.IModelDeviceService;

/**
 * 设备（metro_device 外部主数据，只读）
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/device")
public class ModelDeviceController extends BaseController {

    private final IModelDeviceService modelDeviceService;

    /**
     * 查询设备列表
     */
    @SaCheckPermission("model:device:list")
    @GetMapping("/list")
    public TableDataInfo<ModelDeviceVo> list(ModelDeviceBo bo, PageQuery pageQuery) {
        return modelDeviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备列表
     */
    @SaCheckPermission("model:device:export")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelDeviceBo bo, HttpServletResponse response) {
        List<ModelDeviceVo> list = modelDeviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备数据", ModelDeviceVo.class, response);
    }

    /**
     * 获取设备详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:device:query")
    @GetMapping("/{id}")
    public R<ModelDeviceVo> getInfo(@NotBlank(message = "主键不能为空")
                                    @PathVariable String id) {
        return R.ok(modelDeviceService.queryById(id));
    }
}
