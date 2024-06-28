package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "光字牌-非热水回路VO")
public class LightBoardCircuitNormalResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;
    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "通讯状态:1-在线 0-离线")
    private Integer cst;

    @Schema(description = "回水压力")
    private String returnWaterPressure;
    @Schema(description = "回水压力单位")
    private String returnWaterPressureUnit;

    @Schema(description = "供水压力")
    private String supplyWaterPressure;
    @Schema(description = "供水压力单位")
    private String supplyWaterPressureUnit;

    @Schema(description = "水泵")
    private List<LightBoardCircuitPumpResponse> pumpList;

}
