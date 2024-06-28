package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "平均报修数量趋势")
public class LhMaintenanceAverageResponse {
    @Schema(description = "x轴")
    private Integer[] x = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    @Schema(description = "平均报修数")
    private List<String> data1;
    @Schema(description = "同比去年")
    private List<String> data2;

    {
        data1 = new ArrayList<>();
        data2 = new ArrayList<>();
    }
}
