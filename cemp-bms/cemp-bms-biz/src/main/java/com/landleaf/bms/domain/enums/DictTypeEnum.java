package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DictTypeEnum
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Getter
@AllArgsConstructor
public enum DictTypeEnum {
    /*
     * 系统字典
     */
    SYSTEM(1, "系统字典"),
    /**
     * 用户字典
     */
    TENANT(2, "租户字典"),
    ;

    private final Integer type;
    private final String name;
}
