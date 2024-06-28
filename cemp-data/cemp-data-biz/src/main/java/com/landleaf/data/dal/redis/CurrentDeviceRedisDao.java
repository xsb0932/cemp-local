package com.landleaf.data.dal.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.data.constants.AlarmCodeConstance;
import com.landleaf.data.constants.AlarmStatusEnum;
import com.landleaf.data.constants.MsgContextTypeEnum;
import com.landleaf.redis.constance.KeyConstance;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS;

/**
 * @author Yang
 */
@Repository
public class CurrentDeviceRedisDao {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> getCurrent(String id) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(KeyConstance.DEVICE_CURRENT_STATUS_V1 + id))) {
            // 从新的拿
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> deviceCurrentStatus = hashOperations.entries(KeyConstance.DEVICE_CURRENT_STATUS_V1 + id);
            // 按照原来的逻辑，这部分应该只取property
            Map<String, Object> propertys = (Map<String, Object>) deviceCurrentStatus.get(MsgContextTypeEnum.PROPERTYS.getType());
            if (null != propertys) {
                propertys.forEach((k, v) -> {
                    Map<String, Object> temp = (Map<String, Object>) v;
                    if (temp.containsKey("val")) {
                        result.put(k, temp.get("val"));
                    }
                });
            }
            Map<String, Object> cstInfo = (Map<String, Object>) deviceCurrentStatus.get("CST");
            if (null != cstInfo) {
                result.put("CST", cstInfo.get("val"));
                result.put("uploadTime", cstInfo.get("time"));
            }
            return result;
        }

        return hashOperations.entries(DEVICE_CURRENT_STATUS + id);
    }

    public Map<String, Integer> getDeviceCstMap(List<String> bizDeviceIds) {
        Map<String, Integer> result = new HashMap<>();
        if (CollUtil.isEmpty(bizDeviceIds)) {
            return result;
        }
        for (String bizDeviceId : bizDeviceIds) {
            HashOperations<String, Object, Object> hashOperation = redisTemplate.opsForHash();
            String keyV1 = KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId;
            if (Boolean.TRUE.equals(hashOperation.hasKey(keyV1, "CST"))) {
                Object cst = hashOperation.get(keyV1, "CST");
                result.put(bizDeviceId, null == cst ? 0 : JSONUtil.parseObj(cst).getInt("val"));
            } else {
                String key = KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId;
                if (Boolean.TRUE.equals(hashOperation.hasKey(key, "CST"))) {
                    Object cst = hashOperation.get(key, "CST");
                    result.put(bizDeviceId, null == cst ? 0 : Integer.parseInt(cst.toString()));
                } else {
                    result.put(bizDeviceId, 0);
                }
            }
        }
        return result;
    }

    public Integer getDeviceCstStatus(String bizDeviceId) {
        HashOperations<String, Object, Object> hashOperation = redisTemplate.opsForHash();
        String keyV1 = KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId;
        if (Boolean.TRUE.equals(hashOperation.hasKey(keyV1, "CST"))) {
            Object cst = hashOperation.get(keyV1, "CST");
            return null == cst ? 0 : JSONUtil.parseObj(cst).getInt("val");
        } else {
            String key = KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId;
            if (Boolean.TRUE.equals(hashOperation.hasKey(key, "CST"))) {
                Object cst = hashOperation.get(key, "CST");
                return null == cst ? 0 : Integer.parseInt(cst.toString());
            } else {
                return 0;
            }
        }
    }

    public List<String> getCurrentAlarmCode(String bizDeviceId) {
        String key = KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId;
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        Map<String, Object> deviceCurrentStatus = hashOperations.entries(key);
        List<String> codeList = new ArrayList<>();
        if (null != deviceCurrentStatus && deviceCurrentStatus.containsKey(MsgContextTypeEnum.ALARM.getType())) {
            // 按照原来的逻辑，这部分应该只取property
            Map<String, Object> alarm = (Map<String, Object>) deviceCurrentStatus.get(MsgContextTypeEnum.ALARM.getType());
            if (null != alarm) {
                alarm.forEach((k, v) -> {
                    if (!AlarmCodeConstance.CONN_ALARM_CODE.equals(k)) {
                        Map<String, Object> temp = (Map<String, Object>) v;
                        if (temp.containsKey("val")) {
                            if (AlarmStatusEnum.TRIGGER_RESET.getCode().equals(String.valueOf(temp.get("val")))) {
                                codeList.add(k);
                            }
                        }
                    }
                });
            }
        }
        return codeList;
    }
}