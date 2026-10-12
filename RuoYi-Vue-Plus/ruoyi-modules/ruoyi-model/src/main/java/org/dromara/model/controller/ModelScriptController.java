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
import org.dromara.model.domain.vo.ModelScriptVo;
import org.dromara.model.domain.bo.ModelScriptBo;
import org.dromara.model.service.IModelScriptService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模型算法脚本版本
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/model/script")
public class ModelScriptController extends BaseController {

    private final IModelScriptService modelScriptService;

    /**
     * 查询模型算法脚本版本列表
     */
    @SaCheckPermission("model:script:list")
    @GetMapping("/list")
    public TableDataInfo<ModelScriptVo> list(ModelScriptBo bo, PageQuery pageQuery) {
        return modelScriptService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出模型算法脚本版本列表
     */
    @SaCheckPermission("model:script:export")
    @Log(title = "模型算法脚本版本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ModelScriptBo bo, HttpServletResponse response) {
        List<ModelScriptVo> list = modelScriptService.queryList(bo);
        ExcelUtil.exportExcel(list, "模型算法脚本版本", ModelScriptVo.class, response);
    }

    /**
     * 获取模型算法脚本版本详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("model:script:query")
    @GetMapping("/{id}")
    public R<ModelScriptVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(modelScriptService.queryById(id));
    }

    /**
     * 新增模型算法脚本版本
     */
    @SaCheckPermission("model:script:add")
    @Log(title = "模型算法脚本版本", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelScriptBo bo) {
        return toAjax(modelScriptService.insertByBo(bo));
    }

    /**
     * 修改模型算法脚本版本
     */
    @SaCheckPermission("model:script:edit")
    @Log(title = "模型算法脚本版本", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ModelScriptBo bo) {
        return toAjax(modelScriptService.updateByBo(bo));
    }

    /**
     * 上传模型算法脚本（存 MinIO 并落库 model_script）
     *
     * @param file          .py 脚本文件
     * @param requirements  可选依赖清单 requirements.txt
     * @param modelId       所属模型ID
     * @param scriptVersion 版本号（可空）
     */
    @SaCheckPermission("model:script:upload")
    @Log(title = "模型算法脚本版本", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/upload")
    public R<ModelScriptVo> uploadScript(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "requirements", required = false) MultipartFile requirements,
                                        @RequestParam("modelId") String modelId,
                                        @RequestParam(value = "scriptVersion", required = false) String scriptVersion) {
        return R.ok(modelScriptService.uploadScript(file, requirements, modelId, scriptVersion));
    }

    /**
     * 删除模型算法脚本版本
     *
     * @param ids 主键串
     */
    @SaCheckPermission("model:script:remove")
    @Log(title = "模型算法脚本版本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(modelScriptService.deleteWithValidByIds(List.of(ids), true));
    }
}
