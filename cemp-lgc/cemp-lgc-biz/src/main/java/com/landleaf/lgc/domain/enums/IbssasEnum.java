package com.landleaf.lgc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Ibsaas 相关
 *
 * @author xushibai
 * @since 2023/10/12
 **/
@Getter
@AllArgsConstructor
public enum IbssasEnum {
    /**
     * 夏季
     */
    AIR_MODE_1("1", "夏季"),
    /**
     * 除湿
     */
    AIR_MODE_2("2", "除湿"),
    /**
     * 冬季
     */
    AIR_MODE_3("3", "冬季"),
    /**
     * 通风
     */
    AIR_MODE_4("4", "通风"),
    /**
     * 智能
     */
    AIR_MODE_5("5", "智能"),
    ;

    private final String code;
    private final String name;

    public static String getName(String code) {
        for (IbssasEnum value : IbssasEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
