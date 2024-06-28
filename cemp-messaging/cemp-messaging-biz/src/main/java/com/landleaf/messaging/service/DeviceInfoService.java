package com.landleaf.messaging.service;

import cn.hutool.json.JSONObject;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;

import java.util.Map;

/**
 * 设备信息的服务类
 */
public interface DeviceInfoService {
    /**
     * 通过outerId获取bizDeviceId
     *
     * @param gateId
     * @param pkId
     * @param sourceDevId
     */
    String queryBizDeviceIdByOuterId(String gateId, String pkId, String sourceDevId);

    /**
     * 通过bizDeviceId，查询设备信息
     *
     * @param bizDeviceId
     * @return
     */
    Map<Object, Object> queryDeviceCurrentStatus(String bizDeviceId);

    /**
     * 更新设备property的数据
     * @param bizDeviceId
     * @param deviceStatusObj
     * @param time
     */
    void refreshDeviceCurrentStatus(String updateType, String bizGateId, String bizProductId, String bizDeviceId, Map<Object, Object> deviceStatusObj, JSONObject currentVal, long time);

    /**
     * 根据bizDeviceId,获取超时时间
     * @param bizDeviceId
     * @return
     */
    long getTimeout(String bizDeviceId);

    /**
     * 更新设备Parameter的数据
     * @param bizDeviceId
     * @param deviceStatusObj
     * @param time
     */
    void refreshDeviceCurrentParameter(String type, String gateId, String pkId, String bizDeviceId, Map<Object, Object> deviceStatusObj, JSONObject parameters, long time);

    /**
     * 根据bizDeviceId,获取pkid
     * @param bizDeviceId
     * @return
     */
    DeviceInfoCacheDTO getPkId(String bizDeviceId);

    /**
     * 根据deviceInfo,获取gatewayId
     * @param deviceInfo
     * @return
     */
    String getGatewayId( DeviceInfoCacheDTO deviceInfo);
}
