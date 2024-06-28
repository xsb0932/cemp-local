package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WeatherHistoryDTO {
    @Schema(description = "气象城市编码")
    private String weatherCode;

    @Schema(description = "温度")
    private BigDecimal temperature;

    @Schema(description = "湿度")
    private BigDecimal humidity;

    @Schema(description = "发布时间")
    private LocalDateTime updateTime;

    @Schema(description = "统计时间")
    private String staTime;
}
