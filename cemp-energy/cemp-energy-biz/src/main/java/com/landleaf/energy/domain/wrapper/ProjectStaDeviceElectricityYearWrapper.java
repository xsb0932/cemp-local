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

import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityYearEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceElectricityYearVO;

/**
 * 统计表-设备指标-电表-统计年的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceElectricityYearWrapper extends BaseWrapper<ProjectStaDeviceElectricityYearVO, ProjectStaDeviceElectricityYearEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceElectricityYearWrapper builder() {
		return new ProjectStaDeviceElectricityYearWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceElectricityYearVO entity2VO(ProjectStaDeviceElectricityYearEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceElectricityYearVO vo = new ProjectStaDeviceElectricityYearVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceElectricityYearVO> listEntity2VO(List<ProjectStaDeviceElectricityYearEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceElectricityYearVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceElectricityYearVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceElectricityYearVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityYearEntity> page) {
		PageDTO<ProjectStaDeviceElectricityYearVO> pageVO = new PageDTO<ProjectStaDeviceElectricityYearVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceElectricityYearVO>());
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
	public PageDTO<ProjectStaDeviceElectricityYearVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityYearEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}