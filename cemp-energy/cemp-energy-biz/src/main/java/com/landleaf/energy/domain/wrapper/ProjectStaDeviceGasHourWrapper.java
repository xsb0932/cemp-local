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

import com.landleaf.energy.domain.entity.ProjectStaDeviceGasHourEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceGasHourVO;

/**
 * 统计表-设备指标-气类-统计小时的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceGasHourWrapper extends BaseWrapper<ProjectStaDeviceGasHourVO, ProjectStaDeviceGasHourEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceGasHourWrapper builder() {
		return new ProjectStaDeviceGasHourWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceGasHourVO entity2VO(ProjectStaDeviceGasHourEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceGasHourVO vo = new ProjectStaDeviceGasHourVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceGasHourVO> listEntity2VO(List<ProjectStaDeviceGasHourEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceGasHourVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceGasHourVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceGasHourVO> pageEntity2VO(IPage<ProjectStaDeviceGasHourEntity> page) {
		PageDTO<ProjectStaDeviceGasHourVO> pageVO = new PageDTO<ProjectStaDeviceGasHourVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceGasHourVO>());
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
	public PageDTO<ProjectStaDeviceGasHourVO> pageEntity2VO(IPage<ProjectStaDeviceGasHourEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}