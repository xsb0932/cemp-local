package com.landleaf.energy.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "手工抄表气表返回集合")
public class GasMeterDeviceResponse {
    @Schema(description = "设备业务id")
    private String bizDeviceId;

    @Schema(description = "设备名称")
    private String name;
}
