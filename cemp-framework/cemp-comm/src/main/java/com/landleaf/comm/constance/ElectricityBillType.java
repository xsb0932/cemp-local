package com.landleaf.comm.constance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ElectricityBillType {
    VALLEY("valley", "谷时段"),
    FLAT("flat", "平时段"),
    PEAK("peak", "峰时段"),
    TIP("tip", "尖时段"),
    ;
    private String type;

    private String desc;
}
