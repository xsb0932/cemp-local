package com.landleaf.messaging.service;

import java.util.Map;

/**
 * CurrentAlarmService
 *
 * @author 张力方
 * @since 2023/8/14
 **/
public interface DeviceServiceService {
    /**
     * 处理故障
     *
     * @param time
     * @param alarmCode
     * @param val
     */
    void dealServiceEventInfo(String bizGateId, String pkId, String bizDeviceId, long time, String alarmCode, Map<String, Object> val, String userInfo);
}
