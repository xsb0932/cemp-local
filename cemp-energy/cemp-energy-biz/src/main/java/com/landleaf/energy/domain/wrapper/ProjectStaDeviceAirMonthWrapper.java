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

import com.landleaf.energy.domain.entity.ProjectStaDeviceAirMonthEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceAirMonthVO;

/**
 * 统计表-设备指标-空调-统计月的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceAirMonthWrapper extends BaseWrapper<ProjectStaDeviceAirMonthVO, ProjectStaDeviceAirMonthEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceAirMonthWrapper builder() {
		return new ProjectStaDeviceAirMonthWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceAirMonthVO entity2VO(ProjectStaDeviceAirMonthEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceAirMonthVO vo = new ProjectStaDeviceAirMonthVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceAirMonthVO> listEntity2VO(List<ProjectStaDeviceAirMonthEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceAirMonthVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceAirMonthVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceAirMonthVO> pageEntity2VO(IPage<ProjectStaDeviceAirMonthEntity> page) {
		PageDTO<ProjectStaDeviceAirMonthVO> pageVO = new PageDTO<ProjectStaDeviceAirMonthVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceAirMonthVO>());
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
	public PageDTO<ProjectStaDeviceAirMonthVO> pageEntity2VO(IPage<ProjectStaDeviceAirMonthEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}