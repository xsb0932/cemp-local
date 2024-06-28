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

import com.landleaf.energy.domain.entity.ProjectStaSubitemHourEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubitemHourVO;

/**
 * ProjectStaSubitemHourEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubitemHourWrapper extends BaseWrapper<ProjectStaSubitemHourVO, ProjectStaSubitemHourEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubitemHourWrapper builder() {
		return new ProjectStaSubitemHourWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubitemHourVO entity2VO(ProjectStaSubitemHourEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubitemHourVO vo = new ProjectStaSubitemHourVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubitemHourVO> listEntity2VO(List<ProjectStaSubitemHourEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubitemHourVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubitemHourVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubitemHourVO> pageEntity2VO(IPage<ProjectStaSubitemHourEntity> page) {
		PageDTO<ProjectStaSubitemHourVO> pageVO = new PageDTO<ProjectStaSubitemHourVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubitemHourVO>());
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
	public PageDTO<ProjectStaSubitemHourVO> pageEntity2VO(IPage<ProjectStaSubitemHourEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}