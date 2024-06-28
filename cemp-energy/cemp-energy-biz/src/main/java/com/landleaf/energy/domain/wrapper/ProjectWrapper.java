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

import com.landleaf.energy.domain.entity.ProjectEntity;
import com.landleaf.energy.domain.vo.ProjectVO;

/**
 * 项目的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectWrapper extends BaseWrapper<ProjectVO, ProjectEntity> {
	/**
	 * 构造
	 */
	public static ProjectWrapper builder() {
		return new ProjectWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectVO entity2VO(ProjectEntity e) {
		if (null == e) {
			return null;
		}
		ProjectVO vo = new ProjectVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectVO> listEntity2VO(List<ProjectEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectVO> pageEntity2VO(IPage<ProjectEntity> page) {
		PageDTO<ProjectVO> pageVO = new PageDTO<ProjectVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectVO>());
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
	public PageDTO<ProjectVO> pageEntity2VO(IPage<ProjectEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}