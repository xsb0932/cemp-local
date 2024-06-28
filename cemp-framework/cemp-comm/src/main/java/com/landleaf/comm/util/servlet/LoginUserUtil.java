package com.landleaf.comm.util.servlet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author Towne
 * @since 2021/8/10 17:19
 */
@Slf4j
public class LoginUserUtil {

    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";

    private LoginUserUtil() {
    }

    public static Long getLoginUserId() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (Objects.isNull(request)) {
            return 0L;
        }
        String userId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.isBlank(userId) || "null".equals(userId)) {
            return 0L;
        }
        return Long.valueOf(userId);
    }

    public static Long getLoginTenantId() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (Objects.isNull(request)) {
            return 0L;
        }
        String tenantId = request.getHeader(HEADER_TENANT_ID);
        if (StringUtils.isBlank(tenantId) || "null".equals(tenantId)) {
            return 0L;
        }
        return Long.valueOf(tenantId);
    }

    public static String getToken() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (Objects.isNull(request)) {
            return null;
        }
        return request.getHeader(AUTHORIZATION);
    }
}
