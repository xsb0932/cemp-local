package com.landleaf.data.api.device.dto;

import lombok.Data;

import java.util.List;

/**
 * 设备告警
 */
@Data
public class DeviceAlarmDTO {

    private String bizDeviceId;

    private List<String> devAlarmCode;

    private List<String> ruleAlarmCode;
}
