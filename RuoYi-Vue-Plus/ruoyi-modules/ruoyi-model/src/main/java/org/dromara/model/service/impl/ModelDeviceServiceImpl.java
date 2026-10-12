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
import org.dromara.model.domain.bo.ModelDeviceBo;
import org.dromara.model.domain.vo.ModelDeviceVo;
import org.dromara.model.domain.ModelDevice;
import org.dromara.model.mapper.ModelDeviceMapper;
import org.dromara.model.service.IModelDeviceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备台账Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ModelDeviceServiceImpl implements IModelDeviceService {

    private final ModelDeviceMapper baseMapper;

    /**
     * 查询设备台账
     *
     * @param id 主键
     * @return 设备台账
     */
    @Override
    public ModelDeviceVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备台账列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备台账分页列表
     */
    @Override
    public TableDataInfo<ModelDeviceVo> queryPageList(ModelDeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ModelDevice> lqw = buildQueryWrapper(bo);
        Page<ModelDeviceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备台账列表
     *
     * @param bo 查询条件
     * @return 设备台账列表
     */
    @Override
    public List<ModelDeviceVo> queryList(ModelDeviceBo bo) {
        LambdaQueryWrapper<ModelDevice> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ModelDevice> buildQueryWrapper(ModelDeviceBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ModelDevice> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ModelDevice::getId);
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), ModelDevice::getDeviceName, bo.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), ModelDevice::getDeviceType, bo.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(bo.getLineId()), ModelDevice::getLineId, bo.getLineId());
        lqw.eq(StringUtils.isNotBlank(bo.getStationId()), ModelDevice::getStationId, bo.getStationId());
        lqw.eq(StringUtils.isNotBlank(bo.getInstallPosition()), ModelDevice::getInstallPosition, bo.getInstallPosition());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ModelDevice::getStatus, bo.getStatus());
        lqw.eq(bo.getSortOrder() != null, ModelDevice::getSortOrder, bo.getSortOrder());
        return lqw;
    }

    /**
     * 新增设备台账
     *
     * @param bo 设备台账
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ModelDeviceBo bo) {
        ModelDevice add = MapstructUtils.convert(bo, ModelDevice.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备台账
     *
     * @param bo 设备台账
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ModelDeviceBo bo) {
        ModelDevice update = MapstructUtils.convert(bo, ModelDevice.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModelDevice entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备台账信息
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
}
