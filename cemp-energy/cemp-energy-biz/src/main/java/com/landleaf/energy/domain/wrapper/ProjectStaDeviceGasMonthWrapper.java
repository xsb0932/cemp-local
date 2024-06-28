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

import com.landleaf.energy.domain.entity.ProjectStaDeviceGasMonthEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceGasMonthVO;

/**
 * 统计表-设备指标-气类-统计月的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceGasMonthWrapper extends BaseWrapper<ProjectStaDeviceGasMonthVO, ProjectStaDeviceGasMonthEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceGasMonthWrapper builder() {
		return new ProjectStaDeviceGasMonthWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceGasMonthVO entity2VO(ProjectStaDeviceGasMonthEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceGasMonthVO vo = new ProjectStaDeviceGasMonthVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceGasMonthVO> listEntity2VO(List<ProjectStaDeviceGasMonthEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceGasMonthVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceGasMonthVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceGasMonthVO> pageEntity2VO(IPage<ProjectStaDeviceGasMonthEntity> page) {
		PageDTO<ProjectStaDeviceGasMonthVO> pageVO = new PageDTO<ProjectStaDeviceGasMonthVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceGasMonthVO>());
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
	public PageDTO<ProjectStaDeviceGasMonthVO> pageEntity2VO(IPage<ProjectStaDeviceGasMonthEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}