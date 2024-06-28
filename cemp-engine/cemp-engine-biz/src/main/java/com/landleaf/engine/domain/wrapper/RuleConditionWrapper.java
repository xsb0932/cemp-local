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

import com.landleaf.engine.domain.entity.RuleConditionEntity;
import com.landleaf.engine.domain.vo.RuleConditionVO;

/**
 * RuleConditionEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2024-04-23
 */
public class RuleConditionWrapper extends BaseWrapper<RuleConditionVO, RuleConditionEntity> {
	/**
	 * 构造
	 */
	public static RuleConditionWrapper builder() {
		return new RuleConditionWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuleConditionVO entity2VO(RuleConditionEntity e) {
		if (null == e) {
			return null;
		}
		RuleConditionVO vo = new RuleConditionVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleConditionVO> listEntity2VO(List<RuleConditionEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, RuleConditionVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<RuleConditionVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<RuleConditionVO> pageEntity2VO(IPage<RuleConditionEntity> page) {
		PageDTO<RuleConditionVO> pageVO = new PageDTO<RuleConditionVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<RuleConditionVO>());
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
	public PageDTO<RuleConditionVO> pageEntity2VO(IPage<RuleConditionEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}