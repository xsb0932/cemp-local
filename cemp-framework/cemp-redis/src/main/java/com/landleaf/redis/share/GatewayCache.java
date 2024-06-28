package com.landleaf.redis.share;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class GatewayCache {
    @Resource
    private RedisUtils redisUtils;

    private static Long CACHE_INVALID_TIME = 5 * 60L;

    /**
     * 存储的过期时间
     */
    private String KEY_TIME = "time";

    /**
     * 存储的值
     */
    private String KEY_VAL = "val";

    public <T> T getCache(String bizGateId, Class<T> t) {
        Object val = redisUtils.hget(KeyConstance.MESSAGING_GATEWAY_CACHE, bizGateId);
        if (null != val) {
            JSONObject obj = JSONObject.parse(String.valueOf(val));
            long time = obj.getLong(KEY_TIME);
            if (time < System.currentTimeMillis()) {
                // 已过期
                return null;
            }
            return obj.getJSONObject(KEY_VAL).toJavaObject(t);
        }
        return null;
    }

    public void setCache(String bizGateId, Object obj) {
        JSONObject cache = new JSONObject();
        cache.put(KEY_TIME, System.currentTimeMillis() + CACHE_INVALID_TIME * 1000);
        cache.put(KEY_VAL, obj);
        redisUtils.hset(KeyConstance.MESSAGING_GATEWAY_CACHE, bizGateId, JSON.toJSONString(cache));
    }

    public void clear(String bizGateId) {
        redisUtils.hdel(KeyConstance.MESSAGING_GATEWAY_CACHE, bizGateId);
        // 删除设备与网关的关联关系。全删，不管他。
        redisUtils.del(KeyConstance.DEVICE_GATEWAY_CACHE);
    }
}

