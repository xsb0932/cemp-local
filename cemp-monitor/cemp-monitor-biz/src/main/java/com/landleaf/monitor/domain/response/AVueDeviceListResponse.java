package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "avue选择设备下拉对象")
public class AVueDeviceListResponse {
    @Schema(description = "设备id（全局唯一id）")
    private String bizDeviceId;

    @Schema(description = "设备名称")
    private String name;
}
