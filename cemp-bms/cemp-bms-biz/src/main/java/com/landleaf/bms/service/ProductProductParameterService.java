package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomProductParameterAddRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.request.ProductParameterValueUpdateRequest;
import com.landleaf.bms.domain.request.ProductProductParameterEditRequest;
import com.landleaf.bms.domain.response.ProductProductParameterListResponse;

/**
 * ProductProductParameterService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductProductParameterService {

    /**
     * 产品-新增自定义产品参数
     *
     * @param request 新增请求参数
     */
    void addCustomProductParameter(CustomProductParameterAddRequest request);

    /**
     * 产品-编辑产品参数
     *
     * @param request 编辑产品参数
     */
    void editProductParameter(ProductProductParameterEditRequest request);

    /**
     * 产品-删除产品参数
     *
     * @param id 产品参数id
     */
    void deleteProductParameter(Long id);

    /**
     * 产品-分页查询产品参数接口
     *
     * @param request 请求参数
     * @return 产品参数列表
     */
    Page<ProductProductParameterListResponse> pageQueryProductParameter(ProductFeatureQueryRequest request);

    void updateProductParameterValue(ProductParameterValueUpdateRequest request);
}
