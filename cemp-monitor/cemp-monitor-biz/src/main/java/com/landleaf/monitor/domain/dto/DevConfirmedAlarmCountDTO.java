package com.landleaf.monitor.domain.dto;

import lombok.Data;

/**
 * 设备当前未确认的告警的数量
 */
@Data
public class DevConfirmedAlarmCountDTO {

    /**
     * bizDeviceId
     */
    private String bizDeviceId;

    /**
     * 未确认的告警数
     */
    private Integer count;
}
