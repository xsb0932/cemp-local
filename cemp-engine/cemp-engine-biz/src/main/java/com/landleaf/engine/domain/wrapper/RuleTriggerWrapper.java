package com.landleaf.engine.domain.wrapper;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.pgsql.base.wrapper.BaseWrapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.landleaf.engine.domain.entity.RuleTriggerEntity;
import com.landleaf.engine.domain.vo.RuleTriggerVO;

/**
 * RuleTriggerEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2024-04-23
 */
public class RuleTriggerWrapper extends BaseWrapper<RuleTriggerVO, RuleTriggerEntity> {
	/**
	 * 构造
	 */
	public static RuleTriggerWrapper builder() {
		return new RuleTriggerWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuleTriggerVO entity2VO(RuleTriggerEntity e) {
		if (null == e) {
			return null;
		}
		RuleTriggerVO vo = new RuleTriggerVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleTriggerVO> listEntity2VO(List<RuleTriggerEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, RuleTriggerVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<RuleTriggerVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<RuleTriggerVO> pageEntity2VO(IPage<RuleTriggerEntity> page) {
		PageDTO<RuleTriggerVO> pageVO = new PageDTO<RuleTriggerVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<RuleTriggerVO>());
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
	public PageDTO<RuleTriggerVO> pageEntity2VO(IPage<RuleTriggerEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}