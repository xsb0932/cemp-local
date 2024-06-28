package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 协议类型
 *
 * @author Yang
 */

@Getter
@AllArgsConstructor
public enum GatewayProtocolTypeEnum {
    /*
     * MQTT
     */
    MQTT("01", "MQTT"),
    ;

    private final String code;
    private final String name;

    public static GatewayProtocolTypeEnum fromCode(String code) {
        for (GatewayProtocolTypeEnum value : GatewayProtocolTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
