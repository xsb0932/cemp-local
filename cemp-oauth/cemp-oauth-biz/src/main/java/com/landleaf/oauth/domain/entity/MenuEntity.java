package com.landleaf.oauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_menu")
public class MenuEntity extends TenantBaseEntity {
    /**
     * 菜单权限id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 系统模块id
     */
    @TableField(value = "module_id")
    private Long moduleId;

    /**
     * 系统模块code
     */
    @TableField(value = "module_code")
    private String moduleCode;

    /**
     * 菜单名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 权限标识（校验唯一）
     */
    @TableField(value = "permission")
    private String permission;

    /**
     * 菜单类型（1目录 2菜单 3按钮（预留））
     */
    @TableField(value = "type")
    private String type;

    /**
     * 显示顺序
     */
    @TableField(value = "sort")
    private Long sort;

    /**
     * 路由地址
     */
    @TableField(value = "path")
    private String path;

    /**
     * 父菜单id
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 打开方式
     */
    @TableField(value = "open_with")
    private String openWith;

}