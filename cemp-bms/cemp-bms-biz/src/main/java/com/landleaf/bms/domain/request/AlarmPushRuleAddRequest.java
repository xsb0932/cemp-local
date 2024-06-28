package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "告警推送规则新增参数")
public class AlarmPushRuleAddRequest {
    @Schema(description = "推送规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    @Schema(description = "规则描述")
    private String description;
}
