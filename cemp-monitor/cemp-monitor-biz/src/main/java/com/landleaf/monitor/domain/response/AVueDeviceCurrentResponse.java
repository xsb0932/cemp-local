package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "AVue设备当前属性&参数")
public class AVueDeviceCurrentResponse {
    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "属性")
    private List<AVueDeviceCurrentAttrDTO> attrs;

    @Schema(description = "参数")
    private List<AVueDeviceCurrentParameterDTO> parameters;

    {
        this.attrs = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }
}
