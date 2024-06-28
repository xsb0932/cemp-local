package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.ProductDeviceParameterListResponse;
import com.landleaf.bms.domain.entity.ProductDeviceParameterEntity;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceManagerMonitorProperty;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Objects;

/**
 * ProductDeviceParameterMapper
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Mapper
public interface ProductDeviceParameterMapper extends ExtensionMapper<ProductDeviceParameterEntity> {
    Page<ProductDeviceParameterListResponse> pageQuery(@Param("page") Page<ProductDeviceParameterListResponse> page,
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
        return exists(Wrappers.<ProductDeviceParameterEntity>lambdaQuery()
                .eq(ProductDeviceParameterEntity::getIdentifier, identifier)
                .eq(ProductDeviceParameterEntity::getProductId, productId)
                .ne(Objects.nonNull(id), ProductDeviceParameterEntity::getId, id));
    }

    @Select("select * from tb_product_device_parameter where deleted = 0 and product_id = #{productId} and identifier = #{code} limit 1")
    ProductDeviceParameterEntity getParameter(@Param("productId") Long productId, @Param("code") String code);

    @Select("select * from tb_product_device_parameter where product_id = #{productId} and function_type != '01' and deleted = 0")
    List<ProductDeviceParameterEntity> getParameters(@Param("productId") Long productId);


    List<DeviceManagerMonitorProperty> selectDeviceManagerMonitorPropertyListById(@Param("productId") Long productId);
}
