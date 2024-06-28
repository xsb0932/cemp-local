package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报修单详情")
public class MaintenanceInfoResponse {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "项目id")
    private String bizProjectId;
    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "房号")
    private String room;
    @Schema(description = "报修月份")
    private String yearMonth;
    @Schema(description = "报修日期")
    private String maintenanceDate;
    @Schema(description = "报修单类别")
    private String maintenanceType;
    @Schema(description = "报修内容")
    private String content;
}
