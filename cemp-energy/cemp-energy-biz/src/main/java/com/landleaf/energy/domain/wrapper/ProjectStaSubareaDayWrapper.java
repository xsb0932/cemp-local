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

import com.landleaf.energy.domain.entity.ProjectStaSubareaDayEntity;
import com.landleaf.energy.domain.vo.ProjectStaSubareaDayVO;

/**
 * ProjectStaSubareaDayEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectStaSubareaDayWrapper extends BaseWrapper<ProjectStaSubareaDayVO, ProjectStaSubareaDayEntity> {
	/**
	 * 构造
	 */
	public static ProjectStaSubareaDayWrapper builder() {
		return new ProjectStaSubareaDayWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectStaSubareaDayVO entity2VO(ProjectStaSubareaDayEntity e) {
		if (null == e) {
			return null;
		}
		ProjectStaSubareaDayVO vo = new ProjectStaSubareaDayVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectStaSubareaDayVO> listEntity2VO(List<ProjectStaSubareaDayEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectStaSubareaDayVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectStaSubareaDayVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectStaSubareaDayVO> pageEntity2VO(IPage<ProjectStaSubareaDayEntity> page) {
		PageDTO<ProjectStaSubareaDayVO> pageVO = new PageDTO<ProjectStaSubareaDayVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectStaSubareaDayVO>());
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
	public PageDTO<ProjectStaSubareaDayVO> pageEntity2VO(IPage<ProjectStaSubareaDayEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}