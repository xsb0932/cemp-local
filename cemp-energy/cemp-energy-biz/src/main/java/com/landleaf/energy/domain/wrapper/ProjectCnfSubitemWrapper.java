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

import com.landleaf.energy.domain.entity.ProjectCnfSubitemEntity;
import com.landleaf.energy.domain.vo.ProjectCnfSubitemVO;

/**
 * ProjectCnfSubitemEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectCnfSubitemWrapper extends BaseWrapper<ProjectCnfSubitemVO, ProjectCnfSubitemEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfSubitemWrapper builder() {
		return new ProjectCnfSubitemWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfSubitemVO entity2VO(ProjectCnfSubitemEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfSubitemVO vo = new ProjectCnfSubitemVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfSubitemVO> listEntity2VO(List<ProjectCnfSubitemEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfSubitemVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfSubitemVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfSubitemVO> pageEntity2VO(IPage<ProjectCnfSubitemEntity> page) {
		PageDTO<ProjectCnfSubitemVO> pageVO = new PageDTO<ProjectCnfSubitemVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfSubitemVO>());
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
	public PageDTO<ProjectCnfSubitemVO> pageEntity2VO(IPage<ProjectCnfSubitemEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}