package com.landleaf.monitor.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class DeviceStaDTO {
    @Schema(description = "设备ID")
    private String bizDeviceId;

    @Schema(description = "产品ID")
    private String bizProductId;

    @Schema(description = "品类ID")
    private String bizCategoryId;

    @Schema(description = "项目ID")
    private String bizProjectId;

    @Schema(description = "项目代码")
    private String projectCode;

    @Schema(description = "其他参数，key-标识符，value-值")
    private Map<String, String> otherParams;
}
