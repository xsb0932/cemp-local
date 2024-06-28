package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 网关状态
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Getter
@AllArgsConstructor
public enum GatewayStatusEnum {
    /*
     * 启动
     */
    RUNNING("01", "启动"),
    /**
     * 停止
     */
    STOP("02", "停止"),
    ;

    private final String code;
    private final String name;

    public static GatewayStatusEnum fromCode(String code) {
        for (GatewayStatusEnum value : GatewayStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
