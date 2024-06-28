package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "报修类型")
public class MaintenanceTypeListResponse {
    @Schema(description = "code")
    private String value;
    @Schema(description = "名称")
    private String name;
}
