package com.landleaf.lh.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光字牌-主机VO")
public class LightBoardEngineResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;
    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "通讯状态:1-在线 0-离线")
    private Integer cst;
    @Schema(description = "主机负荷率")
    private String plr;
    @Schema(description = "主机运行状态:1-运行 0-停止")
    private String pumpRST;
    @Schema(description = "是否有未确认的告警")
    private Boolean hasUCAlarm;
    @Schema(description = "是否有告警")
    private Boolean hasAlarm;

    @JsonIgnore
    private String zfBizDeviceId;
    @Schema(description = "蒸发器阀门开到位:1-开到位 0-关")
    private String evaporatingValveOpenedFlag;
    @Schema(description = "蒸发器进水温度")
    private String evaporatingInTemp;
    @Schema(description = "蒸发器进水温度单位")
    private String evaporatingInTempUnit;
    @Schema(description = "蒸发器出水温度")
    private String evaporatingOutTemp;
    @Schema(description = "蒸发器出水温度单位")
    private String evaporatingOutTempUnit;

    @JsonIgnore
    private String lnBizDeviceId;
    @Schema(description = "冷凝器阀门开到位:1-开到位 0-关")
    private String condensingValveOpenedFlag;
    @Schema(description = "冷凝器进水温度")
    private String condensingInTemp;
    @Schema(description = "冷凝器进水温度单位")
    private String condensingInTempUnit;
    @Schema(description = "冷凝器出水温度")
    private String condensingOutTemp;
    @Schema(description = "冷凝器出水温度单位")
    private String condensingOutTempUnit;
}
