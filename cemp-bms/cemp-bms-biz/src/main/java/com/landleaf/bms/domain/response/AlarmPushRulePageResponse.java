package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "告警推送分页属性对象")
public class AlarmPushRulePageResponse {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "推送规则名称")
    private String ruleName;
    @Schema(description = "告警项目")
    private List<String> projectList;
    @Schema(description = "告警类型")
    private List<String> alarmTypeList;
    @Schema(description = "告警等级")
    private List<String> alarmLevelList;
    @Schema(description = "告警状态")
    private List<String> alarmStatusList;
    @Schema(description = "邮件推送用户")
    private List<String> emailUserList = new ArrayList<>();
    @Schema(description = "短信推送用户")
    private List<String> messageUserList = new ArrayList<>();
    @Schema(description = "钉钉推送机器人")
    private List<String> dingUserList = new ArrayList<>();
    @Schema(description = "推送状态（系统字典:01启用 02停用）")
    private String ruleStatus;
    @Schema(description = "推送状态名称")
    private String ruleStatusName;
    @Schema(description = "规则描述")
    private String description;
}
