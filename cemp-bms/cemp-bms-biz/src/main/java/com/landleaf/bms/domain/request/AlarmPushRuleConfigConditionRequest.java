package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "告警条件")
public class AlarmPushRuleConfigConditionRequest {
    @Schema(description = "类型(ALARM_TYPE，ALARM_LEVEL，ALARM_STATUS)")
    private String type;
    @Schema(description = "数据")
    private List<String> data;
}
