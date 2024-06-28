package com.landleaf.monitor.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.monitor.domain.dto.DeviceControlDTO;
import com.landleaf.monitor.domain.entity.DeviceModeEntity;

/**
 * DeviceModeEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-09-14
 */
public interface DeviceModeService extends IService<DeviceModeEntity> {



    void setMode(DeviceControlDTO cmd);

    DeviceModeEntity getMode(String bizDeviceId);

    List<DeviceModeEntity> getModeByCode(String modeCode);

    List<DeviceModeEntity> getModeByCode(String modeCode,List<String> devices);
}
