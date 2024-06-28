package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "avue选择设备查询对象")
public class AVueDeviceAttrPageRequest {
    @Schema(description = "设备业务id")
    @NotBlank
    private String bizDeviceId;

    @Schema(description = "功能标识符")
    private String attrCode;

    @Schema(description = "功能名称")
    private String attrName;
}