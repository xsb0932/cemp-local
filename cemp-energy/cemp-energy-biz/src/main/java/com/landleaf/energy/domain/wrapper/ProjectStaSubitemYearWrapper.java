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

import com.landleaf.energy.domain.entity.ProjectStaSubitemYearEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubitemYearVO;

/**
 * ProjectStaSubitemYearEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubitemYearWrapper extends BaseWrapper<ProjectStaSubitemYearVO, ProjectStaSubitemYearEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubitemYearWrapper builder() {
		return new ProjectStaSubitemYearWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubitemYearVO entity2VO(ProjectStaSubitemYearEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubitemYearVO vo = new ProjectStaSubitemYearVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubitemYearVO> listEntity2VO(List<ProjectStaSubitemYearEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubitemYearVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubitemYearVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubitemYearVO> pageEntity2VO(IPage<ProjectStaSubitemYearEntity> page) {
		PageDTO<ProjectStaSubitemYearVO> pageVO = new PageDTO<ProjectStaSubitemYearVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubitemYearVO>());
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
	public PageDTO<ProjectStaSubitemYearVO> pageEntity2VO(IPage<ProjectStaSubitemYearEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}