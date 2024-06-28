package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;

@Data
@Schema(description = "项目汇总数据")
public class LhProjectResponse {
    @Schema(description = "项目数量")
    private Integer total;
    @Schema(description = "项目面积")
    private BigDecimal area;
    @Schema(description = "业态面积占比")
    private Collection<LhProjectBizTypeAreaResponse> bizTypeArea;
}
