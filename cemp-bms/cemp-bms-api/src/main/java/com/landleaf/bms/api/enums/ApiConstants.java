package com.landleaf.bms.api.enums;


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
    public static final String NAME = "cemp-bms";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX +  "/bms";


}
