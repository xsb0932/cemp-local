package com.landleaf.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum TargetMessageType {
    ATTR("01", "属性报文"),
    EVENT("02", "事件报文"),
    ;

    private TargetMessageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public static Map<String, String> getDescMap() {
        return Arrays.stream(TargetMessageType.values()).collect(Collectors.toMap(TargetMessageType::getCode, TargetMessageType::getDesc));
    }
}
