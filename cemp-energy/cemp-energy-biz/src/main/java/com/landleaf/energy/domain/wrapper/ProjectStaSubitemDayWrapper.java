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

import com.landleaf.energy.domain.entity.ProjectStaSubitemDayEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubitemDayVO;

/**
 * ProjectStaSubitemDayEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubitemDayWrapper extends BaseWrapper<ProjectStaSubitemDayVO, ProjectStaSubitemDayEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubitemDayWrapper builder() {
		return new ProjectStaSubitemDayWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubitemDayVO entity2VO(ProjectStaSubitemDayEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubitemDayVO vo = new ProjectStaSubitemDayVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubitemDayVO> listEntity2VO(List<ProjectStaSubitemDayEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubitemDayVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubitemDayVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubitemDayVO> pageEntity2VO(IPage<ProjectStaSubitemDayEntity> page) {
		PageDTO<ProjectStaSubitemDayVO> pageVO = new PageDTO<ProjectStaSubitemDayVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubitemDayVO>());
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
	public PageDTO<ProjectStaSubitemDayVO> pageEntity2VO(IPage<ProjectStaSubitemDayEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}