package com.landleaf.comm.base.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 当前登录用户
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@Data
@Accessors(chain = true)
public class AuthUser {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户状用户状态（0正常 1停用）
     */
    private Short status;
    /**
     * token
     */
    private String token;
    /**
     * 租户id
     */
    private Long tenantId;
}
