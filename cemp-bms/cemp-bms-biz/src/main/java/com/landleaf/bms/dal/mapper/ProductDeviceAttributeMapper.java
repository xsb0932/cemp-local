package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.ProductDeviceAttributeListResponse;
import com.landleaf.bms.domain.entity.ProductDeviceAttributeEntity;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceManagerMonitorAttribute;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Objects;

/**
 * ProductDeviceAttributeMapper
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Mapper
public interface ProductDeviceAttributeMapper extends ExtensionMapper<ProductDeviceAttributeEntity> {
    Page<ProductDeviceAttributeListResponse> pageQuery(@Param("page") Page<ProductDeviceAttributeListResponse> page,
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
        return exists(Wrappers.<ProductDeviceAttributeEntity>lambdaQuery()
                .eq(ProductDeviceAttributeEntity::getIdentifier, identifier)
                .eq(ProductDeviceAttributeEntity::getProductId, productId)
                .ne(Objects.nonNull(id), ProductDeviceAttributeEntity::getId, id));
    }

    @Select("select * from tb_product_device_attribute t1 WHERE t1.deleted = 0 and t1.product_id in (select id from tb_product t1 where t1.deleted = 0 and t1.category_id =#{categoryId} )")
    List<ProductDeviceAttributeEntity> getAttrMap(String categoryId);

    @Select("select identifier from tb_product_device_attribute t1 where t1.deleted = 0 and t1.product_id = #{productId} and t1.data_type = '05'")
    List<String> getEnumAttrs(@Param("productId") Long productId);

    List<DeviceManagerMonitorAttribute> selectDeviceManagerMonitorAttributeListById(@Param("productId") Long productId);
}
