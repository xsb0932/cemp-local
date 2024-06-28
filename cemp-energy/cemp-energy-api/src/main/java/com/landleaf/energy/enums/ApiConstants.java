package com.landleaf.energy.enums;

import com.landleaf.comm.base.pojo.enums.RpcConstants;

/**
 * @author Yang
 */
public class ApiConstants {
    /**
     * 服务名
     * <p>
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "cemp-energy";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/energy";

    public static final String VERSION = "1.0.0";
}
