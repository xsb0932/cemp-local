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

import com.landleaf.energy.domain.entity.ProjectStaDeviceWaterYearEntity;
import com.landleaf.energy.domain.vo.ProjectStaDeviceWaterYearVO;

/**
 * 统计表-设备指标-水表-统计年的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaDeviceWaterYearWrapper extends BaseWrapper<ProjectStaDeviceWaterYearVO, ProjectStaDeviceWaterYearEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaDeviceWaterYearWrapper builder() {
		return new ProjectStaDeviceWaterYearWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaDeviceWaterYearVO entity2VO(ProjectStaDeviceWaterYearEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaDeviceWaterYearVO vo = new ProjectStaDeviceWaterYearVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaDeviceWaterYearVO> listEntity2VO(List<ProjectStaDeviceWaterYearEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaDeviceWaterYearVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaDeviceWaterYearVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaDeviceWaterYearVO> pageEntity2VO(IPage<ProjectStaDeviceWaterYearEntity> page) {
		PageDTO<ProjectStaDeviceWaterYearVO> pageVO = new PageDTO<ProjectStaDeviceWaterYearVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaDeviceWaterYearVO>());
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
	public PageDTO<ProjectStaDeviceWaterYearVO> pageEntity2VO(IPage<ProjectStaDeviceWaterYearEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}