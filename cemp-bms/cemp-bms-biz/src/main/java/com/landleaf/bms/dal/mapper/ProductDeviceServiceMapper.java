package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.domain.entity.ProductDeviceServiceEntity;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceManagerMonitorService;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

/**
 * ProductDeviceServiceMapper
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Mapper
public interface ProductDeviceServiceMapper extends ExtensionMapper<ProductDeviceServiceEntity> {
    Page<ProductDeviceServiceListResponse> pageQuery(@Param("page") Page<ProductDeviceServiceListResponse> page,
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
        return exists(Wrappers.<ProductDeviceServiceEntity>lambdaQuery()
                .eq(ProductDeviceServiceEntity::getIdentifier, identifier)
                .eq(ProductDeviceServiceEntity::getProductId, productId)
                .ne(Objects.nonNull(id), ProductDeviceServiceEntity::getId, id));
    }

    List<DeviceManagerMonitorService> selectDeviceMonitorServiceListById(@Param("productId") Long productId);
}
