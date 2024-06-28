package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品状态枚举
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Getter
@AllArgsConstructor
public enum ProductStatusEnum {
    /*
     * 已发布
     */
    RELEASE(0),
    /**
     * 未发布
     */
    NOT_RELEASE(1),
    ;

    private final Integer type;
}
