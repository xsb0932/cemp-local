package com.landleaf.operatelog.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模块枚举
 *
 * @author 张力方
 * @since 2023/6/13
 **/
@Getter
@AllArgsConstructor
public enum ModuleTypeEnums {
    OAUTH("权限模块"),
    BMS("后端管理模块"),
    ;

    private final String name;
}
