package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 功能类型枚举
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Getter
@AllArgsConstructor
public enum FunctionTypeEnum {
    /*
     * 系统默认功能
     */
    SYSTEM_DEFAULT("01"),
    /**
     * 系统可选功能
     */
    SYSTEM_OPTIONAL("02"),
    /**
     * 标准可选功能
     */
    STANDARD_OPTIONAL("03"),
    ;

    private final String value;

}
