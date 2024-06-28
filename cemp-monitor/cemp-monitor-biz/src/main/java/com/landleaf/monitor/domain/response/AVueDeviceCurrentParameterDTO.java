package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "参数")
public class AVueDeviceCurrentParameterDTO {
    @Schema(description = "功能名称")
    private String parameterName;

    @Schema(description = "参数值")
    private String value;

    @Schema(description = "单位")
    private String unit;
}
