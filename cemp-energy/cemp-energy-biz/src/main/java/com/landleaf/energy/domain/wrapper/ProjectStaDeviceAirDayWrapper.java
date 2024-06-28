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

import com.landleaf.energy.domain.entity.ProjectStaDeviceAirDayEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceAirDayVO;

/**
 * 统计表-设备指标-空调-统计天的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceAirDayWrapper extends BaseWrapper<ProjectStaDeviceAirDayVO, ProjectStaDeviceAirDayEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceAirDayWrapper builder() {
		return new ProjectStaDeviceAirDayWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceAirDayVO entity2VO(ProjectStaDeviceAirDayEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceAirDayVO vo = new ProjectStaDeviceAirDayVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceAirDayVO> listEntity2VO(List<ProjectStaDeviceAirDayEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceAirDayVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceAirDayVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceAirDayVO> pageEntity2VO(IPage<ProjectStaDeviceAirDayEntity> page) {
		PageDTO<ProjectStaDeviceAirDayVO> pageVO = new PageDTO<ProjectStaDeviceAirDayVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceAirDayVO>());
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
	public PageDTO<ProjectStaDeviceAirDayVO> pageEntity2VO(IPage<ProjectStaDeviceAirDayEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}