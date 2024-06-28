package com.landleaf.oauth.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 维护用户默认模块
 *
 * @author 张力方
 * @since 2023/6/2
 **/
@Getter
@AllArgsConstructor
public enum RoleDefaultModuleEnum {
    /*
     * 平台管理员默认模块 - 后台管理模块
     */
    PLATFORM(1, "HTGLMK"),
    /**
     * 租户管理员默认模块 - 后台管理模块
     */
    TENANT(2, "HTGLMK"),
    /**
     * 其他角色 - 综合监控管理系统
     */
    OTHER(3, "ZHJKGLXT"),
    ;

    private final Integer roleType;
    private final String moduleCode;

}
