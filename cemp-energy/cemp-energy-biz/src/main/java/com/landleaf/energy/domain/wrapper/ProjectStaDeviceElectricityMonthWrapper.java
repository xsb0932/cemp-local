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

import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityMonthEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceElectricityMonthVO;

/**
 * 统计表-设备指标-电表-统计月的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceElectricityMonthWrapper extends BaseWrapper<ProjectStaDeviceElectricityMonthVO, ProjectStaDeviceElectricityMonthEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceElectricityMonthWrapper builder() {
		return new ProjectStaDeviceElectricityMonthWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceElectricityMonthVO entity2VO(ProjectStaDeviceElectricityMonthEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceElectricityMonthVO vo = new ProjectStaDeviceElectricityMonthVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceElectricityMonthVO> listEntity2VO(List<ProjectStaDeviceElectricityMonthEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceElectricityMonthVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceElectricityMonthVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceElectricityMonthVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityMonthEntity> page) {
		PageDTO<ProjectStaDeviceElectricityMonthVO> pageVO = new PageDTO<ProjectStaDeviceElectricityMonthVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceElectricityMonthVO>());
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
	public PageDTO<ProjectStaDeviceElectricityMonthVO> pageEntity2VO(IPage<ProjectStaDeviceElectricityMonthEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}