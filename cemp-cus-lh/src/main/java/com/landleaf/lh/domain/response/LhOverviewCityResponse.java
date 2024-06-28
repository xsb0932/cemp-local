package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "总览地图数据")
public class LhOverviewCityResponse {
    @Schema(description = "城市名")
    private String name;
    @Schema(description = "数据")
    private List<Object> value = new ArrayList<>();
}
