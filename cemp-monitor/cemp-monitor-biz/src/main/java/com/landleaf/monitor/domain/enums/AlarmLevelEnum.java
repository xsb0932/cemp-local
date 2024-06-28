package com.landleaf.monitor.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AlarmLevelEnum
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Getter
@AllArgsConstructor
public enum AlarmLevelEnum {
    /*
     * 信息
     */
    INFO("00", "信息"),
    /**
     * 提示
     */
    NOTICE("01", "提示"),
    /**
     * 警告
     */
    WARN("02", "警告"),
    /**
     * 故障
     */
    ERROR("03", "故障"),
    ;

    private final String code;
    private final String name;

    public static AlarmLevelEnum fromName(String name) {
        for (AlarmLevelEnum value : AlarmLevelEnum.values()) {
            if (name.equals(value.getName())){
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (AlarmLevelEnum value : AlarmLevelEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
