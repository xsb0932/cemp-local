package com.landleaf.monitor.dal.redis;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.monitor.domain.dto.WeatherDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.landleaf.redis.constance.KeyConstance.WEATHER_CACHE;

/**
 * @author Yang
 */
@Repository
public class WeatherCacheRedisDAO {
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    public void save(Map<String, WeatherDTO> cityWeatherMap) {
        HashOperations<Object, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(WEATHER_CACHE, cityWeatherMap);
    }

    public WeatherDTO getWeatherByWeatherName(String weatherName) {
        Object obj = redisTemplate.opsForHash().get(WEATHER_CACHE, weatherName);
        if (null == obj) {
            return null;
        }
        return JSONObject.from(obj).to(WeatherDTO.class);
    }
}
