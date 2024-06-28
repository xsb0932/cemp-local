package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "推送配置详情对象")
public class AlarmPushRuleConfigResponse {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "推送规则名称")
    private String ruleName;
    @Schema(description = "推送状态（系统字典:01启用 02停用）")
    private String ruleStatus;
    @Schema(description = "推送状态名称")
    private String ruleStatusName;
    @Schema(description = "规则描述")
    private String description;
    @Schema(description = "选择全部项目")
    private Boolean selectAllProject;
    @Schema(description = "告警项目")
    private List<String> bizProjectIdList;
    @Schema(description = "告警条件")
    private List<AlarmPushRuleConfigConditionResponse> conditionList;
    @Schema(description = "推送方式")
    private List<AlarmPushRuleConfigPushResponse> pushList;

    {
        this.conditionList = new ArrayList<>();
        this.pushList = new ArrayList<>();
    }
}
