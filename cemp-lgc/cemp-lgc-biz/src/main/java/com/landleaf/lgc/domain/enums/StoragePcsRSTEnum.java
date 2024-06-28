package com.landleaf.lgc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * StoragePcsRSTEnum
 *
 * @author 张力方
 * @since 2023/8/23
 **/
@Getter
@AllArgsConstructor
public enum StoragePcsRSTEnum {
    /**
     * 关机
     */
    OFF("0", "关机"),
    /**
     * 待机
     */
    STANDBY("1", "待机"),
    /**
     * 充电
     */
    IN_CHARGE("2", "充电"),
    /**
     * 放电
     */
    DIS_CHARGE("3", "放电"),
    ;

    private final String code;
    private final String name;

    public static String getName(String code) {
        for (StoragePcsRSTEnum value : StoragePcsRSTEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
