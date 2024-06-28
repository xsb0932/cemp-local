package com.landleaf.bms.domain.dto;

import lombok.Data;

@Data
public class ManagementNodeTreeDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点code（校验唯一）
     */
    private String code;

    /**
     * 父业务id
     */
    private String parentBizNodeId;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 节点类型（字典类型-管理节点）
     */
    private String type;

    /**
     * 权限路径path
     */
    private String path;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 管理节点业务id（全局唯一id）
     */
    private String bizNodeId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 项目业务id
     */
    private String bizProjectId;
}
