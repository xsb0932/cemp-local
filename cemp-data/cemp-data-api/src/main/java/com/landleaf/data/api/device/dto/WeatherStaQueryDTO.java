package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Data
@Schema(name = "WeatherStaQueryDTO", description = "环境查询参数")
public class WeatherStaQueryDTO {

    @Schema(description = "查询开始")
    private LocalDateTime queryBeginTime;

    @Schema(description = "查询结束")
    private LocalDateTime queryEndTime;

    @Schema(description = "城市")
    private String cityName;

    @Schema(description = "查询月份")
    private List<YearMonth> yms;

    @Schema(description = "类型 1:月 2:年")
    private Integer type;



}
