package com.landleaf.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.landleaf.bms.dal.mapper.DeviceParameterDetailMapper;
import com.landleaf.bms.domain.entity.DeviceParameterDetailEntity;
import com.landleaf.bms.service.DeviceParameterDetailService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 设备参数明细表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-07-24
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceParameterDetailServiceImpl extends ServiceImpl<DeviceParameterDetailMapper, DeviceParameterDetailEntity> implements DeviceParameterDetailService {

	/**
	 * 数据库操作句柄
	 */
	private final DeviceParameterDetailMapper deviceParameterDetailMapper;


	@Override
	public int deleteByIdentifiers(String bizDeviceId, List<String> identifiers) {
		return deviceParameterDetailMapper.delete(new QueryWrapper<DeviceParameterDetailEntity>().lambda().eq(DeviceParameterDetailEntity::getBizDeviceId, bizDeviceId).in(DeviceParameterDetailEntity::getIdentifier, identifiers));
	}
}
