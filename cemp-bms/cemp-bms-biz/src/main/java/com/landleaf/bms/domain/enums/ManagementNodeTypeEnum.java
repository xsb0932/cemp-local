package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理节点类型枚举
 * <p>
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Getter
@AllArgsConstructor
public enum ManagementNodeTypeEnum {
    /*
     * 租户企业根节点
     */
    ROOT("01", 1),
    /**
     * 项目
     */
    PROJECT("00", 100),
    ;

    private final String type;
    /**
     * 根据sort决定父子关系
     */
    private final Integer sort;
}
