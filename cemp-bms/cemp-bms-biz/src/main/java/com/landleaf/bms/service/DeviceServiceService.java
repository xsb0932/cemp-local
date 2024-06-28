package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceServiceChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceServiceTabulationResponse;

/**
 * 设备服务Service
 *
 * @author yue lin
 * @since 2023/6/25 10:03
 */
public interface DeviceServiceService {

    /**
     * 分页查询设备服务列表
     *
     * @param request 参数
     * @return  结果集
     */
    Page<DeviceServiceTabulationResponse> searchDeviceServiceTabulation(FeatureQueryRequest request);

    /**
     * 删除设备服务
     * @param id id
     */
    void deleteDeviceService(Long id);

    /**
     * 创建设备服务
     * @param request 参数
     * @return  id
     */
    Long createDeviceService(DeviceServiceChangeRequest.Create request);

    /**
     * 更新设备服务
     * @param request 参数
     * @return  id
     */
    Long updateDeviceService(DeviceServiceChangeRequest.Update request);
    

}
