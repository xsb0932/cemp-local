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
public class StaLatestDataRequest {
    @Schema(description = "时间")
    @NotNull
    private LocalDateTime time;

    @Schema(description = "产品业务id")
    @NotNull
    private String bizProductId;

    @Schema(description = "设备业务id")
    @NotNull
    private String bizDeviceId;

    @Schema(description = "字段名")
    @NotNull
    private String field;


}
