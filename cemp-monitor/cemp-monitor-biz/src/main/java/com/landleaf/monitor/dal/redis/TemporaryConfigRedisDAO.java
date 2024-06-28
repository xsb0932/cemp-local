package com.landleaf.monitor.dal.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static com.landleaf.redis.constance.KeyConstance.PRODUCT_ATTR;

/**
 * 前期一些写死的配置，从缓存里取
 *
 * @author Yang
 */
@Repository
public class TemporaryConfigRedisDAO {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Set<String> getAttrCodes(String bizProductId) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        return hashOperations.keys(PRODUCT_ATTR + bizProductId);
    }

    public void initConfig() {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        // 写死安科瑞电表属性code
        String key = PRODUCT_ATTR + "PK00000001";
        hashOperations.put(key, "CST", "通讯状态");
        hashOperations.put(key, "Ua", "a相电压");
        hashOperations.put(key, "Ub", "b相电压");
        hashOperations.put(key, "Uc", "c相电压");
        hashOperations.put(key, "Uab", "ab线电压");
        hashOperations.put(key, "Ubc", "bc线电压");
        hashOperations.put(key, "Uca", "ca线电压");
        hashOperations.put(key, "Ia", "a相电流");
        hashOperations.put(key, "Ib", "b相电流");
        hashOperations.put(key, "Ic", "c相电流");
        hashOperations.put(key, "F", "频率");
        hashOperations.put(key, "P", "有功功率");
        hashOperations.put(key, "Q", "无功功率");
        hashOperations.put(key, "S", "视在功率");
        hashOperations.put(key, "PF", "功率因素");
        hashOperations.put(key, "Epimp", "正向有功总电能");
        hashOperations.put(key, "Epexp", "反向有功总电能");
        hashOperations.put(key, "Eqimp", "正向无功总电能");
        hashOperations.put(key, "Eqexp", "反向无功总电能");

        // 写死安科瑞燃气表属性code
        String key2 = PRODUCT_ATTR + "PK00000002";
        hashOperations.put(key2, "CST", "通讯状态");
        hashOperations.put(key2, "Gascons", "天然气总用量");

        // 写死安科瑞水表属性code
        String key3 = PRODUCT_ATTR + "PK00000003";
        hashOperations.put(key3, "CST", "通讯状态");
        hashOperations.put(key3, "Watercons", "水总用量");
    }


}
