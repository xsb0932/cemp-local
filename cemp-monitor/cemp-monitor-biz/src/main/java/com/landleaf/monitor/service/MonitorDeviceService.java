package com.landleaf.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.domain.request.MonitorDeviceQueryRequest;
import com.landleaf.monitor.domain.response.MonitorDeviceListResponse;

/**
 * MonitorDeviceService
 *
 * @author 张力方
 * @since 2023/7/20
 **/
public interface MonitorDeviceService {
    Page<MonitorDeviceListResponse> pageQuery(MonitorDeviceQueryRequest request);
}
