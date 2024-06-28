package com.landleaf.monitor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * EventTypeEnum
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Getter
@AllArgsConstructor
public enum EventTypeEnum {
    /*
     * 告警事件
     */
    ALARM_EVENT("01", "告警事件"),
    /**
     * 普通事件
     */
    COMMON_EVENT("02", "普通事件"),
    ;

    private final String code;
    private final String name;

    public static EventTypeEnum fromName(String name) {
        for (EventTypeEnum value : EventTypeEnum.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (EventTypeEnum value : EventTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
