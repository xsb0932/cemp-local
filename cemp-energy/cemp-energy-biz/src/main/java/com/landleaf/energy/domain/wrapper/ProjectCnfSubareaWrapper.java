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

import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;
import com.landleaf.energy.domain.vo.ProjectCnfSubareaVO;

/**
 * ProjectCnfSubareaEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectCnfSubareaWrapper extends BaseWrapper<ProjectCnfSubareaVO, ProjectCnfSubareaEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfSubareaWrapper builder() {
		return new ProjectCnfSubareaWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfSubareaVO entity2VO(ProjectCnfSubareaEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfSubareaVO vo = new ProjectCnfSubareaVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfSubareaVO> listEntity2VO(List<ProjectCnfSubareaEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfSubareaVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfSubareaVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfSubareaVO> pageEntity2VO(IPage<ProjectCnfSubareaEntity> page) {
		PageDTO<ProjectCnfSubareaVO> pageVO = new PageDTO<ProjectCnfSubareaVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfSubareaVO>());
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
	public PageDTO<ProjectCnfSubareaVO> pageEntity2VO(IPage<ProjectCnfSubareaEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}