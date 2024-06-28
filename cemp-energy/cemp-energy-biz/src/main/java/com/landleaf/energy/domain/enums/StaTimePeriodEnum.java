package com.landleaf.energy.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * StaTimePeriodEnum
 *
 **/
@Getter
@AllArgsConstructor
public enum StaTimePeriodEnum {

    HOUR("1", "统计-小时"),
    DAY("2", "统计-天"),
    MONTH("3", "统计-月"),
    YEAR("4", "统计-年"),
    ;

    private final String type;
    private final String name;
}
