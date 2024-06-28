package com.landleaf.monitor.dto;

import lombok.Data;

/**
 * @Data
 */
@Data
public class DeviceAlarmSummaryResponse {
    /**
     * 设备编号
     */
    private String bizDeviceId;

    /**
     * 设备告警
     */
    private int devAlarmCount;

    /**
     * 规则告警
     */
    private int ruleAlarmCount;

    /**
     * 告警总数：设备+规则+其他（暂时没有，后期可能扩充），不包含connAlarmCount
     */
    private int devTotalAlarmCount;

    /**
     * 未确认的当前告警
     */
    private int unconfirmedCount;
}
