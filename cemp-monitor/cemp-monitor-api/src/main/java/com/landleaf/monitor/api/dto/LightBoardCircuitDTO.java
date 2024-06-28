package com.landleaf.monitor.api.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class LightBoardCircuitDTO {
    private Map<String, List<LightBoardCircuitNormalDTO>> normalList;
    private Map<String, List<LightBoardCircuitHotWaterSpecialDTO>> hotWaterSpecialList;
    private Map<String, List<LightBoardCircuitHotWaterDTO>> hotWaterList;

    {
        this.normalList = new HashMap<>();
        this.hotWaterSpecialList = new HashMap<>();
        this.hotWaterList = new HashMap<>();
    }
}
