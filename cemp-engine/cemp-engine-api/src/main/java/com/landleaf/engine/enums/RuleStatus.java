package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum RuleStatus {
    ENABLED("01", "启用"),
    DISABLED("02", "停用"),
    ;

    private RuleStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(RuleStatus.values()).collect(Collectors.toMap(RuleStatus::getCode, RuleStatus::getDesc));
    }
}
