package com.landleaf.bms.domain.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmStatusEnum {
    ZERO("0", "复归"),
    ONE("1", "触发"),
    TWO("2", "--"),
    ;

    private final String code;
    private final String name;

    public static String codeToName(String code) {
        for (AlarmStatusEnum o : values()) {
            if (StrUtil.equals(o.getCode(), code)) {
                return o.name;
            }
        }
        return null;
    }
}
