package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum RuleTriggerType {
    CONTEXT("01", "报文触发"),
    TIME("02", "时间触发"),
    ;


    private RuleTriggerType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(RuleTriggerType.values()).collect(Collectors.toMap(RuleTriggerType::getCode, RuleTriggerType::getDesc));
    }
}
