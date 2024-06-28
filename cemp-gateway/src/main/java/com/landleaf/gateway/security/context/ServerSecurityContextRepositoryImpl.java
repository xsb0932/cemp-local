package com.landleaf.gateway.security.context;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.landleaf.comm.base.pojo.AuthUser;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.gateway.security.config.SecurityProperties;
import com.landleaf.gateway.security.core.util.SecurityFrameworkUtils;
import com.landleaf.redis.constance.KeyConstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * SecurityContextRepositoryImpl
 *
 * @author 张力方
 * @since 2023/5/31
 **/
@Slf4j
public class ServerSecurityContextRepositoryImpl implements ServerSecurityContextRepository {

    private final SecurityProperties securityProperties;

    public ServerSecurityContextRepositoryImpl(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        log.info("========start load ================");

        boolean anyMatch = securityProperties.getPermitAllUrls()
                .stream()
                .anyMatch(it -> exchange.getRequest().getURI().getPath().contains(it));
        if (anyMatch) {
            return Mono.empty();
        }

        ServerHttpResponse response = exchange.getResponse();
        StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
        ServerHttpRequest request = exchange.getRequest();
        response.setStatusCode(HttpStatus.OK);
        // 获取请求头中的token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String redisKey = String.format(KeyConstance.TOKEN_FORMAT, token);
        if (StringUtils.isNotBlank(redisKey)) {
            // 取用户信息(redis)
            Boolean isExpire = stringRedisTemplate.hasKey(redisKey);
            // 判断token是否过期
            if (Boolean.TRUE.equals(isExpire)) {
                // 用token去redis取令牌
                String tokenInfo = stringRedisTemplate.opsForValue().get(redisKey);
                AuthUser authUser = JSON.parseObject(tokenInfo, AuthUser.class);
                Assert.notNull(authUser, "获取token异常");
                SecurityFrameworkUtils.setLoginUser(exchange, authUser);
                // 刷新缓存时间 不再刷新过期时间
//                stringRedisTemplate.expire(redisKey, securityProperties.getTokenTimeOut(), TimeUnit.MINUTES);
                // 把令牌传给Security
                SecurityContext emptyContext = new SecurityContextImpl();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser, null, null);
                emptyContext.setAuthentication(authentication);
                return Mono.just(emptyContext);
            } else {
                DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONBytes(Response.error(GlobalErrorCodeConstants.UNAUTHORIZED)));
                // 返回token已过期
                response.writeWith(Mono.just(dataBuffer));
                return Mono.empty();
            }
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONBytes(Response.error(GlobalErrorCodeConstants.UNAUTHORIZED)));
        response.writeWith(Mono.just(dataBuffer));
        return Mono.empty();
    }
}
