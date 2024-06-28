package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.ProductProductParameterEntity;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.response.ProductProductParameterListResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Objects;

/**
 * ProductProductParameterMapper
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Mapper
public interface ProductProductParameterMapper extends ExtensionMapper<ProductProductParameterEntity> {

    Page<ProductProductParameterListResponse> pageQuery(@Param("page") Page<ProductProductParameterListResponse> page,
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
        return exists(Wrappers.<ProductProductParameterEntity>lambdaQuery()
                .eq(ProductProductParameterEntity::getIdentifier, identifier)
                .eq(ProductProductParameterEntity::getProductId, productId)
                .ne(Objects.nonNull(id), ProductProductParameterEntity::getId, id));
    }

}
