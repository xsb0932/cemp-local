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

import com.landleaf.energy.domain.entity.ProjectStaSubareaHourEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubareaHourVO;

/**
 * ProjectStaSubareaHourEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubareaHourWrapper extends BaseWrapper<ProjectStaSubareaHourVO, ProjectStaSubareaHourEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubareaHourWrapper builder() {
		return new ProjectStaSubareaHourWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubareaHourVO entity2VO(ProjectStaSubareaHourEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubareaHourVO vo = new ProjectStaSubareaHourVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubareaHourVO> listEntity2VO(List<ProjectStaSubareaHourEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubareaHourVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubareaHourVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubareaHourVO> pageEntity2VO(IPage<ProjectStaSubareaHourEntity> page) {
		PageDTO<ProjectStaSubareaHourVO> pageVO = new PageDTO<ProjectStaSubareaHourVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubareaHourVO>());
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
	public PageDTO<ProjectStaSubareaHourVO> pageEntity2VO(IPage<ProjectStaSubareaHourEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}