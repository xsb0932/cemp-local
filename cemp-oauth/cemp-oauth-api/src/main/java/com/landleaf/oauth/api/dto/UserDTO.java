package com.landleaf.oauth.api.dto;

import lombok.Data;

/**
 * @author Yang
 */
@Data
public class UserDTO {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户名
     */
    private String nickname;
}
