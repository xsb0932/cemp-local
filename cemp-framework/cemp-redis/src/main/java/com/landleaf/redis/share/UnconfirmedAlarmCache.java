package com.landleaf.redis.share;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnconfirmedAlarmCache {
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


    /**
     * 根据tenantId，和userId,获取缓存
     *
     * @param tenantId
     * @param userId
     * @param t
     * @param <T>
     * @return
     */
    public <T> List<T> getCache(Long tenantId, Long userId, Class<T> t) {
        if (null == userId) {
            userId = 0L;
        }
        Object val = redisUtils.hget(String.format(KeyConstance.UNCONFIRMED_ALARM_COUNT_CACHE, tenantId), String.valueOf(userId));
        if (null != val) {
            JSONObject obj = JSONObject.parse(String.valueOf(val));
            long time = obj.getLong(KEY_TIME);
            if (time < System.currentTimeMillis()) {
                // 已过期
                return null;
            }
            return obj.getJSONArray(KEY_VAL).toJavaList(t);
        }
        return null;
    }

    /**
     * 将缓存放入redis
     *
     * @param tenantId
     * @param userId
     * @param cacheInfo
     */
    public void setCache(Long tenantId, Long userId, List<?> cacheInfo) {
        if (null == cacheInfo) {
            cacheInfo = Lists.newArrayList();
        }
        JSONObject obj = new JSONObject();
        obj.put(KEY_TIME, System.currentTimeMillis() + CACHE_INVALID_TIME * 1000);
        obj.put(KEY_VAL, cacheInfo);
        if (null == userId) {
            userId = 0L;
        }
        redisUtils.hset(String.format(KeyConstance.UNCONFIRMED_ALARM_COUNT_CACHE, tenantId), String.valueOf(userId), JSON.toJSONString(obj));
    }

    /**
     * 清除缓存，当userId为null时，清除当前的tenant的所有unconfirmed缓存的count信息
     *
     * @param tenantId
     * @param userId
     */
    public void clearCache(Long tenantId, Long userId) {
        if (null == userId) {
            // 清除所有
            redisUtils.del(String.format(KeyConstance.UNCONFIRMED_ALARM_COUNT_CACHE, tenantId));
            return;
        }
        redisUtils.hdel(String.format(KeyConstance.UNCONFIRMED_ALARM_COUNT_CACHE, tenantId), String.valueOf(userId));
        return;
    }
}
