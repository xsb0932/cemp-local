package com.landleaf.bms.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.bms.domain.entity.DeviceParameterDetailEntity;

import java.util.List;


/**
 * 设备参数明细表的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-07-24
 */
public interface DeviceParameterDetailService extends IService<DeviceParameterDetailEntity> {

    /**
     * 根据identifier，删除数据
     * @param identifiers
     * @return
     */
    int deleteByIdentifiers(String bizDeviceId, List<String> identifiers);
}
