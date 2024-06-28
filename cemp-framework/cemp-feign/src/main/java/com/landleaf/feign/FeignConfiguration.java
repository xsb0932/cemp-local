package com.landleaf.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @className: FeignConfiguration
 * @description: 下游服务feign拦截器转发traceId
 * @author: Eason
 * @date: 2021/9/22
 **/
@Component
public class FeignConfiguration {
    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            try {
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    requestTemplate.header(HEADER_TRACE_ID, attrs.getRequest().getHeader(HEADER_TRACE_ID));
                    requestTemplate.header(HEADER_USER_ID, attrs.getRequest().getHeader(HEADER_USER_ID));
                    requestTemplate.header(HEADER_TENANT_ID, attrs.getRequest().getHeader(HEADER_TENANT_ID));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
