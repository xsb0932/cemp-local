package com.landleaf.messaging.service;

import java.util.Map;

/**
 * DeviceEventService
 *
 * @author 张力方
 * @since 2023/8/14
 **/
public interface DeviceEventService {
    /**
     * 处理故障
     *
     * @param time
     * @param alarmCode
     */
    void dealEventInfo(String bizGateId, String pkId, String bizDeviceId, Map<Object, Object> deviceStatusObj, long time, String alarmCode, Map<String, Object> val);
}
