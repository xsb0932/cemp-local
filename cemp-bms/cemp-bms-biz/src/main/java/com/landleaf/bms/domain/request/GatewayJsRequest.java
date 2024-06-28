package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "js入参")
public class GatewayJsRequest {
    @Schema(description = "js内容")
    @NotBlank(message = "js内容不能为空")
    private String func;
}
