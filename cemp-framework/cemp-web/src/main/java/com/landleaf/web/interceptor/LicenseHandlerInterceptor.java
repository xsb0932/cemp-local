package com.landleaf.web.interceptor;


import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.license.LicenseCheck;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.tenant.TenantProperties;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户过滤器
 *
 * @author yue lin
 * @since 2023/5/31 13:48
 */
@RequiredArgsConstructor
@Slf4j
public class LicenseHandlerInterceptor implements HandlerInterceptor {

    private final LicenseCheck licenseCheck;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!licenseCheck.isLegal()) {
            throw new BusinessException(GlobalErrorCodeConstants.LICENSE_LIMIT.getCode(), GlobalErrorCodeConstants.LICENSE_LIMIT.getMsg());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
