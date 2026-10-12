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
import org.dromara.model.domain.bo.MetroLineBo;
import org.dromara.model.domain.vo.MetroLineVo;
import org.dromara.model.domain.MetroLine;
import org.dromara.model.mapper.MetroLineMapper;
import org.dromara.model.service.IMetroLineService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 线路信息Service业务层处理
 *
 * @author Lion Li
 * @date 2026-10-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MetroLineServiceImpl implements IMetroLineService {

    private final MetroLineMapper baseMapper;

    /**
     * 查询线路信息
     *
     * @param id 主键
     * @return 线路信息
     */
    @Override
    public MetroLineVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询线路信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 线路信息分页列表
     */
    @Override
    public TableDataInfo<MetroLineVo> queryPageList(MetroLineBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MetroLine> lqw = buildQueryWrapper(bo);
        Page<MetroLineVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的线路信息列表
     *
     * @param bo 查询条件
     * @return 线路信息列表
     */
    @Override
    public List<MetroLineVo> queryList(MetroLineBo bo) {
        LambdaQueryWrapper<MetroLine> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MetroLine> buildQueryWrapper(MetroLineBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MetroLine> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MetroLine::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), MetroLine::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), MetroLine::getCode, bo.getCode());
        lqw.eq(StringUtils.isNotBlank(bo.getCompanyId()), MetroLine::getCompanyId, bo.getCompanyId());
        lqw.eq(bo.getSortOrder() != null, MetroLine::getSortOrder, bo.getSortOrder());
        return lqw;
    }

    /**
     * 新增线路信息
     *
     * @param bo 线路信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MetroLineBo bo) {
        MetroLine add = MapstructUtils.convert(bo, MetroLine.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改线路信息
     *
     * @param bo 线路信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MetroLineBo bo) {
        MetroLine update = MapstructUtils.convert(bo, MetroLine.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MetroLine entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除线路信息信息
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
