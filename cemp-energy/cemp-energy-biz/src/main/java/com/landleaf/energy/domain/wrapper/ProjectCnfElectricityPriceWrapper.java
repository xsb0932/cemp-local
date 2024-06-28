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

import com.landleaf.energy.domain.entity.ProjectCnfElectricityPriceEntity;
import com.landleaf.energy.domain.vo.ProjectCnfElectricityPriceVO;

/**
 * 电费配置表的展示类型转化工具
 *
 * @author hebin
 * @since 2024-03-20
 */
public class ProjectCnfElectricityPriceWrapper extends BaseWrapper<ProjectCnfElectricityPriceVO, ProjectCnfElectricityPriceEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfElectricityPriceWrapper builder() {
		return new ProjectCnfElectricityPriceWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfElectricityPriceVO entity2VO(ProjectCnfElectricityPriceEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfElectricityPriceVO vo = new ProjectCnfElectricityPriceVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfElectricityPriceVO> listEntity2VO(List<ProjectCnfElectricityPriceEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfElectricityPriceVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfElectricityPriceVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfElectricityPriceVO> pageEntity2VO(IPage<ProjectCnfElectricityPriceEntity> page) {
		PageDTO<ProjectCnfElectricityPriceVO> pageVO = new PageDTO<ProjectCnfElectricityPriceVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfElectricityPriceVO>());
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
	public PageDTO<ProjectCnfElectricityPriceVO> pageEntity2VO(IPage<ProjectCnfElectricityPriceEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}