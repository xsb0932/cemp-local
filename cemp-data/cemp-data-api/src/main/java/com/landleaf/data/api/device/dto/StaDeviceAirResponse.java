package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class StaDeviceAirResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;

    @Schema(description = "产品ID")
    private String bizProductId;

    @Schema(description = "通讯状态数据")
    private List<IntAttrValue> cstDataList;

    @Schema(description = "运行状态数据")
    private List<IntAttrValue> rstDataList;

    @Schema(description = "温度数据")
    private List<BigDecimalAttrValue> temperatureDataList;

}
