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

import com.landleaf.energy.domain.entity.ProjectStaDeviceGasYearEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceGasYearVO;

/**
 * 统计表-设备指标-气类-统计年的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceGasYearWrapper extends BaseWrapper<ProjectStaDeviceGasYearVO, ProjectStaDeviceGasYearEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceGasYearWrapper builder() {
		return new ProjectStaDeviceGasYearWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceGasYearVO entity2VO(ProjectStaDeviceGasYearEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceGasYearVO vo = new ProjectStaDeviceGasYearVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceGasYearVO> listEntity2VO(List<ProjectStaDeviceGasYearEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceGasYearVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceGasYearVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceGasYearVO> pageEntity2VO(IPage<ProjectStaDeviceGasYearEntity> page) {
		PageDTO<ProjectStaDeviceGasYearVO> pageVO = new PageDTO<ProjectStaDeviceGasYearVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceGasYearVO>());
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
	public PageDTO<ProjectStaDeviceGasYearVO> pageEntity2VO(IPage<ProjectStaDeviceGasYearEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}