package com.landleaf.bms.context;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.dal.mapper.DeviceIotMapper;
import com.landleaf.bms.dal.mapper.DeviceParameterDetailMapper;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.bms.domain.entity.DeviceParameterDetailEntity;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.dao.DeviceCacheDao;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.dao.dto.DeviceParameterValueCacheDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DeviceCacheInit implements ApplicationRunner {
    private DeviceCacheDao deviceCacheDao;
    private DeviceIotMapper deviceIotMapper;
    private DeviceParameterDetailMapper deviceParameterDetailMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TenantContext.setIgnore(true);
        List<DeviceIotEntity> deviceList = deviceIotMapper.selectList(Wrappers.emptyWrapper());
        List<DeviceParameterValueCacheDTO> deviceParameterValueCacheDTOList = new ArrayList<>();
        for (DeviceIotEntity device : deviceList) {
            DeviceInfoCacheDTO deviceInfoCacheDTO = BeanUtil.copyProperties(device, DeviceInfoCacheDTO.class);
            log.info("[start]>>>初始化设备信息缓存 {}", device.getBizDeviceId());
            deviceCacheDao.saveDeviceInfoCache(deviceInfoCacheDTO);

            deviceParameterValueCacheDTOList.clear();
            List<DeviceParameterDetailEntity> parameters = deviceParameterDetailMapper.selectList(new LambdaQueryWrapper<DeviceParameterDetailEntity>().eq(DeviceParameterDetailEntity::getBizDeviceId, device.getBizDeviceId()));
            for (DeviceParameterDetailEntity parameter : parameters) {
                DeviceParameterValueCacheDTO cacheDTO = new DeviceParameterValueCacheDTO();
                cacheDTO.setIdentifier(parameter.getIdentifier()).setValue(parameter.getValue());
                deviceParameterValueCacheDTOList.add(cacheDTO);
            }
            if (deviceParameterValueCacheDTOList.isEmpty()) {
                continue;
            }
            deviceCacheDao.saveDeviceParameterValueCache(device.getBizDeviceId(), deviceParameterValueCacheDTOList);
        }
    }
}
