package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-管理节点
 *
 * @author 张力方
 * @since 2023/06/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_user_node")
public class UserNodeEntity extends TenantBaseEntity {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 节点id
     */
    private Long nodeId;

    /**
     * 权限路径path
     */
    private String path;

    /**
     * 授权类型（1区域 2项目）
     */
    private Short type;

    /**
     * 租户id
     */
    private Long tenantId;
}

