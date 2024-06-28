package com.landleaf.bms.api.enums;

import lombok.Getter;

public enum MsgTypeEnum {

    SYS("01", "系统消息"),
    MAIL("02", "站内信"),
    NOTICE("03", "公告");

    @Getter
    private String type;

    @Getter
    private String desc;

    MsgTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
