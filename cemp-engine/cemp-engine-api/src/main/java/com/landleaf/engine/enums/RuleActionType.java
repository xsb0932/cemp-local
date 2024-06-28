package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum RuleActionType {
    ALARM("01", "规则告警"),
    COMMAND("02", "控制命令"),
    ;

    private RuleActionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(RuleActionType.values()).collect(Collectors.toMap(RuleActionType::getCode, RuleActionType::getDesc));
    }
}
