package com.landleaf.monitor.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class LightBoardCircuitHotWaterSpecialDTO {
    private String bizProjectId;
    private String bizDeviceId;
    private String deviceName;
    private String code;

    private List<LightBoardCircuitPumpDTO> pumpList;

}
