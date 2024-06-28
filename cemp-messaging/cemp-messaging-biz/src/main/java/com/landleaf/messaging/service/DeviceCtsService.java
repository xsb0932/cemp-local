package com.landleaf.messaging.service;

import com.landleaf.messaging.domain.DeviceLastCommunicationInfo;

import java.util.Map;

/**
 * 设备连接状态的逻辑定义
 */
public interface DeviceCtsService {
    /**
     * 处理设备在线离线信息
     *
     * @param deviceStatusObj
     */
    void refreshDeviceConnStatus(String gateId, String pkId, String bizDeviceId, Map<Object, Object> deviceStatusObj, long time, long timeout);

    /**
     * 处理设备连接状态
     * @param msg
     */
    void dealDeviceConnStatus(DeviceLastCommunicationInfo msg);
}
