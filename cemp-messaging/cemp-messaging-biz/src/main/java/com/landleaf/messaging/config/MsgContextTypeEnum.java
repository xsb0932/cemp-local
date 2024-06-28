package com.landleaf.messaging.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息体中的类型
 */
public enum MsgContextTypeEnum {

    PARAMETERS("parameters"),
    PROPERTYS("propertys"),
    ALARM("alarm"),
    CST("CST"),
    SERVICE("service"),
    EVENT("event")
    ;

    MsgContextTypeEnum(String type) {
        this.type = type;
    }

    @Setter
    @Getter
    private String type;
}
