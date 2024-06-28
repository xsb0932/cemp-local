package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "告警推送参数")
public class AlarmPushRequest {
    @Schema(description = "租户id")
    @NotNull(message = "租户id不能为空")
    private Long tenantId;
    @Schema(description = "项目业务ID")
    @NotBlank(message = "项目业务ID不能为空")
    private String bizProjectId;
    @Schema(description = "告警类型")
    @NotBlank(message = "告警类型不能为空")
    private String alarmType;
    @Schema(description = "告警等级")
    @NotBlank(message = "告警等级不能为空")
    private String alarmLevel;
    @Schema(description = "告警状态")
    @NotBlank(message = "告警状态不能为空")
    private String alarmStatus;

    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "对象名称")
    private String objName;
    @Schema(description = "发生时间")
    private LocalDateTime eventTime;
    @Schema(description = "内容")
    private String alarmDesc;
}
