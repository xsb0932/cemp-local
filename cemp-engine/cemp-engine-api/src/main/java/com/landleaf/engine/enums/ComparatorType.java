package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum ComparatorType {
    EQ("01", "="),
    NE("02", "≠"),
    GE("03", "≥"),
    LE("04", "≤"),
    GT("05", ">"),
    LT("06", "<"),
    ;

    private ComparatorType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(ComparatorType.values()).collect(Collectors.toMap(ComparatorType::getCode, ComparatorType::getDesc));
    }
}
