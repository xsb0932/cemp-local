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

import com.landleaf.energy.domain.entity.ProjectSubitemDeviceEntity;
import com.landleaf.energy.domain.vo.ProjectSubitemDeviceVO;

/**
 * ProjectSubitemDeviceEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectSubitemDeviceWrapper extends BaseWrapper<ProjectSubitemDeviceVO, ProjectSubitemDeviceEntity> {
	/**
	 * 构造
	 */
	public static ProjectSubitemDeviceWrapper builder() {
		return new ProjectSubitemDeviceWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectSubitemDeviceVO entity2VO(ProjectSubitemDeviceEntity e) {
		if (null == e) {
			return null;
		}
		ProjectSubitemDeviceVO vo = new ProjectSubitemDeviceVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectSubitemDeviceVO> listEntity2VO(List<ProjectSubitemDeviceEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectSubitemDeviceVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectSubitemDeviceVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectSubitemDeviceVO> pageEntity2VO(IPage<ProjectSubitemDeviceEntity> page) {
		PageDTO<ProjectSubitemDeviceVO> pageVO = new PageDTO<ProjectSubitemDeviceVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectSubitemDeviceVO>());
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
	public PageDTO<ProjectSubitemDeviceVO> pageEntity2VO(IPage<ProjectSubitemDeviceEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}