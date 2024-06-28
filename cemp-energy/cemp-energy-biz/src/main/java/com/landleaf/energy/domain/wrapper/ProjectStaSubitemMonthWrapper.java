package com.landleaf.energy.domain.wrapper;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.pgsql.base.wrapper.BaseWrapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.landleaf.energy.domain.entity.ProjectStaSubitemMonthEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubitemMonthVO;

/**
 * ProjectStaSubitemMonthEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubitemMonthWrapper extends BaseWrapper<ProjectStaSubitemMonthVO, ProjectStaSubitemMonthEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubitemMonthWrapper builder() {
		return new ProjectStaSubitemMonthWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubitemMonthVO entity2VO(ProjectStaSubitemMonthEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubitemMonthVO vo = new ProjectStaSubitemMonthVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubitemMonthVO> listEntity2VO(List<ProjectStaSubitemMonthEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubitemMonthVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubitemMonthVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubitemMonthVO> pageEntity2VO(IPage<ProjectStaSubitemMonthEntity> page) {
		PageDTO<ProjectStaSubitemMonthVO> pageVO = new PageDTO<ProjectStaSubitemMonthVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubitemMonthVO>());
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
	public PageDTO<ProjectStaSubitemMonthVO> pageEntity2VO(IPage<ProjectStaSubitemMonthEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}