package com.landleaf.monitor.api.dto;

import lombok.Data;

@Data
public class LightBoardCircuitPumpDTO {
    private String bizProjectId;
    private String bizDeviceId;
    private String deviceName;
    private String code;
    private String bizProductId;

    private Boolean hasAlarm;
    private Boolean hasUCAlarm;

    private String fUnit;
}
