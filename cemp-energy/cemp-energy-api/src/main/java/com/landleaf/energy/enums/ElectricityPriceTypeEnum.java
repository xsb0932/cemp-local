package com.landleaf.energy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ElectricityPriceTypeEnum {
    /*
     * 固定价格
     */
    FIXED_PRICE("01", "固定价格"),
    /*
     * 分时电价
     */
    TOU("02", "分时电价"),
    ;

    private String type;

    private String desc;

}
