package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.request.ProductPageListRequest;
import com.landleaf.bms.domain.response.ProductResponse;
import com.landleaf.bms.domain.response.RepoProductResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ProductMapper
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {

    Page<RepoProductResponse> pageQueryRepo(@Param("page") Page<RepoProductResponse> page,
                                            @Param("request") ProductPageListRequest request,
                                            @Param("categoryBizIds") List<String> categoryBizIds);

    Page<ProductResponse> pageQueryCustom(@Param("page") Page<ProductResponse> page,
                                          @Param("request") ProductPageListRequest request,
                                          @Param("categoryBizIds") List<String> categoryBizIds,
                                          @Param("tenantId") Long tenantId);

    List<ProductEntity> selectTenantProductList(@Param("tenantId") Long tenantId);
}
