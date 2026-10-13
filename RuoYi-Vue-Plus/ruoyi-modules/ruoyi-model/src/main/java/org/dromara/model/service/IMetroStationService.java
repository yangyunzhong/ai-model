package org.dromara.model.service;

import org.dromara.model.domain.vo.MetroStationVo;
import org.dromara.model.domain.bo.MetroStationBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 车站信息Service接口
 *
 * @author Lion Li
 * @date 2026-10-13
 */
public interface IMetroStationService {

    /**
     * 查询车站信息
     *
     * @param id 主键
     * @return 车站信息
     */
    MetroStationVo queryById(String id);

    /**
     * 分页查询车站信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 车站信息分页列表
     */
    TableDataInfo<MetroStationVo> queryPageList(MetroStationBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的车站信息列表
     *
     * @param bo 查询条件
     * @return 车站信息列表
     */
    List<MetroStationVo> queryList(MetroStationBo bo);

    /**
     * 新增车站信息
     *
     * @param bo 车站信息
     * @return 是否新增成功
     */
    Boolean insertByBo(MetroStationBo bo);

    /**
     * 修改车站信息
     *
     * @param bo 车站信息
     * @return 是否修改成功
     */
    Boolean updateByBo(MetroStationBo bo);

    /**
     * 校验并批量删除车站信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
