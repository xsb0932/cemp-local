package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "avue选择设备属性分页对象")
public class AVueDeviceAttrPageResponse {
    @Schema(description = "功能标识符")
    private String attrCode;

    @Schema(description = "功能名称")
    private String attrName;

    @Schema(description = "单位")
    private String unit;
}
