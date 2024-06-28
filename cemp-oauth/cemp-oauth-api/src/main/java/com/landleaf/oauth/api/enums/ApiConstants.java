package com.landleaf.oauth.api.enums;


import com.landleaf.comm.base.pojo.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * @author 张力方
 */
public class ApiConstants {

    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "cemp-oauth";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX +  "/oauth";

    public static final String VERSION = "1.0.0";

}
