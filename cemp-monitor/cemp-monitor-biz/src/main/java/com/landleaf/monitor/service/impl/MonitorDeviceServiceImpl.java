package com.landleaf.monitor.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.dal.mapper.DeviceMonitorMapper;
import com.landleaf.monitor.domain.request.MonitorDeviceQueryRequest;
import com.landleaf.monitor.domain.response.MonitorDeviceListResponse;
import com.landleaf.monitor.service.MonitorDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * MonitorDeviceServiceImpl
 *
 * @author 张力方
 * @since 2023/7/25
 **/
@Service
@RequiredArgsConstructor
public class MonitorDeviceServiceImpl implements MonitorDeviceService {
    private final DeviceMonitorMapper deviceMonitorMapper;

    @Override
    public Page<MonitorDeviceListResponse> pageQuery(MonitorDeviceQueryRequest request) {
        return deviceMonitorMapper.pageQuery(
                        Page.of(request.getPageNo(), request.getPageSize()),
                        request);
    }
}
