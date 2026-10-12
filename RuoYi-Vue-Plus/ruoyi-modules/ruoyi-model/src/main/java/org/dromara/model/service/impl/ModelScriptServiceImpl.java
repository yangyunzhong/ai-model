package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelScriptBo;
import org.dromara.model.domain.vo.ModelScriptVo;
import org.dromara.model.domain.ModelScript;
import org.dromara.model.mapper.ModelScriptMapper;
import org.dromara.model.service.IModelScriptService;
import cn.hutool.crypto.digest.DigestUtil;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.entity.UploadResult;
import org.dromara.common.oss.factory.OssFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 模型算法脚本版本Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelScriptServiceImpl implements IModelScriptService {

    private final ModelScriptMapper baseMapper;

    /**
     * 查询模型算法脚本版本
     *
     * @param id 主键
     * @return 模型算法脚本版本
     */
    @Override
    public ModelScriptVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询模型算法脚本版本列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型算法脚本版本分页列表
     */
    @Override
    public TableDataInfo<ModelScriptVo> queryPageList(ModelScriptBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelScript> lqw = buildQueryWrapper(bo);
        Page<ModelScriptVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的模型算法脚本版本列表
     *
     * @param bo 查询条件
     * @return 模型算法脚本版本列表
     */
    @Override
    public List<ModelScriptVo> queryList(ModelScriptBo bo) {
        LambdaQueryWrapper<ModelScript> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelScript> buildQueryWrapper(ModelScriptBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelScript> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelScript::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), ModelScript::getModelId, bo.getModelId());
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), ModelScript::getFileName, bo.getFileName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginalName()), ModelScript::getOriginalName, bo.getOriginalName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileSuffix()), ModelScript::getFileSuffix, bo.getFileSuffix());
        lqw.eq(bo.getFileSize() != null, ModelScript::getFileSize, bo.getFileSize());
        lqw.eq(StringUtils.isNotBlank(bo.getOssId()), ModelScript::getOssId, bo.getOssId());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptUrl()), ModelScript::getScriptUrl, bo.getScriptUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getSha256()), ModelScript::getSha256, bo.getSha256());
        lqw.eq(StringUtils.isNotBlank(bo.getEntryFunction()), ModelScript::getEntryFunction, bo.getEntryFunction());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptVersion()), ModelScript::getScriptVersion, bo.getScriptVersion());
        lqw.eq(StringUtils.isNotBlank(bo.getRequirementsOssId()), ModelScript::getRequirementsOssId, bo.getRequirementsOssId());
        lqw.eq(StringUtils.isNotBlank(bo.getCheckStatus()), ModelScript::getCheckStatus, bo.getCheckStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getCheckMessage()), ModelScript::getCheckMessage, bo.getCheckMessage());
        lqw.eq(StringUtils.isNotBlank(bo.getIsCurrent()), ModelScript::getIsCurrent, bo.getIsCurrent());
        return lqw;
    }

    /**
     * 新增模型算法脚本版本
     *
     * @param bo 模型算法脚本版本
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelScriptBo bo) {
        ModelScript add = MapstructUtils.convert(bo, ModelScript.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改模型算法脚本版本
     *
     * @param bo 模型算法脚本版本
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelScriptBo bo) {
        ModelScript update = MapstructUtils.convert(bo, ModelScript.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelScript entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除模型算法脚本版本信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public ModelScriptVo uploadScript(MultipartFile file, MultipartFile requirements, String modelId, String scriptVersion) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("脚本文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !StringUtils.endsWithIgnoreCase(originalName, ".py")) {
            throw new ServiceException("仅支持上传 .py 脚本文件");
        }
        String suffix = StringUtils.substring(originalName, originalName.lastIndexOf("."), originalName.length());

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ServiceException("读取脚本文件失败");
        }

        // 1) 上传到 MinIO：OssFactory 会自动读取 application 中配置的默认 OSS（minio）
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult = storage.uploadSuffix(bytes, suffix, file.getContentType());

        // 2) 可选依赖清单 requirements.txt 一并上传（对象键写入 requirements_oss_id）
        String requirementsObjectKey = null;
        if (requirements != null && !requirements.isEmpty()) {
            byte[] reqBytes;
            try {
                reqBytes = requirements.getBytes();
            } catch (IOException e) {
                throw new ServiceException("读取依赖清单文件失败");
            }
            UploadResult reqUpload = storage.uploadSuffix(reqBytes, ".txt", requirements.getContentType());
            requirementsObjectKey = reqUpload.getFilename();
        }

        // 3) 同一模型下的旧版本全部置为非当前版本
        baseMapper.update(null, Wrappers.<ModelScript>lambdaUpdate()
            .eq(ModelScript::getModelId, modelId)
            .set(ModelScript::getIsCurrent, "0"));

        // 4) 落库 model_script
        ModelScript entity = new ModelScript();
        entity.setModelId(modelId);
        entity.setFileName(uploadResult.getFilename());   // MinIO 中的存储名（uuid 路径）
        entity.setOriginalName(originalName);             // 原始文件名，如 detect.py
        entity.setFileSuffix(suffix);                     // .py
        entity.setFileSize((long) bytes.length);
        entity.setScriptUrl(uploadResult.getUrl());       // MinIO 访问地址
        entity.setSha256(DigestUtil.sha256Hex(bytes));    // 完整性校验
        entity.setEntryFunction("execute");               // 平台约定入口函数
        entity.setScriptVersion(StringUtils.isBlank(scriptVersion) ? "v1" : scriptVersion);
        entity.setRequirementsOssId(requirementsObjectKey); // 依赖清单在 MinIO 的对象键（可空）
        entity.setCheckStatus("待校验");                   // 待校验/通过/失败
        entity.setIsCurrent("1");                         // 本次上传为当前生效版本
        baseMapper.insert(entity);

        return baseMapper.selectVoById(entity.getId());
    }
}
