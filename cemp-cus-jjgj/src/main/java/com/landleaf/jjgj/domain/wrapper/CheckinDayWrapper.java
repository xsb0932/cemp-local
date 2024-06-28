package com.landleaf.jjgj.domain.wrapper;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.jjgj.domain.entity.CheckinDayEntity;
import com.landleaf.jjgj.domain.vo.CheckinDayVO;
import com.landleaf.pgsql.base.wrapper.BaseWrapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JjgjCheckinDayEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-10-16
 */
public class CheckinDayWrapper extends BaseWrapper<CheckinDayVO, CheckinDayEntity> {
    /**
     * 构造
     */
    public static CheckinDayWrapper builder() {
        return new CheckinDayWrapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckinDayVO entity2VO(CheckinDayEntity e) {
        if (null == e) {
            return null;
        }
        CheckinDayVO vo = new CheckinDayVO();
        BeanUtil.copyProperties(e, vo);
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CheckinDayVO> listEntity2VO(List<CheckinDayEntity> eList) {
        if (!CollectionUtils.isEmpty(eList)) {
            return eList.stream().map(i -> BeanUtil.copyProperties(i, CheckinDayVO.class))
                    .collect(Collectors.toList());
        }
        return new ArrayList<CheckinDayVO>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<CheckinDayVO> pageEntity2VO(IPage<CheckinDayEntity> page) {
        PageDTO<CheckinDayVO> pageVO = new PageDTO<CheckinDayVO>();
        if (null == page) {
            pageVO.setCurrent(0);
            pageVO.setTotal(0);
            pageVO.setPages(1);
            pageVO.setRecords(new ArrayList<CheckinDayVO>());
            return pageVO;
        }
        pageVO.setCurrent(page.getCurrent());
        pageVO.setTotal(page.getTotal());
        pageVO.setPages(page.getPages());
        pageVO.setRecords(listEntity2VO(page.getRecords()));
        return pageVO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<CheckinDayVO> pageEntity2VO(IPage<CheckinDayEntity> page, Map<?, ?> map) {
        return pageEntity2VO(page);
    }
}