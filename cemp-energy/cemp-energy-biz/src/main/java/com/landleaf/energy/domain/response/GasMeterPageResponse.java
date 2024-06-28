package com.landleaf.energy.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "气表抄表分页视图对象")
public class GasMeterPageResponse {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "设备业务id")
    private String bizDeviceId;
    @Schema(description = "设备名称")
    private String name;
    @Schema(description = "期数")
    private String time;
    @Schema(description = "期初值")
    private BigDecimal gasStart;
    @Schema(description = "期末值")
    private BigDecimal gasEnd;
    @Schema(description = "用水总量")
    private BigDecimal gasTotal;
    @Schema(description = "抄表时间")
    private LocalDateTime updateTime;
    @Schema(description = "抄表人员")
    private String username;
    @Schema(description = "备注")
    private String remark;
}
