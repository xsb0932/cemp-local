package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.request.ProductParameterChangeRequest;
import com.landleaf.bms.domain.response.ProductParameterTabulationResponse;

/**
 * 产品参数服务
 *
 * @author 张力方
 * @since 2023/6/25
 **/
public interface ProductParameterService {
    Long create(ProductParameterChangeRequest.Create request);

    Long update(ProductParameterChangeRequest.Update request);

    void delete(Long id);

    Page<ProductParameterTabulationResponse> searchProductParameterTabulation(FeatureQueryRequest request);

}
