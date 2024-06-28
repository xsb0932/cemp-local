package com.landleaf.energy.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "水表抄表记录保存or更新对象")
public class WaterMeterSaveRequest {
    @Schema(description = "抄表周期类型")
    @NotBlank(message = "抄表周期类型不能为空")
    private String meterReadCycle;
    @Schema(description = "设备业务id")
    @NotBlank(message = "设备业务id不能为空")
    private String bizDeviceId;
    @Schema(description = "期数")
    @NotBlank(message = "期数不能为空")
    private String time;
    @Schema(description = "期初值")
    private BigDecimal waterStart;
    @Schema(description = "期末值")
    @NotNull(message = "期末值不能为空")
    private BigDecimal waterEnd;
    @Schema(description = "用水总量")
    @NotNull(message = "用水总量不能为空")
    private BigDecimal waterTotal;
    @Schema(description = "备注")
    private String remark;
}
