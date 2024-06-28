package com.landleaf.bms.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "条件对象")
public class AlarmPushRuleConfigConditionResponse {
    @Schema(description = "类型(ALARM_TYPE，ALARM_LEVEL，ALARM_STATUS)")
    private String type;
    @Schema(description = "数据")
    private List<String> data;
    @JsonIgnore
    private Integer sort;
}
