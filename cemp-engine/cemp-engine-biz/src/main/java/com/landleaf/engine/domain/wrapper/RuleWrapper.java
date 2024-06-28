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

import com.landleaf.engine.domain.entity.RuleEntity;
import com.landleaf.engine.domain.vo.RuleVO;

/**
 * RuleEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2024-04-23
 */
public class RuleWrapper extends BaseWrapper<RuleVO, RuleEntity> {
	/**
	 * 构造
	 */
	public static RuleWrapper builder() {
		return new RuleWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuleVO entity2VO(RuleEntity e) {
		if (null == e) {
			return null;
		}
		RuleVO vo = new RuleVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleVO> listEntity2VO(List<RuleEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, RuleVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<RuleVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<RuleVO> pageEntity2VO(IPage<RuleEntity> page) {
		PageDTO<RuleVO> pageVO = new PageDTO<RuleVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<RuleVO>());
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
	public PageDTO<RuleVO> pageEntity2VO(IPage<RuleEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}