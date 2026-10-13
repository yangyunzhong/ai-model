package org.dromara.model.service.impl;

import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.model.mapper.ModelDeviceMapper;
import org.dromara.model.service.IModelDeviceService;

import java.util.List;

/**
 * 设备Service业务层处理（metro_device 外部主数据，只读）
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelDeviceServiceImpl implements IModelDeviceService {

    private final ModelDeviceMapper baseMapper;

    /**
     * 查询设备
     *
     * @param id 主键
     * @return 设备
     */
    @Override
    public ModelDeviceVo queryById(String id){
        return baseMapper.selectVoByIdWithRef(id);
    }

    /**
     * 分页查询设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备分页列表
     */
    @Override
    public TableDataInfo<ModelDeviceVo> queryPageList(ModelDeviceBo bo, PageQuery pageQuery) {
        Page<ModelDeviceVo> result = baseMapper.selectVoPageWithRef(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备列表
     *
     * @param bo 查询条件
     * @return 设备列表
     */
    @Override
    public List<ModelDeviceVo> queryList(ModelDeviceBo bo) {
        return baseMapper.selectVoListWithRef(bo);
    }
}
