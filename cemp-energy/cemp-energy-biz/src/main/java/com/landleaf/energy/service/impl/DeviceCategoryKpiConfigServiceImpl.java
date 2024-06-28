package com.landleaf.energy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.DeviceCategoryKpiConfigMapper;
import com.landleaf.energy.domain.vo.EnergySelectedVO;
import com.landleaf.energy.domain.vo.ProjectStaKpiDeviceVO;
import com.landleaf.energy.util.KpiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.landleaf.energy.domain.entity.DeviceCategoryKpiConfigEntity;
import com.landleaf.energy.service.DeviceCategoryKpiConfigService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DeviceCategoryKpiConfigEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-08-01
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceCategoryKpiConfigServiceImpl extends ServiceImpl<DeviceCategoryKpiConfigMapper, DeviceCategoryKpiConfigEntity> implements DeviceCategoryKpiConfigService {

	/**
	 * 数据库操作句柄
	 */
	private final DeviceCategoryKpiConfigMapper deviceCategoryKpiConfigMapper;

	@Override
	public List<DeviceCategoryKpiConfigEntity> listCategory() {
		TenantContext.setIgnore(true);
		return deviceCategoryKpiConfigMapper.listCategory(TenantContext.getTenantId());
	}

	@Override
	public List<EnergySelectedVO> getKpi(String code) {
		TenantContext.setIgnore(true);
		List<DeviceCategoryKpiConfigEntity> total = deviceCategoryKpiConfigMapper.listAll();
		if(StringUtils.isNotBlank(code)){
			Map<String,List<DeviceCategoryKpiConfigEntity>> kpiMap = total.stream().collect(Collectors.groupingBy(DeviceCategoryKpiConfigEntity::getBizCategoryId));
			List<DeviceCategoryKpiConfigEntity> configs = kpiMap.get(code);
			return configs.stream().map(config -> new EnergySelectedVO(config.getName(),config.getCode(),KpiUtils.kpiToProperty(config.getCode(), ProjectStaKpiDeviceVO.class),config.getUnit())).collect(Collectors.toList());

		}else {
			return total.stream().map(config -> new EnergySelectedVO(config.getName(),config.getCode(),KpiUtils.kpiToProperty(config.getCode(),ProjectStaKpiDeviceVO.class),config.getUnit())).collect(Collectors.toList());
		}
	}
}
