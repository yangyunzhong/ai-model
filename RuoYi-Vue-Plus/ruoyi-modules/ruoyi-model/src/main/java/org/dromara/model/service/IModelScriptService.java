package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelScriptVo;
import org.dromara.model.domain.bo.ModelScriptBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collection;
import java.util.List;

/**
 * 模型算法脚本版本Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IModelScriptService {

    /**
     * 查询模型算法脚本版本
     *
     * @param id 主键
     * @return 模型算法脚本版本
     */
    ModelScriptVo queryById(String id);

    /**
     * 分页查询模型算法脚本版本列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 模型算法脚本版本分页列表
     */
    TableDataInfo<ModelScriptVo> queryPageList(ModelScriptBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的模型算法脚本版本列表
     *
     * @param bo 查询条件
     * @return 模型算法脚本版本列表
     */
    List<ModelScriptVo> queryList(ModelScriptBo bo);

    /**
     * 新增模型算法脚本版本
     *
     * @param bo 模型算法脚本版本
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelScriptBo bo);

    /**
     * 修改模型算法脚本版本
     *
     * @param bo 模型算法脚本版本
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelScriptBo bo);

    /**
     * 校验并批量删除模型算法脚本版本信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 上传算法脚本：文件存 MinIO，元数据落 model_script
     *
     * @param file          .py 脚本文件（必填）
     * @param requirements  可选依赖清单 requirements.txt（可为空）
     * @param modelId       所属模型ID
     * @param scriptVersion 版本号（可空，默认 v1）
     * @return 落库后的脚本记录
     */
    ModelScriptVo uploadScript(MultipartFile file, MultipartFile requirements, String modelId, String scriptVersion);

}
