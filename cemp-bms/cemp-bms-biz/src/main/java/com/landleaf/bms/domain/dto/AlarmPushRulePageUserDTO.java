package com.landleaf.bms.domain.dto;

import lombok.Data;

@Data
public class AlarmPushRulePageUserDTO {
    private Long ruleId;
    private Long userId;
    private String nickname;
    private String dingUrl;
    private Integer pushType;
}
