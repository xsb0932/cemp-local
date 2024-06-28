package com.landleaf.lh.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LhEnergyEfficiencySortData {
    @Schema(description = "项目名")
    private String name;
    @Schema(description = "能效")
    private String avgSQ;
    @Schema(description = "能效同比值")
    private String avgSQ2;
    @JsonIgnore
    private BigDecimal sort;
}
