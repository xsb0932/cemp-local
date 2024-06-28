package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "总览数据")
public class LhOverviewResponse {
    @Schema(description = "总用电")
    private BigDecimal electricity;
    @Schema(description = "总用水")
    private BigDecimal water;
    @Schema(description = "总碳排")
    private BigDecimal co2;
    @Schema(description = "城市项目面积")
    private List<LhOverviewCityResponse> cityList;
}
