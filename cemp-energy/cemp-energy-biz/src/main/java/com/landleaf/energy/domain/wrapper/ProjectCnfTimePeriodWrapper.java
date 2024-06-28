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

import com.landleaf.energy.domain.entity.ProjectCnfTimePeriodEntity;
import com.landleaf.energy.domain.vo.ProjectCnfTimePeriodVO;

/**
 * ProjectCnfTimePeriodEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectCnfTimePeriodWrapper extends BaseWrapper<ProjectCnfTimePeriodVO, ProjectCnfTimePeriodEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfTimePeriodWrapper builder() {
		return new ProjectCnfTimePeriodWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfTimePeriodVO entity2VO(ProjectCnfTimePeriodEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfTimePeriodVO vo = new ProjectCnfTimePeriodVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfTimePeriodVO> listEntity2VO(List<ProjectCnfTimePeriodEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfTimePeriodVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfTimePeriodVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfTimePeriodVO> pageEntity2VO(IPage<ProjectCnfTimePeriodEntity> page) {
		PageDTO<ProjectCnfTimePeriodVO> pageVO = new PageDTO<ProjectCnfTimePeriodVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfTimePeriodVO>());
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
	public PageDTO<ProjectCnfTimePeriodVO> pageEntity2VO(IPage<ProjectCnfTimePeriodEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}