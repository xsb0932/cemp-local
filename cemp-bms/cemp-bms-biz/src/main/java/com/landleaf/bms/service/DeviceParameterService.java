package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceParameterChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceParameterTabulationResponse;

/**
 * 设备参数service
 *
 * @author yue lin
 * @since 2023/6/25 15:13
 */
public interface DeviceParameterService {

    /**
     * 分页查询设备参数列表
     *
     * @param request 参数
     * @return  结果集
     */
    Page<DeviceParameterTabulationResponse> searchDeviceParameterTabulation(FeatureQueryRequest request);

    /**
     * 删除设备参数
     * @param id id
     */
    void deleteDeviceParameter(Long id);

    /**
     * 创建设备参数
     * @param request 参数
     * @return  id
     */
    Long createDeviceParameter(DeviceParameterChangeRequest.Create request);

    /**
     * 更新设备参数
     * @param request 参数
     * @return  id
     */
    Long updateDeviceParameter(DeviceParameterChangeRequest.Update request);

}
