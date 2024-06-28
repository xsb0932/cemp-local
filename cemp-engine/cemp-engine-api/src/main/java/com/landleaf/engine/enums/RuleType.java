package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum RuleType {

    ALARM("01", "告警规则"),
    COMMAND("02", "指令规则"),
    ;

    private RuleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(RuleType.values()).collect(Collectors.toMap(RuleType::getCode, RuleType::getDesc));
    }
}
