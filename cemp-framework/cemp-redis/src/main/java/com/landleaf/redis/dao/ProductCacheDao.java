package com.landleaf.redis.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.dao.dto.DeviceParameterValueCacheDTO;
import com.landleaf.redis.dao.dto.ProductCacheDTO;
import com.landleaf.redis.dao.dto.ProductProductParameterCacheDTO;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Data
@Component
public class ProductCacheDao {
    @Resource
    private RedisTemplate<String, String> redisStringTemplate;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 保存产品信息缓存
     *
     * @param productCacheDTO 产品信息
     */
    public void saveProdInfoCache(ProductCacheDTO productCacheDTO) {
        if (null == productCacheDTO) {
            return;
        }
        redisStringTemplate.opsForHash().put(KeyConstance.PROD_INFO_CACHE, productCacheDTO.getBizId(), JSON.toJSONString(productCacheDTO));
    }

    /**
     * 获取产品信息缓存
     *
     * @param bizId 产品业务id
     * @return 产品信息
     */
    public ProductCacheDTO getProdInfoCache(String bizId) {
        Object cache = redisStringTemplate.opsForHash().get(KeyConstance.PROD_INFO_CACHE, bizId);
        if (null == cache) {
            return null;
        }
        return JSON.parseObject(cache.toString(), ProductCacheDTO.class);
    }

    /**
     * 保存产品参数配置缓存
     *
     * @param bizId                               产品业务id
     * @param productProductParameterCacheDTOList 产品参数配置信息
     */
    public void saveProductParameterValueCache(String bizId, List<ProductProductParameterCacheDTO> productProductParameterCacheDTOList) {
        if (StrUtil.isBlank(bizId) || CollectionUtil.isEmpty(productProductParameterCacheDTOList)) {
            redisStringTemplate.opsForHash().delete(KeyConstance.PRODUCT_PARAMETER_VALUE_CACHE, bizId);
            return;
        }
        redisStringTemplate.opsForHash().put(KeyConstance.PRODUCT_PARAMETER_VALUE_CACHE, bizId, JSON.toJSONString(productProductParameterCacheDTOList));
    }


    /**
     * 获取产品参数配置缓存
     *
     * @param bizId 产品业务id
     * @return 产品参数配置
     */
    public List<ProductProductParameterCacheDTO> getProductParameterValueCache(String bizId) {
        Object cache = redisStringTemplate.opsForHash().get(KeyConstance.PRODUCT_PARAMETER_VALUE_CACHE, bizId);
        if (null == cache) {
            return Collections.emptyList();
        }
        return JSON.parseArray(cache.toString(), ProductProductParameterCacheDTO.class);
    }
}
