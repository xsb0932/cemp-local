package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "能效排名")
public class LhEnergyEfficiencySortResponse {
    @Schema(description = "空调")
    private List<LhEnergyEfficiencySortData> electricity;
    @Schema(description = "热水")
    private List<LhEnergyEfficiencySortData> heatingWater;

    {
        this.electricity = new ArrayList<>();
        this.heatingWater = new ArrayList<>();
    }
}
