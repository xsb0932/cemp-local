package com.landleaf.monitor.service;

import com.landleaf.data.api.device.dto.DeviceHistoryDTO;
import com.landleaf.monitor.domain.dto.HistoryQueryDTO;

import java.util.List;

/**
 * 设备历史查询逻辑封装
 */
public interface DeviceHistoryService {
    /**
     * 根据查询条件，查询历史信息
     *
     * @param queryDTO
     */
    List<DeviceHistoryDTO> queryHistory(HistoryQueryDTO queryDTO);

    /**
     * 根据attrCode,获取name
     * @param attrCode
     * @return
     */
    String queryNameByCode(String attrCode);
}
