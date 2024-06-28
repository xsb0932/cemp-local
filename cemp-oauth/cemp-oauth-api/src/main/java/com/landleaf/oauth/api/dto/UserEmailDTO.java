package com.landleaf.oauth.api.dto;

import lombok.Data;

@Data
public class UserEmailDTO {
    /**
     * 用户id
     */
    private Long id;
    
    /**
     * 邮箱
     */
    private String email;
}
