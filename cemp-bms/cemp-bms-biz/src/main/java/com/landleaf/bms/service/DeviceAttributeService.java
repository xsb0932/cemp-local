package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceAttributeChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;

/**
 * 设备属性service
 *
 * @author yue lin
 * @since 2023/6/25 15:13
 */
public interface DeviceAttributeService {

    /**
     * 分页查询设备属性列表
     *
     * @param request 参数
     * @return  结果集
     */
    Page<DeviceAttributeTabulationResponse> searchDeviceAttributeTabulation(FeatureQueryRequest request);

    /**
     * 删除设备属性
     * @param id id
     */
    void deleteDeviceAttribute(Long id);

    /**
     * 创建设备属性
     * @param request 参数
     * @return  id
     */
    Long createDeviceAttribute(DeviceAttributeChangeRequest.Create request);

    /**
     * 更新设备属性
     * @param request 参数
     * @return  id
     */
    Long updateDeviceAttribute(DeviceAttributeChangeRequest.Update request);

}
