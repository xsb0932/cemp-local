package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum RuleConditionType {
    TIME("01", "时间条件"),
    DEVICE("02", "设备条件"),
    ;

    private RuleConditionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(RuleConditionType.values()).collect(Collectors.toMap(RuleConditionType::getCode, RuleConditionType::getDesc));
    }
}
