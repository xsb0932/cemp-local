package com.landleaf.redis.dict;

import com.alibaba.fastjson2.JSON;
import cn.hutool.core.text.CharSequenceUtil;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.landleaf.redis.constance.KeyConstance.SYSTEM_DICT_DATA;
import static com.landleaf.redis.constance.KeyConstance.TENANT_DICT_DATA;


/**
 * DictUtils
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Component
@RequiredArgsConstructor
public class DictUtils {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 查询字典码值
     * <p>
     * 仅作 redis 查询，不做数据库查询
     *
     * @param dictCode 字典编码
     * @return 结果集
     */
    public List<DictDataEntity> selectDictDataList(String dictCode) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // 查询系统字典
        Boolean hasSystemKey = hashOperations.hasKey(SYSTEM_DICT_DATA, dictCode);
        if (Boolean.TRUE.equals(hasSystemKey)) {
            return JSON.parseArray(hashOperations.get(SYSTEM_DICT_DATA, dictCode), DictDataEntity.class);
        }
        // 查询租户字典
        Long tenantId = TenantContext.getTenantId();
        Boolean hasTenantKey = hashOperations.hasKey(String.format(TENANT_DICT_DATA, tenantId), dictCode);
        if (Boolean.TRUE.equals(hasTenantKey)) {
            return JSON.parseArray(hashOperations.get(String.format(TENANT_DICT_DATA, tenantId), dictCode), DictDataEntity.class);
        }
        return Collections.emptyList();
    }

    /**
     * 查询字典码值描述
     *
     * @param dictCode  字典编码
     * @param dictValue 字典码值
     * @return 结果集
     */
    public String selectDictLabel(String dictCode, String dictValue) {
        return selectDictDataList(dictCode)
                .stream()
                .filter(it -> CharSequenceUtil.equals(it.getValue(), dictValue))
                .map(DictDataEntity::getLabel)
                .findFirst()
                .orElse("");
    }

    /**
     * 查询字典码值描述
     *
     * @param dictCode   字典编码
     * @param dictValues 字典码值
     * @return 结果集
     */
    public List<String> selectDictLabel(String dictCode, List<String> dictValues) {
        List<String> dictLabels = new ArrayList<>();
        List<DictDataEntity> dictDataEntities = selectDictDataList(dictCode);
        for (String dictValue : dictValues) {
            Optional<DictDataEntity> dataEntity = dictDataEntities.stream().filter(item -> item.getValue().equals(dictValue)).findAny();
            dictLabels.add(dataEntity.map(DictDataEntity::getLabel).orElse(null));
        }
        return dictLabels;
    }
}
