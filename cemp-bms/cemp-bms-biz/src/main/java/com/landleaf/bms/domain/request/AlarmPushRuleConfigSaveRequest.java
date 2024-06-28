package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "配置保存参数对象")
public class AlarmPushRuleConfigSaveRequest {
    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;
    @Schema(description = "是否选择全部项目")
    @NotNull(message = "是否为全部项目的标识为空")
    private Boolean selectAllProject;
    @Schema(description = "项目业务id集合")
    private List<String> bizProjectIdList;
    @Schema(description = "告警条件")
    private List<AlarmPushRuleConfigConditionRequest> conditionList;
    @Schema(description = "推送方式")
    private List<AlarmPushRuleConfigPushRequest> pushList;

}
