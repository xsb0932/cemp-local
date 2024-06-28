package com.landleaf.monitor.service.impl;

import com.landleaf.monitor.dal.mapper.DeviceParameterMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.monitor.domain.entity.DeviceParameterEntity;
import com.landleaf.monitor.service.DeviceParameterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备参数明细表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-07-27
 */
@Service
@AllArgsConstructor
@Slf4j
public class DeviceParameterServiceImpl extends ServiceImpl<DeviceParameterMapper, DeviceParameterEntity> implements DeviceParameterService {


}
