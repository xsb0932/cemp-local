package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaDeviceBaseRequest {
    @Schema(description = "开始时间")
    @NotNull
    private LocalDateTime start;

    @Schema(description = "结束时间")
    @NotNull
    private LocalDateTime end;

    @Schema(description = "产品-设备ids")
    private Map<String, List<String>> productDeviceIds;
}
