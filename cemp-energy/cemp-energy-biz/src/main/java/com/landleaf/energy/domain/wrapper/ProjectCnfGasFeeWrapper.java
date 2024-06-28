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

import com.landleaf.energy.domain.entity.ProjectCnfGasFeeEntity;
import com.landleaf.energy.domain.vo.ProjectCnfGasFeeVO;

/**
 * 燃气费用配置表的展示类型转化工具
 *
 * @author hebin
 * @since 2023-07-04
 */
public class ProjectCnfGasFeeWrapper extends BaseWrapper<ProjectCnfGasFeeVO, ProjectCnfGasFeeEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfGasFeeWrapper builder() {
		return new ProjectCnfGasFeeWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfGasFeeVO entity2VO(ProjectCnfGasFeeEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfGasFeeVO vo = new ProjectCnfGasFeeVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfGasFeeVO> listEntity2VO(List<ProjectCnfGasFeeEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfGasFeeVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfGasFeeVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfGasFeeVO> pageEntity2VO(IPage<ProjectCnfGasFeeEntity> page) {
		PageDTO<ProjectCnfGasFeeVO> pageVO = new PageDTO<ProjectCnfGasFeeVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfGasFeeVO>());
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
	public PageDTO<ProjectCnfGasFeeVO> pageEntity2VO(IPage<ProjectCnfGasFeeEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}