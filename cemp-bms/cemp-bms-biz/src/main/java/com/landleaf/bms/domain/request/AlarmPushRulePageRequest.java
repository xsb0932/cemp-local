package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警推送分页请求参数")
public class AlarmPushRulePageRequest extends PageParam {
    @Schema(description = "启用状态")
    private String ruleStatus;
    @Schema(description = "规则名称")
    private String ruleName;
}
