package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光字牌-回路的泵VO")
public class LightBoardCircuitPumpResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;
    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "通讯状态:1-在线 0-离线")
    private Integer cst;

    @Schema(description = "泵运行状态:1-运行 0-停止")
    private String pumpRST;
    @Schema(description = "频率")
    private String f;
    @Schema(description = "频率单位")
    private String fUnit;
    @Schema(description = "是否有未确认的告警")
    private Boolean hasUCAlarm;
    @Schema(description = "是否有告警")
    private Boolean hasAlarm;
}
