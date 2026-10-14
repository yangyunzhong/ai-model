package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelAnomalyAnalysisVo;
import org.dromara.model.domain.vo.ModelAnomalyEventVo;
import org.dromara.model.domain.bo.ModelAnomalyEventBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 异物检测事件Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IModelAnomalyEventService {

    /**
     * 查询异物检测事件
     *
     * @param id 主键
     * @return 异物检测事件
     */
    ModelAnomalyEventVo queryById(String id);

    /**
     * 分页查询异物检测事件列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 异物检测事件分页列表
     */
    TableDataInfo<ModelAnomalyEventVo> queryPageList(ModelAnomalyEventBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的异物检测事件列表
     *
     * @param bo 查询条件
     * @return 异物检测事件列表
     */
    List<ModelAnomalyEventVo> queryList(ModelAnomalyEventBo bo);

    /**
     * 新增异物检测事件
     *
     * @param bo 异物检测事件
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelAnomalyEventBo bo);

    /**
     * 修改异物检测事件
     *
     * @param bo 异物检测事件
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelAnomalyEventBo bo);

    /**
     * 校验并批量删除异物检测事件信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 异物检测分析聚合查询：趋势曲线、实时监测面板、规则配置与偏差指标
     *
     * @param bo 过滤条件（设备ID/设备类型/线路/车站）
     * @return 分析结果
     */
    ModelAnomalyAnalysisVo analysis(ModelAnomalyEventBo bo);
}
