package com.landleaf.gateway.conf;

import cn.hutool.core.util.IdUtil;
import com.landleaf.gateway.security.core.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器
 *
 * @author yue lin
 * @since 2023/5/31 16:42
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // todo 查询当前token下的用户租户ID已经是否胃平台管理员，忽略租户限制
        ServerHttpRequest req = exchange.getRequest().mutate()
                .header(HEADER_TRACE_ID, IdUtil.fastSimpleUUID())
                .header(HEADER_USER_ID, String.valueOf(SecurityFrameworkUtils.getAuthUserId(exchange)))
                .header(HEADER_TENANT_ID, String.valueOf(SecurityFrameworkUtils.getAuthUserTenantId(exchange)))
                .build();
        return chain.filter(exchange.mutate().request(req).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
