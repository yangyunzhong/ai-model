package org.dromara.model.service;

import org.dromara.model.domain.vo.MetroLineVo;
import org.dromara.model.domain.bo.MetroLineBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 线路信息Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IMetroLineService {

    /**
     * 查询线路信息
     *
     * @param id 主键
     * @return 线路信息
     */
    MetroLineVo queryById(String id);

    /**
     * 分页查询线路信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 线路信息分页列表
     */
    TableDataInfo<MetroLineVo> queryPageList(MetroLineBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的线路信息列表
     *
     * @param bo 查询条件
     * @return 线路信息列表
     */
    List<MetroLineVo> queryList(MetroLineBo bo);

    /**
     * 新增线路信息
     *
     * @param bo 线路信息
     * @return 是否新增成功
     */
    Boolean insertByBo(MetroLineBo bo);

    /**
     * 修改线路信息
     *
     * @param bo 线路信息
     * @return 是否修改成功
     */
    Boolean updateByBo(MetroLineBo bo);

    /**
     * 校验并批量删除线路信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
