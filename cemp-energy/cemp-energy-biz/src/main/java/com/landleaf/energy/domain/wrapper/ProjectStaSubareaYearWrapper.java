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

import com.landleaf.energy.domain.entity.ProjectStaSubareaYearEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubareaYearVO;

/**
 * ProjectStaSubareaYearEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubareaYearWrapper extends BaseWrapper<ProjectStaSubareaYearVO, ProjectStaSubareaYearEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubareaYearWrapper builder() {
		return new ProjectStaSubareaYearWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubareaYearVO entity2VO(ProjectStaSubareaYearEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubareaYearVO vo = new ProjectStaSubareaYearVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubareaYearVO> listEntity2VO(List<ProjectStaSubareaYearEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubareaYearVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubareaYearVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubareaYearVO> pageEntity2VO(IPage<ProjectStaSubareaYearEntity> page) {
		PageDTO<ProjectStaSubareaYearVO> pageVO = new PageDTO<ProjectStaSubareaYearVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubareaYearVO>());
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
	public PageDTO<ProjectStaSubareaYearVO> pageEntity2VO(IPage<ProjectStaSubareaYearEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}