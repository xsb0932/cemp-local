package com.landleaf.bms.dal.redis;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.landleaf.bms.dal.mapper.DictDataMapper;
import com.landleaf.bms.dal.mapper.DictTypeMapper;
import com.landleaf.bms.domain.entity.DictDataEntity;
import com.landleaf.bms.domain.entity.DictTypeEntity;
import com.landleaf.bms.domain.enums.DictTypeEnum;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static com.landleaf.redis.constance.KeyConstance.SYSTEM_DICT_DATA;
import static com.landleaf.redis.constance.KeyConstance.TENANT_DICT_DATA;

/**
 * DictRedisDao
 *
 * @author 张力方
 * @since 2023/6/19
 **/
@Repository
@RequiredArgsConstructor
public class DictRedisDAO {
    private final RedisTemplate<String, String> redisTemplate;
    private final DictDataMapper dictDataMapper;
    private final DictTypeMapper dictTypeMapper;

    /**
     * 查询类型下的字典数据
     *
     * @return 结果集
     */
    public List<DictDataEntity> searchDictDataList(DictTypeEntity dictType) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        if (dictType.getType() == 1) {
            // 查询系统字典
            Boolean hasSystemKey = hashOperations.hasKey(SYSTEM_DICT_DATA, dictType.getCode());
            if (Boolean.TRUE.equals(hasSystemKey)) {
                return JSON.parseArray(hashOperations.get(SYSTEM_DICT_DATA, dictType.getCode()), DictDataEntity.class);
            }
        } else if (dictType.getType() == 2) {
            // 查询租户字典
            Long tenantId = TenantContext.getTenantId();
            Boolean hasTenantKey = hashOperations.hasKey(String.format(TENANT_DICT_DATA, tenantId), dictType.getCode());
            if (Boolean.TRUE.equals(hasTenantKey)) {
                return JSON.parseArray(hashOperations.get(String.format(TENANT_DICT_DATA, tenantId), dictType.getCode()), DictDataEntity.class);
            }
        }
        return Collections.emptyList();

    }

    @PostConstruct
    public void reloadDictRedisCache() {
        TenantContext.setIgnore(true);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        List<DictTypeEntity> dictTypeEntityList = dictTypeMapper.selectList(null);
        List<DictDataEntity> dictDataEntityList = dictDataMapper.selectList(new QueryWrapper<DictDataEntity>().lambda().orderByAsc(DictDataEntity::getSort));
        List<DictTypeEntity> systemDictTypeList = dictTypeEntityList.stream().filter(item -> item.getType().equals(DictTypeEnum.SYSTEM.getType())).toList();
        List<DictTypeEntity> tenantDictTypeList = dictTypeEntityList.stream().filter(item -> item.getType().equals(DictTypeEnum.TENANT.getType())).toList();
        for (DictTypeEntity dictTypeEntity : systemDictTypeList) {
            List<DictDataEntity> dictDataEntities = dictDataEntityList.stream().filter(item -> item.getDictId().equals(dictTypeEntity.getId())).toList();
            if (CollUtil.isNotEmpty(dictDataEntities)) {
                String code = dictTypeEntity.getCode();
                Boolean hasKey = hashOperations.hasKey(SYSTEM_DICT_DATA, code);
                if (Boolean.TRUE.equals(hasKey)) {
                    hashOperations.delete(SYSTEM_DICT_DATA, code);
                }
                hashOperations.putIfAbsent(SYSTEM_DICT_DATA, code, JSON.toJSONString(dictDataEntities));
            }
        }
        for (DictTypeEntity dictTypeEntity : tenantDictTypeList) {
            List<DictDataEntity> dictDataEntities = dictDataEntityList.stream().filter(item -> item.getDictId().equals(dictTypeEntity.getId())).toList();
            if (CollUtil.isNotEmpty(dictDataEntities)) {
                String code = dictTypeEntity.getCode();
                Boolean hasKey = hashOperations.hasKey(String.format(TENANT_DICT_DATA, dictTypeEntity.getTenantId()), code);
                if (Boolean.TRUE.equals(hasKey)) {
                    hashOperations.delete(String.format(TENANT_DICT_DATA, dictTypeEntity.getTenantId()), code);
                }
                hashOperations.putIfAbsent(String.format(TENANT_DICT_DATA, dictTypeEntity.getTenantId()), code, JSON.toJSONString(dictDataEntities));
            }
        }
    }

    @Deprecated
    public void reloadDictRedisCache(String dictCode) {
        // 这边原本的逻辑感觉像是想单独刷新某个租户的字典缓存 但是存在bug 不想细看了 统一改成上面的全部reload
        Long tenantId = TenantContext.getTenantId();
        List<DictDataEntity> dictDataEntities = dictDataMapper.searchByDictCode(dictCode);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // 查询系统字典
        Boolean hasSystemKey = hashOperations.hasKey(SYSTEM_DICT_DATA, dictCode);
        if (Boolean.TRUE.equals(hasSystemKey)) {
            hashOperations.delete(SYSTEM_DICT_DATA, dictCode);
        }
        hashOperations.putIfAbsent(SYSTEM_DICT_DATA, dictCode, JSON.toJSONString(dictDataEntities));
        // 查询租户字典
        Boolean hasTenantKey = hashOperations.hasKey(String.format(TENANT_DICT_DATA, tenantId), dictCode);
        if (Boolean.TRUE.equals(hasTenantKey)) {
            hashOperations.delete(String.format(TENANT_DICT_DATA, tenantId), dictCode);
        }
        hashOperations.putIfAbsent(String.format(TENANT_DICT_DATA, tenantId), dictCode, JSON.toJSONString(dictDataEntities));
    }
}
