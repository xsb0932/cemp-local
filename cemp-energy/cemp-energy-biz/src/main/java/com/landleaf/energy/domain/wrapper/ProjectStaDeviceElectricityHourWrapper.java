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

import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityHourEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceElectricityHourVO;

/**
 * 统计表-设备指标-电表-统计小时的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceElectricityHourWrapper extends BaseWrapper<ProjectStaDeviceElectricityHourVO, ProjectStaDeviceElectricityHourEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceElectricityHourWrapper builder() {
		return new ProjectStaDeviceElectricityHourWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceElectricityHourVO entity2VO(ProjectStaDeviceElectricityHourEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceElectricityHourVO vo = new ProjectStaDeviceElectricityHourVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceElectricityHourVO> listEntity2VO(List<ProjectStaDeviceElectricityHourEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceElectricityHourVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceElectricityHourVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceElectricityHourVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityHourEntity> page) {
		PageDTO<ProjectStaDeviceElectricityHourVO> pageVO = new PageDTO<ProjectStaDeviceElectricityHourVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceElectricityHourVO>());
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
	public PageDTO<ProjectStaDeviceElectricityHourVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityHourEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}