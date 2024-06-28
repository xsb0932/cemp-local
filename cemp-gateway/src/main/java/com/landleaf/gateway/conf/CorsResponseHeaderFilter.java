package com.landleaf.gateway.conf;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {
    static List<String> list = new ArrayList<String>();

    static {
        list.add("*");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("========start cors filter ================");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            exchange.getResponse().getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            exchange.getResponse().getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            exchange.getResponse().getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
            exchange.getResponse().getHeaders().set("Access-Control-Allow-Private-Network", "true");
            exchange.getResponse().getHeaders().entrySet().stream()
                    .filter(kv -> (kv.getKey() != null && kv.getValue().size() > 1))
                    .filter(kv -> (kv.getKey().equals(HttpHeaders.VARY)))
                    .forEach(kv ->
                    {
                        // Vary只需要去重即可
                        if (kv.getKey().equals(HttpHeaders.VARY))
                            kv.setValue(kv.getValue().stream().distinct().collect(Collectors.toList()));
                    });

        }));
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
