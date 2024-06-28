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

import com.landleaf.energy.domain.entity.ProjectCnfWaterFeeEntity;
import com.landleaf.energy.domain.vo.ProjectCnfWaterFeeVO;

/**
 * 用水费用配置表的展示类型转化工具
 *
 * @author hebin
 * @since 2023-07-04
 */
public class ProjectCnfWaterFeeWrapper extends BaseWrapper<ProjectCnfWaterFeeVO, ProjectCnfWaterFeeEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfWaterFeeWrapper builder() {
		return new ProjectCnfWaterFeeWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfWaterFeeVO entity2VO(ProjectCnfWaterFeeEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfWaterFeeVO vo = new ProjectCnfWaterFeeVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfWaterFeeVO> listEntity2VO(List<ProjectCnfWaterFeeEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfWaterFeeVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfWaterFeeVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfWaterFeeVO> pageEntity2VO(IPage<ProjectCnfWaterFeeEntity> page) {
		PageDTO<ProjectCnfWaterFeeVO> pageVO = new PageDTO<ProjectCnfWaterFeeVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfWaterFeeVO>());
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
	public PageDTO<ProjectCnfWaterFeeVO> pageEntity2VO(IPage<ProjectCnfWaterFeeEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}