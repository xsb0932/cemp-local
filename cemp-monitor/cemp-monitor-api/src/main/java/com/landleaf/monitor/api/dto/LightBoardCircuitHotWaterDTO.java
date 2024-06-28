package com.landleaf.monitor.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class LightBoardCircuitHotWaterDTO {
    private String bizProjectId;
    private String bizDeviceId;
    private String deviceName;
    private String code;
    private String bizProductId;

    private String supplyWaterTempUnit;
    private String supplyWaterPressureUnit;

    private List<LightBoardCircuitPumpDTO> pumpList;
}
