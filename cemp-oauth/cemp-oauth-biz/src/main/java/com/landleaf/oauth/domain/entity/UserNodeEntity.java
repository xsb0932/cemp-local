package com.landleaf.oauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户管理节点
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_user_node")
public class UserNodeEntity extends TenantBaseEntity {
    /**
     * 管理节点id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 节点id
     */
    @TableField(value = "node_id")
    private Long nodeId;

    /**
     * 权限路径path（/landleaf/Shanghai/PJ0001）
     */
    @TableField(value = "path")
    private String path;

    /**
     * 授权类型（1区域 2项目）
     */
    @TableField(value = "type")
    private Short type;

}