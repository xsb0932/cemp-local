package com.landleaf.bms.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "条件对象")
public class AlarmPushRuleConfigPushResponse {
    @Schema(description = "类型(0邮件 1短信 2钉钉)")
    private Integer type;
    @Schema(description = "数据")
    private List<String> data;
    @JsonIgnore
    private Integer sort;
}
