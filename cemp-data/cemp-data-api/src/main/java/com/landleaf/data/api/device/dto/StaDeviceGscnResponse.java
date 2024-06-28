package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class StaDeviceGscnResponse {
    @Schema(description = "设备ID")
    private String bizDeviceId;

    @Schema(description = "产品ID")
    private String bizProductId;

    @Schema(description = "通讯状态数据")
    private List<IntAttrValue> cstDataList = new ArrayList<>();

    @Schema(description = "运行状态数据")
    private List<IntAttrValue> pcsrstDataList = new ArrayList<>();

    @Schema(description = "有功用电量-开始值")
    private BigDecimal epimpStartData;

    @Schema(description = "有功用电量-结束值")
    private BigDecimal epimpEndData;

    @Schema(description = "有功发电量-开始值")
    private BigDecimal epexpStartData;

    @Schema(description = "有功发电量-结束值")
    private BigDecimal epexpEndData;

}
