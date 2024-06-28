package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "项目业态面积")
public class LhProjectBizTypeAreaResponse {
    @Schema(description = "业态")
    private String bizTypeName;
    @Schema(description = "面积")
    private BigDecimal area;
}
