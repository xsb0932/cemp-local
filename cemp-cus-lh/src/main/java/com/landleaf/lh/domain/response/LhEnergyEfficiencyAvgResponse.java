package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "平均能效趋势")
public class LhEnergyEfficiencyAvgResponse {
    @Schema(description = "x轴")
    private Integer[] x = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    @Schema(description = "热水能效")
    private List<String> data1;
    @Schema(description = "空调能效")
    private List<String> data2;
    @Schema(description = "平均气温")
    private List<String> data3;

    {
        this.data1 = new ArrayList<>();
        this.data2 = new ArrayList<>();
        this.data3 = new ArrayList<>();
    }
}
