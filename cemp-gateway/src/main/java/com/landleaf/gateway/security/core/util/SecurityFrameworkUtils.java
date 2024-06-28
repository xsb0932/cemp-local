package com.landleaf.gateway.security.core.util;

import cn.hutool.core.map.MapUtil;
import com.landleaf.comm.base.pojo.AuthUser;
import org.springframework.web.server.ServerWebExchange;

/**
 * 安全服务工具类
 *
 * @author 张力方
 */
public class SecurityFrameworkUtils {
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private SecurityFrameworkUtils() {
    }

    /**
     * 设置登录用户
     *
     * @param exchange 请求
     * @param user 用户
     */
    public static void setLoginUser(ServerWebExchange exchange, AuthUser user) {
        exchange.getAttributes().put(HEADER_USER_ID, user.getUserId());
        exchange.getAttributes().put(HEADER_TENANT_ID, user.getTenantId());
    }

    /**
     * 获得登录用户的编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getAuthUserId(ServerWebExchange exchange) {
        return MapUtil.getLong(exchange.getAttributes(), HEADER_USER_ID);
    }

    /**
     * 获得登录用户的租户编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getAuthUserTenantId(ServerWebExchange exchange) {
        return MapUtil.getLong(exchange.getAttributes(), HEADER_TENANT_ID);
    }

}
