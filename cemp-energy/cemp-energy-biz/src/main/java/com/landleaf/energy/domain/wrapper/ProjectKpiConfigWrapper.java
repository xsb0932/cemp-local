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

import com.landleaf.energy.domain.entity.ProjectKpiConfigEntity;
import com.landleaf.energy.domain.vo.ProjectKpiConfigVO;

/**
 * ProjectKpiConfigEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-27
 */
public class ProjectKpiConfigWrapper extends BaseWrapper<ProjectKpiConfigVO, ProjectKpiConfigEntity> {
	/**
	 * 构造
	 */
	public static ProjectKpiConfigWrapper builder() {
		return new ProjectKpiConfigWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectKpiConfigVO entity2VO(ProjectKpiConfigEntity e) {
		if (null == e) {
			return null;
		}
		ProjectKpiConfigVO vo = new ProjectKpiConfigVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectKpiConfigVO> listEntity2VO(List<ProjectKpiConfigEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectKpiConfigVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectKpiConfigVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectKpiConfigVO> pageEntity2VO(IPage<ProjectKpiConfigEntity> page) {
		PageDTO<ProjectKpiConfigVO> pageVO = new PageDTO<ProjectKpiConfigVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectKpiConfigVO>());
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
	public PageDTO<ProjectKpiConfigVO> pageEntity2VO(IPage<ProjectKpiConfigEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}