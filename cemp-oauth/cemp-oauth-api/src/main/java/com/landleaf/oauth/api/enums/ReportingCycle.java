package com.landleaf.oauth.api.enums;

import lombok.Getter;

@Getter
public enum ReportingCycle {
    LABEL_0("0", "自然月"),
    LABEL_6("6", "上月6（不含）到本月6"),
    LABEL_5("5", "上月5（不含）到本月5"),
    LABEL_4("4", "上月4（不含）到本月4"),
    LABEL_3("3", "上月3（不含）到本月3"),
    LABEL_2("2", "上月2（不含）到本月2"),
    LABEL_1("1", "上月1（不含）到本月1"),
    LABEL_28("28", "上月28（不含）到本月28"),
    LABEL_27("27", "上月27（不含）到本月27"),
    LABEL_26("26", "上月26（不含）到本月26"),
    LABEL_25("25", "上月25（不含）到本月25"),
    LABEL_24("24", "上月24（不含）到本月24"),
    LABEL_23("23", "上月23（不含）到本月23"),
    ;

    ReportingCycle(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;
}
