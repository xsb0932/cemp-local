package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BasePRequest {
    @Schema(description = "设备id")
    private List<String> deviceIds;
    @Schema(description = "产品业务id")
    private String productBizId;
    @Schema(description = "开始时间")
    @NotNull
    private LocalDateTime start;

    @Schema(description = "结束时间")
    @NotNull
    private LocalDateTime end;
}
