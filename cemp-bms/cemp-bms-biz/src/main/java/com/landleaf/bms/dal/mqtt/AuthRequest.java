package com.landleaf.bms.dal.mqtt;

import lombok.Data;

/**
 * AuthRequest
 *
 * @author 张力方
 * @since 2023/8/16
 **/
@Data
public class AuthRequest {
    private String username;
    private String password;
}
