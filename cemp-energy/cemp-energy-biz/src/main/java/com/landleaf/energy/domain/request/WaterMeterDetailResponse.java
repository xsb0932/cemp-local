package com.landleaf.energy.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "水表抄表详情")
public class WaterMeterDetailResponse {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "设备业务id")
    private String bizDeviceId;
    @Schema(description = "设备名称")
    private String name;
    @Schema(description = "期数")
    private String time;
    @Schema(description = "期初值")
    private BigDecimal waterStart;
    @Schema(description = "期末值")
    private BigDecimal waterEnd;
    @Schema(description = "用水总量")
    private BigDecimal waterTotal;
    @Schema(description = "抄表时间")
    private LocalDateTime updateTime;
    @Schema(description = "抄表人员")
    private String username;
    @Schema(description = "备注")
    private String remark;
}
