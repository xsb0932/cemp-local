package com.landleaf.monitor.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AlarmStatusEnum
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Getter
@AllArgsConstructor
public enum AlarmStatusEnum {
    /*
     * 触发-无复归
     */
    TRIGGER_NO_RESET("0", "复归"),
    /**
     * 触发-需复归
     */
    TRIGGER_RESET("1", "触发"),
    /**
     * 复归
     */
    RESET("2", "--"),
    ;

    private final String code;
    private final String name;

    public static AlarmStatusEnum fromName(String name) {
        for (AlarmStatusEnum value : AlarmStatusEnum.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (AlarmStatusEnum value : AlarmStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
