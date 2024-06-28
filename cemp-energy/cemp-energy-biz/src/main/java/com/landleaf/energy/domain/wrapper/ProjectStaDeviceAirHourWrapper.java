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

import com.landleaf.energy.domain.entity.ProjectStaDeviceAirHourEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceAirHourVO;

/**
 * 统计表-设备指标-空调-统计小时的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceAirHourWrapper extends BaseWrapper<ProjectStaDeviceAirHourVO, ProjectStaDeviceAirHourEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceAirHourWrapper builder() {
		return new ProjectStaDeviceAirHourWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceAirHourVO entity2VO(ProjectStaDeviceAirHourEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceAirHourVO vo = new ProjectStaDeviceAirHourVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceAirHourVO> listEntity2VO(List<ProjectStaDeviceAirHourEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceAirHourVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceAirHourVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceAirHourVO> pageEntity2VO(IPage<ProjectStaDeviceAirHourEntity> page) {
		PageDTO<ProjectStaDeviceAirHourVO> pageVO = new PageDTO<ProjectStaDeviceAirHourVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceAirHourVO>());
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
	public PageDTO<ProjectStaDeviceAirHourVO> pageEntity2VO(IPage<ProjectStaDeviceAirHourEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}