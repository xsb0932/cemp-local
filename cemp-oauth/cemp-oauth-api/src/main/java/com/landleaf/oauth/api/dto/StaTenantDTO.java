package com.landleaf.oauth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StaTenantDTO {
    @Schema(description = "租户id")
    private Long id;

    @Schema(description = "租户企业code")
    private String code;

    /**
     * 月报周期
     */
    private String reportingCycle;
}
