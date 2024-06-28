package com.landleaf.energy.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * StaTimePeriodEnum
 *
 **/
@Getter
@AllArgsConstructor
public enum DeviceComputeEnum {

    ADD("1", "+"),
    SUB("-1", "-"),
    ;

    private final String type;
    private final String name;
}
