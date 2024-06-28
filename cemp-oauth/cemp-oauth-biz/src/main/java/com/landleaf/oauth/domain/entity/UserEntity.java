package com.landleaf.oauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_user")
public class UserEntity extends TenantBaseEntity {
    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 盐
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 用户名
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 电话
     */
    @TableField(value = "mobile")
    private String mobile;

    /**
     * 用户状态（0正常 1停用）
     */
    @TableField(value = "status")
    private Short status;

}
