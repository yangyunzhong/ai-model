package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelComparisonRecordVo;
import org.dromara.model.domain.vo.ModelComparisonExecuteVo;
import org.dromara.model.domain.bo.ModelComparisonRecordBo;
import org.dromara.model.domain.bo.ModelComparisonExecuteBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 数据比对记录Service接口
 *
 * @author Lion Li
 * @date 2026-09-30
 */
public interface IModelComparisonRecordService {

    /**
     * 查询数据比对记录
     *
     * @param id 主键
     * @return 数据比对记录
     */
    ModelComparisonRecordVo queryById(String id);

    /**
     * 分页查询数据比对记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 数据比对记录分页列表
     */
    TableDataInfo<ModelComparisonRecordVo> queryPageList(ModelComparisonRecordBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的数据比对记录列表
     *
     * @param bo 查询条件
     * @return 数据比对记录列表
     */
    List<ModelComparisonRecordVo> queryList(ModelComparisonRecordBo bo);

    /**
     * 新增数据比对记录
     *
     * @param bo 数据比对记录
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelComparisonRecordBo bo);

    /**
     * 修改数据比对记录
     *
     * @param bo 数据比对记录
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelComparisonRecordBo bo);

    /**
     * 校验并批量删除数据比对记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 执行数据比对：取各模型对同一批设备的历史结果，落库并返回比对矩阵
     *
     * @param bo 比对执行参数（设备品种、设备列表、模型列表）
     * @return 比对结果（模型、设备、评分明细与一致性摘要）
     */
    ModelComparisonExecuteVo execute(ModelComparisonExecuteBo bo);
}
