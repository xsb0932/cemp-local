package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光字牌-新风VO")
public class LightBoardFreshAirResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;
    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "通讯状态:1-在线 0-离线")
    private Integer cst;

    @Schema(description = "送风湿度")
    private String supplyAirHumidity;
    @Schema(description = "送风湿度单位")
    private String supplyAirHumidityUnit;
    @Schema(description = "送风湿度是否有未确认的告警")
    private Boolean supplyAirHumidityHasUCAlarm;
    @Schema(description = "送风湿度是否有告警")
    private Boolean supplyAirHumidityHasAlarm;

    @Schema(description = "送风温度")
    private String supplyAirTemp;
    @Schema(description = "送风温度单位")
    private String supplyAirTempUnit;
    @Schema(description = "送风湿度是否有未确认的告警")
    private Boolean supplyAirTempHasUCAlarm;
    @Schema(description = "送风湿度是否有告警")
    private Boolean supplyAirTempHasAlarm;
}
