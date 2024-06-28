package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典状态
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Getter
@AllArgsConstructor
public enum DictStatusEnum {
    /*
     * 正常
     */
    NORMAL(0),
    /**
     * 失效
     */
    INVALID(1),
    ;

    private final Integer type;
}
