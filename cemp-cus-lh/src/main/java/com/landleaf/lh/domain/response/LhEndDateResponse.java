package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "统计结束年月")
public class LhEndDateResponse {
    @Schema(description = "年")
    private Integer year;
    @Schema(description = "月")
    private Integer month;
}


