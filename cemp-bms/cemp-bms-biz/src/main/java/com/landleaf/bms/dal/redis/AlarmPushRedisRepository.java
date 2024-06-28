package com.landleaf.bms.dal.redis;

import cn.hutool.json.JSONUtil;
import com.landleaf.bms.domain.dto.AlarmPushRuleRedisDTO;
import com.landleaf.redis.constance.KeyConstance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlarmPushRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(Long tenantId, List<AlarmPushRuleRedisDTO> rules) {
        redisTemplate.opsForHash().put(KeyConstance.ALARM_PUSH_RULE, tenantId.toString(), JSONUtil.toJsonStr(rules));
    }

    public List<AlarmPushRuleRedisDTO> getRules(Long tenantId) {
        Object cache = redisTemplate.opsForHash().get(KeyConstance.ALARM_PUSH_RULE, tenantId.toString());
        if (null == cache) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(cache.toString()).toList(AlarmPushRuleRedisDTO.class);
    }

    public void clear(Long tenantId) {
        if (null == tenantId) {
            redisTemplate.delete(KeyConstance.ALARM_PUSH_RULE);
        } else {
            redisTemplate.opsForHash().delete(KeyConstance.ALARM_PUSH_RULE, tenantId.toString());
        }
    }
}
