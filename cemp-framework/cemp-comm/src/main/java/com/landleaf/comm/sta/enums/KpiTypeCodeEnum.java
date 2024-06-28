package com.landleaf.comm.sta.enums;

import lombok.Getter;

/**
 * KPI 指标大类
 *
 * @author xushibai
 * @since 2023/10/17
 **/
@Getter
public enum KpiTypeCodeEnum {

    /**
     * 电
     */
    KPI_TYPE_ELECTRICITY("1", "电"),
    /**
     * 水
     */
    KPI_TYPE_WATER("2", "水"),
    /**
     * 气
     */
    KPI_TYPE__GAS("3", "气"),
    /**
     * 碳
     */
    KPI_TYPE_CARBON("4", "碳");

    private final String code;
    private final String desc;

    KpiTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
