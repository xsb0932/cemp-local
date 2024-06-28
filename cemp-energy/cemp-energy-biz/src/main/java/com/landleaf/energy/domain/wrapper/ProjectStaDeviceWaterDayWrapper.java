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

import com.landleaf.energy.domain.entity.ProjectStaDeviceWaterDayEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceWaterDayVO;

/**
 * 统计表-设备指标-水表-统计天的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceWaterDayWrapper extends BaseWrapper<ProjectStaDeviceWaterDayVO, ProjectStaDeviceWaterDayEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceWaterDayWrapper builder() {
		return new ProjectStaDeviceWaterDayWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceWaterDayVO entity2VO(ProjectStaDeviceWaterDayEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceWaterDayVO vo = new ProjectStaDeviceWaterDayVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceWaterDayVO> listEntity2VO(List<ProjectStaDeviceWaterDayEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceWaterDayVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceWaterDayVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceWaterDayVO> pageEntity2VO(IPage<ProjectStaDeviceWaterDayEntity> page) {
		PageDTO<ProjectStaDeviceWaterDayVO> pageVO = new PageDTO<ProjectStaDeviceWaterDayVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceWaterDayVO>());
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
	public PageDTO<ProjectStaDeviceWaterDayVO> pageEntity2VO(IPage<ProjectStaDeviceWaterDayEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}