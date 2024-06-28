package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomDeviceAttributeAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceAttributeEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceAttributeListResponse;

/**
 * ProductDeviceAttributeService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductDeviceAttributeService {
    /**
     * 产品-新增自定义设备属性
     *
     * @param request 新增请求参数
     */
    void addCustomDeviceAttribute(CustomDeviceAttributeAddRequest request);

    /**
     * 产品-编辑设备属性
     *
     * @param request 编辑设备属性
     */
    void editDeviceAttribute(ProductDeviceAttributeEditRequest request);

    /**
     * 产品-删除设备属性
     *
     * @param id 设备属性id
     */
    void deleteDeviceAttribute(Long id);

    /**
     * 产品-分页查询设备属性接口
     *
     * @param request 请求参数
     * @return 设备属性列表
     */
    Page<ProductDeviceAttributeListResponse> pageQueryDeviceAttribute(ProductFeatureQueryRequest request);
}
