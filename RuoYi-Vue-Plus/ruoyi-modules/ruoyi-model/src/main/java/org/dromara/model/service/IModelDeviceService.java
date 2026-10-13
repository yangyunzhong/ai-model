package org.dromara.model.service;

import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.List;

/**
 * 设备Service接口（metro_device 外部主数据，只读）
 *
 * @author Lion Li
 * @date 2026-10-13
 */
public interface IModelDeviceService {

    /**
     * 查询设备
     *
     * @param id 主键
     * @return 设备
     */
    ModelDeviceVo queryById(String id);

    /**
     * 分页查询设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备分页列表
     */
    TableDataInfo<ModelDeviceVo> queryPageList(ModelDeviceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备列表
     *
     * @param bo 查询条件
     * @return 设备列表
     */
    List<ModelDeviceVo> queryList(ModelDeviceBo bo);
}
