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
import org.dromara.model.domain.vo.ModelDefinitionVo;
import org.dromara.model.domain.bo.ModelDefinitionBo;
import org.dromara.model.service.IModelDefinitionService;
import org.dromara.model.service.IModelRuntimeService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 模型定义
 *
 * @author Lion Li
 * @date 2026-09-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/definition")
public class ModelDefinitionController extends BaseController {

    private final IModelDefinitionService modelDefinitionService;
    private final IModelRuntimeService modelRuntimeService;

    /**
     * 查询模型定义列表
     */
    @SaCheckPermission("model:definition:list")
    @GetMapping("/list")
    public TableDataInfo<ModelDefinitionVo> list(ModelDefinitionBo bo, PageQuery pageQuery) {
        return modelDefinitionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型定义列表
     */
    @SaCheckPermission("model:definition:export")
    @Log(title = "模型定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelDefinitionBo bo, HttpServletResponse response) {
        List<ModelDefinitionVo> list = modelDefinitionService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型定义", ModelDefinitionVo.class, response);
    }

    /**
     * 获取模型定义详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:definition:query")
    @GetMapping("/{id}")
    public R<ModelDefinitionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelDefinitionService.queryById(id));
    }

    /**
     * 新增模型定义
     */
    @SaCheckPermission("model:definition:add")
    @Log(title = "模型定义", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<String> add(@Validated(AddGroup.class) @RequestBody ModelDefinitionBo bo) {
        modelDefinitionService.insertByBo(bo);
        return R.ok("操作成功", bo.getId());
    }

    /**
     * 修改模型定义
     */
    @SaCheckPermission("model:definition:edit")
    @Log(title = "模型定义", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelDefinitionBo bo) {
        return toAjax(modelDefinitionService.updateByBo(bo));
    }

    /**
     * 删除模型定义
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:definition:remove")
    @Log(title = "模型定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelDefinitionService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 发布模型：物化脚本并创建/启动沙箱容器
     *
     * @param id 模型主键
     */
    @SaCheckPermission("model:definition:publish")
    @Log(title = "模型定义", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/publish/{id}")
    public R<Void> publish(@NotNull(message = "主键不能为空")
                           @PathVariable String id) {
        modelRuntimeService.publish(id);
        return R.ok();
    }

    /**
     * 下架模型：停止并删除沙箱容器
     *
     * @param id 模型主键
     */
    @SaCheckPermission("model:definition:offline")
    @Log(title = "模型定义", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/offline/{id}")
    public R<Void> offline(@NotNull(message = "主键不能为空")
                           @PathVariable String id) {
        modelRuntimeService.offline(id);
        return R.ok();
    }
}
