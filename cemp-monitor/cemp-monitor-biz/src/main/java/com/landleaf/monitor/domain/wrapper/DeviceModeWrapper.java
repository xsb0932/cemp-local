package com.landleaf.monitor.domain.wrapper;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.pgsql.base.wrapper.BaseWrapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.landleaf.monitor.domain.entity.DeviceModeEntity;
import com.landleaf.monitor.domain.vo.DeviceModeVO;

/**
 * DeviceModeEntity对象的展示类型转化工具
 *
 * @author hebin
 * @since 2023-09-14
 */
public class DeviceModeWrapper extends BaseWrapper<DeviceModeVO, DeviceModeEntity> {
	/**
	 * 构造
	 */
	public static DeviceModeWrapper builder() {
		return new DeviceModeWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceModeVO entity2VO(DeviceModeEntity e) {
		if (null == e) {
			return null;
		}
		DeviceModeVO vo = new DeviceModeVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DeviceModeVO> listEntity2VO(List<DeviceModeEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, DeviceModeVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<DeviceModeVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<DeviceModeVO> pageEntity2VO(IPage<DeviceModeEntity> page) {
		PageDTO<DeviceModeVO> pageVO = new PageDTO<DeviceModeVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<DeviceModeVO>());
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
	public PageDTO<DeviceModeVO> pageEntity2VO(IPage<DeviceModeEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}