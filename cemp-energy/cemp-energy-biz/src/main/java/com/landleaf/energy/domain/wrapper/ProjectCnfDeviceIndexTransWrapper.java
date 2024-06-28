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

import com.landleaf.energy.domain.entity.ProjectCnfDeviceIndexTransEntity;
import com.landleaf.energy.domain.vo.ProjectCnfDeviceIndexTransVO;

/**
 * 设备品类和指标维度转换配置表的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class ProjectCnfDeviceIndexTransWrapper extends BaseWrapper<ProjectCnfDeviceIndexTransVO, ProjectCnfDeviceIndexTransEntity> {
	/**
	 * 构造
	 */
	public static ProjectCnfDeviceIndexTransWrapper builder() {
		return new ProjectCnfDeviceIndexTransWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectCnfDeviceIndexTransVO entity2VO(ProjectCnfDeviceIndexTransEntity e) {
		if (null == e) {
			return null;
		}
		ProjectCnfDeviceIndexTransVO vo = new ProjectCnfDeviceIndexTransVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectCnfDeviceIndexTransVO> listEntity2VO(List<ProjectCnfDeviceIndexTransEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, ProjectCnfDeviceIndexTransVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<ProjectCnfDeviceIndexTransVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<ProjectCnfDeviceIndexTransVO> pageEntity2VO(IPage<ProjectCnfDeviceIndexTransEntity> page) {
		PageDTO<ProjectCnfDeviceIndexTransVO> pageVO = new PageDTO<ProjectCnfDeviceIndexTransVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<ProjectCnfDeviceIndexTransVO>());
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
	public PageDTO<ProjectCnfDeviceIndexTransVO> pageEntity2VO(IPage<ProjectCnfDeviceIndexTransEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}