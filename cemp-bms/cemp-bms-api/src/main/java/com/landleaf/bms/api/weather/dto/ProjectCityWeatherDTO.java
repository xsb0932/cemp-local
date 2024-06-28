package com.landleaf.bms.api.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yang
 */
@Data
public class ProjectCityWeatherDTO {
    @Schema(description = "天气code")
    private String weatherCode;
    @Schema(description = "天气城市名")
    private String weatherName;
}
