package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理节点 entity
 *
 * @author 张力方
 * @since 2023/06/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_management_node")
public class ManagementNodeEntity extends TenantBaseEntity {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
}

