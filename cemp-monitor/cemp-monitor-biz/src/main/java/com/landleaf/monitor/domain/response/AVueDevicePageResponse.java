package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "avue选择设备分页对象")
public class AVueDevicePageResponse {
    @Schema(description = "设备id（全局唯一id）")
    private String bizDeviceId;

    @Schema(description = "品类名称")
    private String categoryName;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备编码")
    private String code;

    @Schema(description = "设备位置")
    private String locationDesc;
}
