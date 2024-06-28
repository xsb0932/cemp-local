package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceEventChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceEventTabulationResponse;

/**
 * 设备事件service
 *
 * @author yue lin
 * @since 2023/6/27 14:19
 */
public interface DeviceEventService {

    /**
     * 分页查询设备事件列表
     *
     * @param request 参数
     * @return  结果集
     */
    Page<DeviceEventTabulationResponse> searchDeviceEventTabulation(FeatureQueryRequest request);

    /**
     * 删除设备事件
     * @param id id
     */
    void deleteDeviceEvent(Long id);

    /**
     * 创建设备事件
     * @param request 参数
     * @return  id
     */
    Long createDeviceEvent(DeviceEventChangeRequest.Create request);

    /**
     * 更新设备事件
     * @param request 参数
     * @return  id
     */
    Long updateDeviceEvent(DeviceEventChangeRequest.Update request);
    
}
