package com.landleaf.monitor.api.dto;

import lombok.Data;

@Data
public class LightBoardFreshAirDTO {
    private String bizProjectId;
    private String bizDeviceId;
    private String deviceName;
    private String code;

    private String supplyAirHumidity;
    private String supplyAirHumidityUnit;

    private String supplyAirTemp;
    private String supplyAirTempUnit;

    private Boolean supplyAirHumidityHasAlarm;
    private Boolean supplyAirHumidityHasUCAlarm;

    private Boolean supplyAirTempHasAlarm;
    private Boolean supplyAirTempHasUCAlarm;
}
