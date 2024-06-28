package com.landleaf.bms.domain.enums;

import lombok.Getter;

/**
 * 分区分类
 *
 * @author yue lin
 * @since 2023/7/17 15:41
 */
public enum CnfSubareaTypeEnum {

    // 园区/小区
    PARK("1", "园区/小区"),

    // 楼栋
    BUILDINGS("2","楼栋"),

    // 楼层
    FLOOR("3","楼层"),

    // 其他
    OTHER("4","其他")


    ;

    @Getter
    private final String value;

    @Getter
    private final String label;

    CnfSubareaTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
