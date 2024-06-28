package com.landleaf.comm.sta.enums;

import lombok.Getter;

/**
 * 能耗大类
 *
 * @author xushibai
 * @since 2023/9/23
 **/
@Getter
public enum EnergyTypeEnum {

    /**
     * 光伏
     */
    ENERGY_TYPE_ELECTRICITY("2", "电"),
    /**
     * 充电桩
     */
    ENERGY_TYPE_WATER("1", "水"),
    /**
     * 储能
     */
    ENERGY_TYPE_GAS("3", "气");

    private final String code;
    private final String desc;

    EnergyTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
