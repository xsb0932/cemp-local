package com.landleaf.energy.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "气表抄表记录保存or更新对象")
public class GasMeterSaveRequest {
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
    private BigDecimal gasStart;
    @Schema(description = "期末值")
    @NotNull(message = "期末值不能为空")
    private BigDecimal gasEnd;
    @Schema(description = "用气总量")
    @NotNull(message = "用气总量不能为空")
    private BigDecimal gasTotal;
    @Schema(description = "备注")
    private String remark;
}
