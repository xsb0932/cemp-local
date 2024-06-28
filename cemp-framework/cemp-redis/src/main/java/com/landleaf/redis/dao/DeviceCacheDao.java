package com.landleaf.redis.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.dao.dto.DeviceParameterValueCacheDTO;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Data
@Component
public class DeviceCacheDao {
    @Resource
    private RedisTemplate<String, String> redisStringTemplate;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 更新messaging中设备id缓存
     *
     * @param bizDeviceId  设备业务id
     * @param bizProductId 产品业务id
     * @param bizGatewayId 网关业务id
     * @param sourceDevId  设备外部id
     */
    public void updateMessagingIdRelationCache(String bizDeviceId, String bizProductId, String bizGatewayId, String sourceDevId) {
        redisUtils.hset(KeyConstance.OUTER_DEVICE_RELATION, String.format(KeyConstance.OUTER_DEVICE_KEY, bizGatewayId, bizProductId, sourceDevId), bizDeviceId);
        redisUtils.hset(KeyConstance.DEVICE_OUTER_RELATION, String.format(KeyConstance.OUTER_DEVICE_KEY, bizGatewayId, bizProductId, bizDeviceId), sourceDevId);
    }

    /**
     * 保存设备信息缓存
     *
     * @param deviceInfoCacheDTO 设备信息
     */
    public void saveDeviceInfoCache(DeviceInfoCacheDTO deviceInfoCacheDTO) {
        if (null == deviceInfoCacheDTO) {
            return;
        }
        redisStringTemplate.opsForHash().put(KeyConstance.DEVICE_INFO_CACHE, deviceInfoCacheDTO.getBizDeviceId(), JSON.toJSONString(deviceInfoCacheDTO));
    }

    /**
     * 获取设备信息缓存
     *
     * @param bizDeviceId 设备业务id
     * @return 设备信息
     */
    public DeviceInfoCacheDTO getDeviceInfoCache(String bizDeviceId) {
        Object cache = redisStringTemplate.opsForHash().get(KeyConstance.DEVICE_INFO_CACHE, bizDeviceId);
        if (null == cache) {
            return null;
        }
        return JSON.parseObject(cache.toString(), DeviceInfoCacheDTO.class);
    }

    /**
     * 保存设备参数配置缓存
     *
     * @param bizDeviceId                      设备业务id
     * @param deviceParameterValueCacheDTOList 设备参数配置信息
     */
    public void saveDeviceParameterValueCache(String bizDeviceId, List<DeviceParameterValueCacheDTO> deviceParameterValueCacheDTOList) {
        if (StrUtil.isBlank(bizDeviceId) || CollectionUtil.isEmpty(deviceParameterValueCacheDTOList)) {
            redisStringTemplate.opsForHash().delete(KeyConstance.DEVICE_PARAMETER_VALUE_CACHE, bizDeviceId);
            return;
        }
        redisStringTemplate.opsForHash().put(KeyConstance.DEVICE_PARAMETER_VALUE_CACHE, bizDeviceId, JSON.toJSONString(deviceParameterValueCacheDTOList));
    }


    /**
     * 获取设备参数配置缓存
     *
     * @param bizDeviceId 设备业务id
     * @return 设备参数配置
     */
    public List<DeviceParameterValueCacheDTO> getDeviceParameterValueCache(String bizDeviceId) {
        Object cache = redisStringTemplate.opsForHash().get(KeyConstance.DEVICE_PARAMETER_VALUE_CACHE, bizDeviceId);
        if (null == cache) {
            return Collections.emptyList();
        }
        return JSON.parseArray(cache.toString(), DeviceParameterValueCacheDTO.class);
    }
}
