package com.landleaf.gateway.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionConfig implements ErrorWebExceptionHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS;

    static {
        Set<String> exceptions = new HashSet<>();
        exceptions.add("AbortedException");
        exceptions.add("ClientAbortException");
        exceptions.add("EOFException");
        exceptions.add("EofException");
        DISCONNECTED_CLIENT_EXCEPTIONS = Collections.unmodifiableSet(exceptions);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        try {
            if (exchange.getResponse().isCommitted() || isDisconnectedClientError(throwable)) {
                return Mono.error(throwable);
            }
            ServerHttpResponse response = exchange.getResponse();
            ResponseData responseData;

            if (throwable instanceof ResponseStatusException) {
                ResponseStatusException rse = (ResponseStatusException) throwable;

                // 默认抛出是该异常ResponseStatusException，可以设置exchange的attribute来进一步判断
//                转化成一个结构返回体 responseData
            } else {
//                转化成一个结构返回体 responseData
            }

            Response resp = Response.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getMsg());
            DataBuffer dataBuffer = response.bufferFactory()
                    .allocateBuffer().write(MAPPER.writeValueAsString(resp).getBytes());

            response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
            response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
            return response.writeAndFlushWith(Mono.just(ByteBufMono.just(dataBuffer)));
        } catch (JsonProcessingException e) {
            log.error("json处理异常:", e);
            return Mono.error(throwable);
        }
    }

    private boolean isDisconnectedClientError(Throwable ex) {
        return DISCONNECTED_CLIENT_EXCEPTIONS.contains(ex.getClass().getSimpleName())
                || isDisconnectedClientErrorMessage(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
    }

    private boolean isDisconnectedClientErrorMessage(String message) {
        message = (message != null) ? message.toLowerCase() : "";
        return (message.contains("broken pipe") || message.contains("connection reset by peer"));
    }
}