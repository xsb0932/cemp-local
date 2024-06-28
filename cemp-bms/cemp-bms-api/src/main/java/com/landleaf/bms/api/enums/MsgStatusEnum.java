package com.landleaf.bms.api.enums;

import lombok.Getter;

public enum MsgStatusEnum {

    DRAFT("01", "草稿"),
    PUBLISHED("02", "已发布");

    @Getter
    private String type;

    @Getter
    private String desc;

    MsgStatusEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
