package com.landleaf.monitor.domain.enums;

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
     * 设备上报事件
     */
    DEVICE_POST("01", "设备上报事件"),
    /**
     * 设备操作事件
     */
    DEVICE_OP("02", "设备操作事件"),
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
