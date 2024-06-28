package com.landleaf.lh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MaintenanceExportDTO {
    private String projectName;
    private String room;
    private String yearMonth;
    @Schema(description = "报修日期")
    private String maintenanceDate;
    @Schema(description = "报修单类别")
    private String maintenanceType;
    @Schema(description = "报修内容")
    private String content;
}
