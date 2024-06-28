package com.landleaf.comm.constance;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yang
 */
@Getter
@AllArgsConstructor
public enum DeviceStaCategoryEnum {
    /**
     * KTYKQ-空调遥控器
     * DB3PH-三相电表
     * RQB-燃气表
     * ZNSB-智能水表
     * ZNB-组串逆变器
     * GSCN-储能系统
     */
    KTYKQ("KTYKQ"),
    DB3PH("DB3PH"),
    RQB("RQB"),
    ZNSB("ZNSB"),
    ZNB("ZNB"),
    GSCN("GSCN"),
    ;

    private final String code;

    public static DeviceStaCategoryEnum ofCode(String code) {
        for (DeviceStaCategoryEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("品类类型错误");
    }
}

