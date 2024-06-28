package com.landleaf.oauth.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型
 *
 * @author 张力方
 * @since 2023/6/2
 **/
@Getter
@AllArgsConstructor
public enum RoleTypeEnum {
    /*
     * 平台管理员
     */
    PLATFORM((short) 1),
    /**
     * 租户管理员
     */
    TENANT((short) 2),
    /**
     * 其他角色
     */
    OTHER((short) 3),
    ;

    private final Short type;
}
