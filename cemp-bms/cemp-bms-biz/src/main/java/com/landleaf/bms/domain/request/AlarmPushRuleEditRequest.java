package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "告警推送规则修改参数")
public class AlarmPushRuleEditRequest {
    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;
    @Schema(description = "推送规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    @Schema(description = "规则描述")
    private String description;
}
