package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomDeviceServiceAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceServiceEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;

/**
 * ProductDeviceServiceService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductDeviceServiceService {
    /**
     * 产品-新增自定义设备服务
     *
     * @param request 新增请求参数
     */
    void addCustomDeviceService(CustomDeviceServiceAddRequest request);

    /**
     * 产品-编辑设备服务
     *
     * @param request 编辑设备服务
     */
    void editDeviceService(ProductDeviceServiceEditRequest request);

    /**
     * 产品-删除设备服务
     *
     * @param id 设备服务id
     */
    void deleteDeviceService(Long id);

    /**
     * 产品-分页查询设备服务接口
     *
     * @param request 请求参数
     * @return 设备服务列表
     */
    Page<ProductDeviceServiceListResponse> pageQueryDeviceService(ProductFeatureQueryRequest request);

}
