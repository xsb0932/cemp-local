package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备管理-测点")
public class DeviceManagerAttributesResponse {
    @Schema(description = "属性标识")
    private String attrCode;
    @Schema(description = "属性名称")
    private String name;
}
