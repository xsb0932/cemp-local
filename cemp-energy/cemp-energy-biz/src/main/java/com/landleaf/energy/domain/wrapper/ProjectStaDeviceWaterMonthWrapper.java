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

import com.landleaf.energy.domain.entity.ProjectStaDeviceWaterMonthEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceWaterMonthVO;

/**
 * 统计表-设备指标-水表-统计月的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceWaterMonthWrapper extends BaseWrapper<ProjectStaDeviceWaterMonthVO, ProjectStaDeviceWaterMonthEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceWaterMonthWrapper builder() {
		return new ProjectStaDeviceWaterMonthWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceWaterMonthVO entity2VO(ProjectStaDeviceWaterMonthEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceWaterMonthVO vo = new ProjectStaDeviceWaterMonthVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceWaterMonthVO> listEntity2VO(List<ProjectStaDeviceWaterMonthEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceWaterMonthVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceWaterMonthVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceWaterMonthVO> pageEntity2VO(IPage<ProjectStaDeviceWaterMonthEntity> page) {
		PageDTO<ProjectStaDeviceWaterMonthVO> pageVO = new PageDTO<ProjectStaDeviceWaterMonthVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceWaterMonthVO>());
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
	public PageDTO<ProjectStaDeviceWaterMonthVO> pageEntity2VO(IPage<ProjectStaDeviceWaterMonthEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}