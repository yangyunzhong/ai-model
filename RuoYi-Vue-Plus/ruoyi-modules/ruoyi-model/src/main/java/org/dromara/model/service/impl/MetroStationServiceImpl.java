package org.dromara.model.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.model.domain.bo.MetroStationBo;
import org.dromara.model.domain.vo.MetroStationVo;
import org.dromara.model.domain.MetroStation;
import org.dromara.model.mapper.MetroStationMapper;
import org.dromara.model.service.IMetroStationService;

import java.util.List;
import java.util.Collection;

/**
 * 车站信息Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MetroStationServiceImpl implements IMetroStationService {

    private final MetroStationMapper baseMapper;

    /**
     * 查询车站信息
     *
     * @param id 主键
     * @return 车站信息
     */
    @Override
    public MetroStationVo queryById(String id){
        return baseMapper.selectVoByIdWithRef(id);
    }

    /**
     * 分页查询车站信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 车站信息分页列表
     */
    @Override
    public TableDataInfo<MetroStationVo> queryPageList(MetroStationBo bo, PageQuery pageQuery) {
        Page<MetroStationVo> result = baseMapper.selectVoPageWithRef(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的车站信息列表
     *
     * @param bo 查询条件
     * @return 车站信息列表
     */
    @Override
    public List<MetroStationVo> queryList(MetroStationBo bo) {
        return baseMapper.selectVoListWithRef(bo);
    }

    /**
     * 新增车站信息
     *
     * @param bo 车站信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MetroStationBo bo) {
        MetroStation add = MapstructUtils.convert(bo, MetroStation.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改车站信息
     *
     * @param bo 车站信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MetroStationBo bo) {
        MetroStation update = MapstructUtils.convert(bo, MetroStation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MetroStation entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除车站信息信息
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
