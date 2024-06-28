package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZnbPResponse {
    @Schema(description = "时间")
    private String time;

    @Schema(description = "功率")
    private BigDecimal p;
}
