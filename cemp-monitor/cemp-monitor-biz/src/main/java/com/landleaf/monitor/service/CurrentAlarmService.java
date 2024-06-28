package com.landleaf.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.domain.request.CurrentAlarmListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;

import java.util.List;

/**
 * CurrentAlarmService
 *
 * @author 张力方
 * @since 2023/8/14
 **/
public interface CurrentAlarmService {
    /**
     * 获取告警类型数量
     *
     * @param projectBizIds 项目业务ids
     * @return 告警类型&数量
     */
    List<AlarmTypeNumResponse> getAlarmTypeNum(List<String> projectBizIds);

    /**
     * 获取告警列表
     *
     * @param request 请求条件
     * @return 告警列表
     */
    Page<AlarmListResponse> getAlarmResponse(CurrentAlarmListRequest request);
}
