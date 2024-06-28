package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum RepeatType {
    REPEAT_WEEK("01", "按周重复"),
    REPEAT_MONTH("02", "按月重复"),
    ;

    private RepeatType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(RepeatType.values()).collect(Collectors.toMap(RepeatType::getCode, RepeatType::getDesc));
    }
}
