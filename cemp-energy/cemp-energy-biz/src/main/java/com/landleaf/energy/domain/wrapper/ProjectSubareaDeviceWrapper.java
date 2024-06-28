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

import com.landleaf.energy.domain.entity.ProjectSubareaDeviceEntity;
import com.landleaf.energy.domain.vo.ProjectSubareaDeviceVO;

/**
 * ProjectSubareaDeviceEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectSubareaDeviceWrapper extends BaseWrapper<ProjectSubareaDeviceVO, ProjectSubareaDeviceEntity> {
	/**
	 * 构造
	 */
	public static ProjectSubareaDeviceWrapper builder() {
		return new ProjectSubareaDeviceWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectSubareaDeviceVO entity2VO(ProjectSubareaDeviceEntity e) {
		if (null == e) {
			return null;
		}
		ProjectSubareaDeviceVO vo = new ProjectSubareaDeviceVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectSubareaDeviceVO> listEntity2VO(List<ProjectSubareaDeviceEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectSubareaDeviceVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectSubareaDeviceVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectSubareaDeviceVO> pageEntity2VO(IPage<ProjectSubareaDeviceEntity> page) {
		PageDTO<ProjectSubareaDeviceVO> pageVO = new PageDTO<ProjectSubareaDeviceVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectSubareaDeviceVO>());
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
	public PageDTO<ProjectSubareaDeviceVO> pageEntity2VO(IPage<ProjectSubareaDeviceEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}