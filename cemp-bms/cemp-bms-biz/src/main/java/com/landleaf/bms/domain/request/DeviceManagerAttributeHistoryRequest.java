package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "设备管理-历史数据查询参数")
public class DeviceManagerAttributeHistoryRequest {
    @Schema(description = "开始时间 yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "开始时间不能为空")
    private String start;

    @Schema(description = "结束时间 yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "结束时间不能为空")
    private String end;

    @Schema(description = "设备业务id")
    @NotBlank(message = "设备业务id不能为空")
    private String bizDeviceId;

    @Schema(description = "属性编码，多个以逗号分割")
    @NotBlank(message = "属性编码不能为空")
    private String attrCode;

    @Schema(description = "周期类型：0=>原始（默认）、1=>5分钟、2=>10分钟、3=>30分钟、4=>1小时、5=>8小时、6=>1天")
    @NotNull(message = "周期类型不能为空")
    private Integer periodType;
}
