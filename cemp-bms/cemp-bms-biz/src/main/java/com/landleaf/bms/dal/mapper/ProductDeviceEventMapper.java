package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.ProductDeviceEventEntity;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceEventListResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Objects;

/**
 * ProductDeviceEventMapper
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Mapper
public interface ProductDeviceEventMapper extends ExtensionMapper<ProductDeviceEventEntity> {
    Page<ProductDeviceEventListResponse> pageQuery(@Param("page") Page<ProductDeviceEventListResponse> page,
                                                   @Param("request") ProductFeatureQueryRequest request);

    /**
     * 标识符是否存在
     *
     * @param identifier 标识符
     * @param id         id
     * @param productId  产品id
     * @return 结果
     */
    default boolean existsIdentifier(String identifier, Long id, Long productId) {
        return exists(Wrappers.<ProductDeviceEventEntity>lambdaQuery()
                .eq(ProductDeviceEventEntity::getIdentifier, identifier)
                .eq(ProductDeviceEventEntity::getProductId, productId)
                .ne(Objects.nonNull(id), ProductDeviceEventEntity::getId, id));
    }
}
