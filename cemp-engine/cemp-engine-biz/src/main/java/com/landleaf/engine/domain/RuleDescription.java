package com.landleaf.engine.domain;

import lombok.Data;

@Data
public class RuleDescription {

    /**
     * 规则id
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则状态
     */
    private String status;

    /**
     * 规则描述
     */
    private String ruleDesc;

    /**
     * 设备编号，多个以逗号分隔
     */
    private String bizDeviceIds;

    /**
     * 原设备编号，多个以逗号分隔
     */
    private String orgBizDeviceIds;
}
