package com.landleaf.energy.domain.enums;

import lombok.Getter;

/**
 * 能源子系统枚举
 *
 * @author Tycoon
 * @since 2023/8/24 13:06
 **/
@Getter
public enum EnergySubSystemEnum {

    /**
     * 光伏
     */
    PHOTOVOLTAIC("PHOTOVOLTAIC", "光伏"),
    /**
     * 充电桩
     */
    CHARGING_STATION("CHARGING_STATION", "充电桩"),
    /**
     * 储能
     */
    ENERGY_STORAGE("ENERGY_STORAGE", "储能"),
    /**
     * 暖通
     */
    HVAC("HVAC", "暖通"),
    /**
     * 热水
     */
    WATER_FEE("WATER_FEE","热水");

    private final String code;
    private final String desc;

    EnergySubSystemEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
