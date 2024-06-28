package com.landleaf.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.domain.request.HistoryEventListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.dto.AlarmResponse;

import java.util.List;
import java.util.Map;

/**
 * HistoryEventService
 *
 * @author 张力方
 * @since 2023/8/14
 **/
public interface HistoryEventService {
    /**
     * 获取告警类型数量
     *
     * @param request 请求条件
     * @return 告警类型&数量
     */
    List<AlarmTypeNumResponse> getAlarmTypeNum(HistoryEventListRequest request);

    /**
     * 获取告警列表
     *
     * @param request 请求条件
     * @return 告警列表
     */
    Page<AlarmListResponse> getAlarmResponse(HistoryEventListRequest request);

    List<AlarmListResponse> getAlarmExcelResponse(HistoryEventListRequest request);

    List<AlarmResponse> getRJDAlarms(String s);
}
