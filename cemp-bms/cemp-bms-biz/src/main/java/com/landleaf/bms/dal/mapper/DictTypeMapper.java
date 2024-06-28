package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.domain.entity.DictTypeEntity;
import com.landleaf.bms.domain.enums.DictTypeEnum;
import com.landleaf.bms.domain.request.DictTypeListRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Objects;

/**
 * DictTypeMapper
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Mapper
public interface DictTypeMapper extends BaseMapper<DictTypeEntity> {

    /**
     * 查询系统字典类型
     *
     * @return 结果
     */
    default List<DictTypeEntity> searchSystemDictTypes(DictTypeListRequest request) {
        String keywords = request == null ? null : request.getKeywords();
        return selectList(Wrappers.<DictTypeEntity>lambdaQuery()
                .eq(DictTypeEntity::getType, DictTypeEnum.SYSTEM.getType())
                .and(keywords != null, it -> it.like(keywords != null, DictTypeEntity::getName, keywords)
                        .or().like(keywords != null, DictTypeEntity::getCode, keywords)));
    }

    /**
     * 查询租户字典类型
     *
     * @param tenantId 租户ID（为空则查询所有租户）
     * @return 结果
     */
    default List<DictTypeEntity> searchTenantDictTypes(Long tenantId, String keywords) {
        return selectList(Wrappers.<DictTypeEntity>lambdaQuery()
                .eq(DictTypeEntity::getType, DictTypeEnum.TENANT.getType())
                .eq(Objects.nonNull(tenantId), DictTypeEntity::getTenantId, tenantId)
                .and(keywords != null, it -> it.like(keywords != null, DictTypeEntity::getName, keywords)
                        .or().like(keywords != null, DictTypeEntity::getCode, keywords))
        );
    }

    /**
     * 查询字典所有的在使用租户ID
     *
     * @return 结果
     */
    default List<Long> searchTenantIds() {
        return selectList(Wrappers.<DictTypeEntity>lambdaQuery().select(DictTypeEntity::getTenantId))
                .stream()
                .map(DictTypeEntity::getTenantId)
                .distinct()
                .toList();
    }

}
