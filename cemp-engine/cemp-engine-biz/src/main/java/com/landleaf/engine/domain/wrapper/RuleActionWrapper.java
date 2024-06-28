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

import com.landleaf.engine.domain.entity.RuleActionEntity;
import com.landleaf.engine.domain.vo.RuleActionVO;

/**
 * RuleActionEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2024-04-23
 */
public class RuleActionWrapper extends BaseWrapper<RuleActionVO, RuleActionEntity> {
	/**
	 * 构造
	 */
	public static RuleActionWrapper builder() {
		return new RuleActionWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuleActionVO entity2VO(RuleActionEntity e) {
		if (null == e) {
			return null;
		}
		RuleActionVO vo = new RuleActionVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleActionVO> listEntity2VO(List<RuleActionEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, RuleActionVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<RuleActionVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<RuleActionVO> pageEntity2VO(IPage<RuleActionEntity> page) {
		PageDTO<RuleActionVO> pageVO = new PageDTO<RuleActionVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<RuleActionVO>());
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
	public PageDTO<RuleActionVO> pageEntity2VO(IPage<RuleActionEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}