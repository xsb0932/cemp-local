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
public enum GatewayJsStatusEnum {
    /*
     * READY
     */
    READY("READY", "就绪"),
    /**
     * RUN
     */
    RUN("RUN", "启动"),
    /**
     * ERROR
     */
    ERROR("ERROR", "错误"),
    ;

    private final String code;
    private final String name;

    public static GatewayJsStatusEnum fromCode(String code) {
        for (GatewayJsStatusEnum value : GatewayJsStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
