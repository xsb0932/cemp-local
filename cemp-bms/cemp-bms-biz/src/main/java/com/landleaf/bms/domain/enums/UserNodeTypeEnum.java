package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户管理节点-授权类型
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Getter
@AllArgsConstructor
public enum UserNodeTypeEnum {
    /*
     * 区域
     */
    AREA((short) 1),
    /**
     * 项目
     */
    PROJECT((short) 2),
    ;

    private final Short type;
}
