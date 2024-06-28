package com.landleaf.energy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KpiSubtypeSubareaEnum {
    /*
     * 负荷分区
     */
    AREALOAD("1", "areaLoad"),
    /*
     * 用水分区
     */
    AREAWATER("2", "areaWater"),
    ;

    private String kpiTypeCode;

    private String kpiSubtype;

}
