package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备台账Service接口
 *
 * @author Lion Li
 * @date 2026-10-08
 */
public interface IModelDeviceService {

    /**
     * 查询设备台账
     *
     * @param id 主键
     * @return 设备台账
     */
    ModelDeviceVo queryById(String id);

    /**
     * 分页查询设备台账列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备台账分页列表
     */
    TableDataInfo<ModelDeviceVo> queryPageList(ModelDeviceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备台账列表
     *
     * @param bo 查询条件
     * @return 设备台账列表
     */
    List<ModelDeviceVo> queryList(ModelDeviceBo bo);

    /**
     * 新增设备台账
     *
     * @param bo 设备台账
     * @return 是否新增成功
     */
    Boolean insertByBo(ModelDeviceBo bo);

    /**
     * 修改设备台账
     *
     * @param bo 设备台账
     * @return 是否修改成功
     */
    Boolean updateByBo(ModelDeviceBo bo);

    /**
     * 校验并批量删除设备台账信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
