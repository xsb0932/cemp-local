package com.landleaf.bms.domain.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RuleStatusEnum {
    ENABLE("01", "启用"),
    DISABLE("02", "停用"),
    ;

    private final String code;
    private final String name;

    public static String codeToName(String code) {
        for (RuleStatusEnum o : values()) {
            if (StrUtil.equals(o.getCode(), code)) {
                return o.name;
            }
        }
        return null;
    }
}
