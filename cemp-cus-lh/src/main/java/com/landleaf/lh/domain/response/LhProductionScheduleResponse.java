package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "生产总进度")
public class LhProductionScheduleResponse {
    @Schema(description = "电计划")
    private String electricityPlan;
    @Schema(description = "电使用")
    private String electricityTotal;
    @Schema(description = "水计划")
    private String waterPlan;
    @Schema(description = "水使用")
    private String waterTotal;
}
