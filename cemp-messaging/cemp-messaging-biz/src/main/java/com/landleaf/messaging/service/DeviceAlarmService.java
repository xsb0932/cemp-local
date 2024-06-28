package com.landleaf.messaging.service;

import java.util.Map;

/**
 * CurrentAlarmService
 *
 * @author 张力方
 * @since 2023/8/14
 **/
public interface DeviceAlarmService {
    /**
     * 处理故障
     *
     * @param alarmType
     * @param time
     * @param alarmCode
     * @param alarmStatus
     */
    void dealAlarmInfo(String bizGateId, String pkId, String bizDeviceId, String alarmType, Map<Object, Object> deviceStatusObj, long time, String alarmCode, String alarmStatus);
}
