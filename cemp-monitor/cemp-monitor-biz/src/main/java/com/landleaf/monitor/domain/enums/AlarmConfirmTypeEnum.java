package com.landleaf.monitor.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AlarmConfirmTypeEnum
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Getter
@AllArgsConstructor
public enum AlarmConfirmTypeEnum {
    /**
     * 自动
     */
    AUTO("00", "自动"),
    /**
     * 手动
     */
    MANUAL("01", "手动"),
    ;

    private final String code;
    private final String name;

    public static AlarmConfirmTypeEnum fromName(String name) {
        for (AlarmConfirmTypeEnum value : AlarmConfirmTypeEnum.values()) {
            if (name.equals(value.getName())){
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (AlarmConfirmTypeEnum value : AlarmConfirmTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
