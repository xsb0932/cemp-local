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

import com.landleaf.energy.domain.entity.ProjectStaDeviceAirYearEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceAirYearVO;

/**
 * 统计表-设备指标-空调-统计年的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceAirYearWrapper extends BaseWrapper<ProjectStaDeviceAirYearVO, ProjectStaDeviceAirYearEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceAirYearWrapper builder() {
		return new ProjectStaDeviceAirYearWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceAirYearVO entity2VO(ProjectStaDeviceAirYearEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceAirYearVO vo = new ProjectStaDeviceAirYearVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceAirYearVO> listEntity2VO(List<ProjectStaDeviceAirYearEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceAirYearVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceAirYearVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceAirYearVO> pageEntity2VO(IPage<ProjectStaDeviceAirYearEntity> page) {
		PageDTO<ProjectStaDeviceAirYearVO> pageVO = new PageDTO<ProjectStaDeviceAirYearVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceAirYearVO>());
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
	public PageDTO<ProjectStaDeviceAirYearVO> pageEntity2VO(IPage<ProjectStaDeviceAirYearEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}