package com.landleaf.monitor.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * EventTypeEnum
 *
 * @author xushibai
 * @since 2023/9/14
 **/
@Getter
@AllArgsConstructor
public enum ModeTypeEnum {
    /*
     * 手动模式
     */
    MODE_1("1", "手动模式"),
    /**
     * 夏季模式
     */
    MODE_2("2", "夏季模式"),
    /**
     * 冬季模式
     */
    MODE_3("3", "冬季模式"),
    ;

    private final String code;
    private final String name;

    public static ModeTypeEnum fromName(String name) {
        for (ModeTypeEnum value : ModeTypeEnum.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (ModeTypeEnum value : ModeTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
