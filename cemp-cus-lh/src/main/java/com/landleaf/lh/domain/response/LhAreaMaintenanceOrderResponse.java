package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xusihbai
 * @since 2024/06/11
 **/
@Data
@Schema(description = "报修排名")
@AllArgsConstructor
public class LhAreaMaintenanceOrderResponse {

    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "项目面积")
    private String area;
    @Schema(description = "报修数量")
    private String maintenanceNum;
    @Schema(description = "报修数量排序")
    private Long maintenanceNumSort;


}
