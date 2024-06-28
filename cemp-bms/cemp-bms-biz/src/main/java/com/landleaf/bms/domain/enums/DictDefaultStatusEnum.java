package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典默认状态
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Getter
@AllArgsConstructor
public enum DictDefaultStatusEnum {
    /*
     * 默认
     */
    DEFAULT(0),
    /**
     * 非默认
     */
    NOT_DEFAULT(1),
    ;

    private final Integer type;
}
