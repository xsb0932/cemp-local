package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StaDeviceWaterResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;

    @Schema(description = "产品ID")
    private String bizProductId;

    @Schema(description = "用水量-开始值")
    private BigDecimal startData;

    @Schema(description = "用水量-结束值")
    private BigDecimal endData;
}
