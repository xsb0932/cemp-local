package com.landleaf.bms.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.ProductAlarmConfEntity;
import com.landleaf.bms.domain.request.ProductAlarmConfQueryRequest;
import com.landleaf.bms.api.dto.ProductAlarmConfListResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ProductAlarmConfMapper
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Mapper
public interface ProductAlarmConfMapper extends ExtensionMapper<ProductAlarmConfEntity> {
    Page<ProductAlarmConfListResponse> pageQuery(@Param("page") Page<ProductAlarmConfListResponse> page,
                                                 @Param("request") ProductAlarmConfQueryRequest request);

}
