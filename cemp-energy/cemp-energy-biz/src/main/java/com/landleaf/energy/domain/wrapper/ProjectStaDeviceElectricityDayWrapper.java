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

import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityDayEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceElectricityDayVO;

/**
 * 统计表-设备指标-电表-统计天的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceElectricityDayWrapper extends BaseWrapper<ProjectStaDeviceElectricityDayVO, ProjectStaDeviceElectricityDayEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceElectricityDayWrapper builder() {
		return new ProjectStaDeviceElectricityDayWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceElectricityDayVO entity2VO(ProjectStaDeviceElectricityDayEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceElectricityDayVO vo = new ProjectStaDeviceElectricityDayVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceElectricityDayVO> listEntity2VO(List<ProjectStaDeviceElectricityDayEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceElectricityDayVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceElectricityDayVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceElectricityDayVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityDayEntity> page) {
		PageDTO<ProjectStaDeviceElectricityDayVO> pageVO = new PageDTO<ProjectStaDeviceElectricityDayVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceElectricityDayVO>());
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
	public PageDTO<ProjectStaDeviceElectricityDayVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityDayEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}