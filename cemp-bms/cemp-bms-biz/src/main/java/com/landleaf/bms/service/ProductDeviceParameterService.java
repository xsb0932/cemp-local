package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomDeviceParameterAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceParameterEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceParameterDetailResponse;
import com.landleaf.bms.api.dto.ProductDeviceParameterListResponse;

import java.util.List;

/**
 * ProductDeviceParameterService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductDeviceParameterService {
    /**
     * 产品-新增自定义设备参数
     *
     * @param request 新增请求参数
     */
    void addCustomDeviceParameter(CustomDeviceParameterAddRequest request);

    /**
     * 产品-编辑设备参数
     *
     * @param request 编辑设备参数
     */
    void editDeviceParameter(ProductDeviceParameterEditRequest request);

    /**
     * 产品-删除设备参数
     *
     * @param id 设备参数id
     */
    void deleteDeviceParameter(Long id);

    /**
     * 产品-分页查询设备参数接口
     *
     * @param request 请求参数
     * @return 设备参数列表
     */
    Page<ProductDeviceParameterListResponse> pageQueryDeviceParameter(ProductFeatureQueryRequest request);

    List<DeviceParameterDetailResponse> listByProduct(String productId);
}
