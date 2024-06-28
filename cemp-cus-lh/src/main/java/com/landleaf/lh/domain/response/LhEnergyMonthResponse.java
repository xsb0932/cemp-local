package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "总能耗趋势")
public class LhEnergyMonthResponse {
    @Schema(description = "x轴")
    private Integer[] x = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    @Schema(description = "电计划值")
    private List<String> data1;
    @Schema(description = "电实际值")
    private List<String> data2;
    @Schema(description = "水计划值")
    private List<String> data3;
    @Schema(description = "水实际值")
    private List<String> data4;

    {
        data1 = new ArrayList<>();
        data2 = new ArrayList<>();
        data3 = new ArrayList<>();
        data4 = new ArrayList<>();
    }

    public LhEnergyMonthResponse empty() {
        for (int i = 0; i < 12; i++) {
            data1.add(null);
            data2.add(null);
            data3.add(null);
            data4.add(null);
        }
        return this;
    }
}
