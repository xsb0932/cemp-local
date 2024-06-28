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

import com.landleaf.energy.domain.entity.ProjectStaSubareaMonthEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubareaMonthVO;

/**
 * ProjectStaSubareaMonthEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubareaMonthWrapper extends BaseWrapper<ProjectStaSubareaMonthVO, ProjectStaSubareaMonthEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubareaMonthWrapper builder() {
		return new ProjectStaSubareaMonthWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubareaMonthVO entity2VO(ProjectStaSubareaMonthEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubareaMonthVO vo = new ProjectStaSubareaMonthVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubareaMonthVO> listEntity2VO(List<ProjectStaSubareaMonthEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubareaMonthVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubareaMonthVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubareaMonthVO> pageEntity2VO(IPage<ProjectStaSubareaMonthEntity> page) {
		PageDTO<ProjectStaSubareaMonthVO> pageVO = new PageDTO<ProjectStaSubareaMonthVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubareaMonthVO>());
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
	public PageDTO<ProjectStaSubareaMonthVO> pageEntity2VO(IPage<ProjectStaSubareaMonthEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}