package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomDeviceEventAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceEventEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceEventListResponse;

/**
 * ProductDeviceEventService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductDeviceEventService {
    /**
     * 产品-新增自定义设备事件
     *
     * @param request 新增请求参数
     */
    void addCustomDeviceEvent(CustomDeviceEventAddRequest request);

    /**
     * 产品-编辑设备事件
     *
     * @param request 编辑设备事件
     */
    void editDeviceEvent(ProductDeviceEventEditRequest request);

    /**
     * 产品-删除设备事件
     *
     * @param id 设备事件id
     */
    void deleteDeviceEvent(Long id);

    /**
     * 产品-分页查询设备事件接口
     *
     * @param request 请求参数
     * @return 设备事件列表
     */
    Page<ProductDeviceEventListResponse> pageQueryDeviceEvent(ProductFeatureQueryRequest request);

}
