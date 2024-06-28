package com.landleaf.web.interceptor;


import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.tenant.TenantProperties;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户过滤器
 *
 * @author yue lin
 * @since 2023/5/31 13:48
 */
@RequiredArgsConstructor
@Slf4j
public class TenantHandlerInterceptor implements HandlerInterceptor {

    private final TenantProperties tenantProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long tenantId = LoginUserUtil.getLoginTenantId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setIgnore(tenantProperties.isIgnore());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.release();
    }
}
