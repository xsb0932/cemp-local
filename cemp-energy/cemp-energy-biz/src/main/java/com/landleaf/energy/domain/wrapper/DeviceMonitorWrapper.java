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

import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.vo.DeviceMonitorVO;

/**
 * DeviceMonitorEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-06-25
 */
public class DeviceMonitorWrapper extends BaseWrapper<DeviceMonitorVO, DeviceMonitorEntity> {
	/**
	 * 构造
	 */
	public static DeviceMonitorWrapper builder() {
		return new DeviceMonitorWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceMonitorVO entity2VO(DeviceMonitorEntity e) {
		if (null == e) {
			return null;
		}
		DeviceMonitorVO vo = new DeviceMonitorVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DeviceMonitorVO> listEntity2VO(List<DeviceMonitorEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, DeviceMonitorVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<DeviceMonitorVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<DeviceMonitorVO> pageEntity2VO(IPage<DeviceMonitorEntity> page) {
		PageDTO<DeviceMonitorVO> pageVO = new PageDTO<DeviceMonitorVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<DeviceMonitorVO>());
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
	public PageDTO<DeviceMonitorVO> pageEntity2VO(IPage<DeviceMonitorEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}