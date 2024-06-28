package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "属性")
public class AVueDeviceCurrentAttrDTO {
    @Schema(description = "功能名称")
    private String attrName;

    @Schema(description = "属性值")
    private String value;

    @Schema(description = "单位")
    private String unit;
}
