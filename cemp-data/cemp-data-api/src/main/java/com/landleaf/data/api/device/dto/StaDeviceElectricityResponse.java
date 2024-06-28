package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StaDeviceElectricityResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;

    @Schema(description = "产品ID")
    private String bizProductId;

    @Schema(description = "正向有功总电能-开始值")
    private BigDecimal startData;

    @Schema(description = "正向有功总电能-结束值")
    private BigDecimal endData;

    @Schema(description = "反向有功总电能-开始值")
    private BigDecimal reStartData;

    @Schema(description = "反向有功总电能-结束值")
    private BigDecimal reEndData;
}
