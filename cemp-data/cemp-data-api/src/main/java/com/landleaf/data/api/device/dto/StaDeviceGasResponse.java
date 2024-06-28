package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StaDeviceGasResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;

    @Schema(description = "产品ID")
    private String bizProductId;

    @Schema(description = "用气量-开始值")
    private BigDecimal startData;

    @Schema(description = "用气量-结束值")
    private BigDecimal endData;
}
