package com.landleaf.bms.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class AlarmPushRuleRedisDTO {
    private Long id;
    private List<String> bizProjectIdList;
    private List<String> alarmTypeList;
    private List<String> alarmLevelList;
    private List<String> alarmStatusList;
    private List<Long> emailUserIdList;
    private List<Long> messageUserIdList;
    private List<String> dingUrlList;

}
