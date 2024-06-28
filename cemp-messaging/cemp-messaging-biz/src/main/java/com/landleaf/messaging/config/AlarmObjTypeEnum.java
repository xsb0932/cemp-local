package com.landleaf.messaging.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AlarmObjTypeEnum
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Getter
@AllArgsConstructor
public enum AlarmObjTypeEnum {
    /*
     * 设备
     */
    DEVICE("01", "设备"),
    /**
     * 规则（工艺）
     */
    RULE("02", "规则（工艺）"),
    /**
     * 软网关
     */
    GATEWAY("03", "软网关"),
    ;

    private final String code;
    private final String name;

    public static AlarmObjTypeEnum fromName(String name) {
        for (AlarmObjTypeEnum value : AlarmObjTypeEnum.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (AlarmObjTypeEnum value : AlarmObjTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
