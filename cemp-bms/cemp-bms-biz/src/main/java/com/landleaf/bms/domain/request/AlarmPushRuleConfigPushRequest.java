package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "推送方式")
public class AlarmPushRuleConfigPushRequest {
    @Schema(description = "类型(0邮件 1短信 2钉钉)")
    private Integer type;
    @Schema(description = "数据")
    private List<String> data;
}
