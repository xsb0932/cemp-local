package com.landleaf.oauth.dal.redis;

import com.alibaba.fastjson2.JSON;
import com.landleaf.comm.base.pojo.AuthUser;
import com.landleaf.oauth.config.SecurityProperties;
import com.landleaf.oauth.domain.entity.UserEntity;
import com.landleaf.redis.constance.KeyConstance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * TokenRedisDAO
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@Repository
@RequiredArgsConstructor
public class TokenRedisDAO {
    private final StringRedisTemplate stringRedisTemplate;
    private final SecurityProperties securityProperties;

    public AuthUser get(String token) {
        String redisKey = formatKey(token);
        return JSON.parseObject(stringRedisTemplate.opsForValue().get(redisKey), AuthUser.class);
    }

    public void set(AuthUser authUser) {
        String redisKey = formatKey(authUser.getToken());
        stringRedisTemplate.opsForValue().set(redisKey, JSON.toJSONString(authUser), securityProperties.getTokenTimeOut(), TimeUnit.MINUTES);
    }

    public void set(UserEntity user, String token) {
        AuthUser authUser = new AuthUser()
                .setUserId(user.getId())
                .setToken(token)
                .setEmail(user.getEmail())
                .setMobile(user.getMobile())
                .setUsername(user.getUsername())
                .setStatus(user.getStatus())
                .setTenantId(user.getTenantId());
        set(authUser);
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.delete(redisKey);
    }

    private static String formatKey(String token) {
        return String.format(KeyConstance.TOKEN_FORMAT, token);
    }

    /**
     * 踢出租户下所有的用户
     *
     * @param tenantId 租户ID
     */
    public void kickOutTenantUser(Long tenantId) {
        Set<String> keys = stringRedisTemplate.keys(KeyConstance.TOKEN_ALL);
        if (!CollectionUtils.isEmpty(keys)) {
            List<String> tenantKeys = keys.stream()
                    .filter(it -> {
                        AuthUser authUser = JSON.parseObject(stringRedisTemplate.opsForValue().get(it), AuthUser.class);
                        return Objects.nonNull(authUser) && Objects.equals(tenantId, authUser.getTenantId());
                    }).toList();
            stringRedisTemplate.delete(tenantKeys);
        }
    }

    /**
     * 踢出用户
     *
     * @param userId 用户ID
     */
    public void kickOutUser(Long userId) {
        Set<String> keys = stringRedisTemplate.keys(KeyConstance.TOKEN_ALL);
        if (!CollectionUtils.isEmpty(keys)) {
            List<String> tenantKeys = keys.stream()
                    .filter(it -> {
                        AuthUser authUser = JSON.parseObject(stringRedisTemplate.opsForValue().get(it), AuthUser.class);
                        return Objects.nonNull(authUser) && Objects.equals(userId, authUser.getUserId());
                    }).toList();
            stringRedisTemplate.delete(tenantKeys);
        }
    }

}
