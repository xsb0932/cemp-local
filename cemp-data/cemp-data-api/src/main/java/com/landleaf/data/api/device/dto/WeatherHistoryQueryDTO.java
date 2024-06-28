package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "WeatherHistoryQueryDTO对象", description = "环境查询参数")
public class WeatherHistoryQueryDTO {

    @Schema(description = "发布时间")
    @NotNull
    private LocalDateTime publishTime;

}
