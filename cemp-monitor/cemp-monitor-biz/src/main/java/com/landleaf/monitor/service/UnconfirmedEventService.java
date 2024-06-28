package com.landleaf.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.domain.request.HistoryEventListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.dto.AlarmResponse;

import java.util.List;
import java.util.Map;

/**
 * UnconfirmedEventService
 *
 * @author 张力方
 * @since 2023/8/14
 **/
public interface UnconfirmedEventService {
    /**
     * 根据projId,分组获取未确认告警的数量
     *
     * @param userId 用户编号，为空时，查询所有的当前tenantId下的userid
     * @return
     */
    Map<String, Integer> getUnconfirmedCount(Long userId);

    /**
     * 获取下条未确认的告警信息
     *
     * @param currentId 当前条id，如果为null。则从头开始找
     * @return 告警信息
     */
    AlarmListResponse getUnconfirmedInfo(Long currentId);

    /**
     * 删除对应的事件
     *
     * @param eventId
     */
    void delEvent(String eventId);
}
