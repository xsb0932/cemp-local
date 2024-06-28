package com.landleaf.energy.domain.enums;

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
public enum RegionTypeEnum {
    /*
     * 客房
     */
    REGION_TYPE_1("1", "客房"),
    /**
     * 大厅
     */
    REGION_TYPE_2("2", "大堂"),
    /**
     * 大厅
     */
    REGION_TYPE_3("3", "过道"),
    ;

    private final String code;
    private final String name;

    public static RegionTypeEnum fromName(String name) {
        for (RegionTypeEnum value : RegionTypeEnum.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (RegionTypeEnum value : RegionTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
