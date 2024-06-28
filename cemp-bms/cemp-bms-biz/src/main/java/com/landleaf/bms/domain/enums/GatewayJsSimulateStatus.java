package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 网关js模拟运行状态
 *
 * @author Yang
 */

@Getter
@AllArgsConstructor
public enum GatewayJsSimulateStatus {
    /*
     * SUCCESS
     */
    SUCCESS("01", "成功"),
    /**
     * FAILED
     */
    FAILED("02", "失败"),
    ;

    private final String code;
    private final String name;

    public static GatewayJsSimulateStatus fromCode(String code) {
        for (GatewayJsSimulateStatus value : GatewayJsSimulateStatus.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
