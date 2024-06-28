package com.landleaf.oauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_role")
public class RoleEntity extends TenantBaseEntity {
    /**
     * 角色id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 角色状态（0正常 1停用）
     */
    @TableField(value = "status")
    private Short status;

    /**
     * 角色类型（1平台管理员 2租户管理员 3普通角色）
     */
    @TableField(value = "type")
    private Short type;
}
