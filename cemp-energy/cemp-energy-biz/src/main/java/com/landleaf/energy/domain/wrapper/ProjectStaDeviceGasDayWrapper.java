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

import com.landleaf.energy.domain.entity.ProjectStaDeviceGasDayEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceGasDayVO;

/**
 * 统计表-设备指标-气类-统计天的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceGasDayWrapper extends BaseWrapper<ProjectStaDeviceGasDayVO, ProjectStaDeviceGasDayEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceGasDayWrapper builder() {
		return new ProjectStaDeviceGasDayWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceGasDayVO entity2VO(ProjectStaDeviceGasDayEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceGasDayVO vo = new ProjectStaDeviceGasDayVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceGasDayVO> listEntity2VO(List<ProjectStaDeviceGasDayEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceGasDayVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceGasDayVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceGasDayVO> pageEntity2VO(IPage<ProjectStaDeviceGasDayEntity> page) {
		PageDTO<ProjectStaDeviceGasDayVO> pageVO = new PageDTO<ProjectStaDeviceGasDayVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceGasDayVO>());
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
	public PageDTO<ProjectStaDeviceGasDayVO> pageEntity2VO(IPage<ProjectStaDeviceGasDayEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}