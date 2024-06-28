package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报修数量排名")
public class LhMaintenanceSortResponse {
    @Schema(description = "项目名")
    private String name;
    @Schema(description = "报修数")
    private Long total;
}
