package com.landleaf.monitor.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.monitor.dal.mapper.HistoryEventMapper;
import com.landleaf.monitor.dal.mapper.UnconfirmedEventMapper;
import com.landleaf.monitor.domain.dto.AlarmTypeNumDTO;
import com.landleaf.monitor.domain.dto.ProjUnconfirmedAlarmCountDTO;
import com.landleaf.monitor.domain.enums.*;
import com.landleaf.monitor.domain.request.HistoryEventListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.service.HistoryEventService;
import com.landleaf.monitor.service.UnconfirmedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HistoryEventServiceImpl
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Service
@RequiredArgsConstructor
public class UnconfirmedEventServiceImpl implements UnconfirmedEventService {
    private final UnconfirmedEventMapper unconfirmedEventMapper;

    @Override
    public Map<String, Integer> getUnconfirmedCount(Long userId) {
        TenantContext.setIgnore(true);
        Long tenantId = TenantContext.getTenantId();
        List<ProjUnconfirmedAlarmCountDTO> countList = unconfirmedEventMapper.getUnconfirmedCount(tenantId, userId);
        if (!CollectionUtils.isEmpty(countList)) {
            return countList.stream().collect(Collectors.toMap(ProjUnconfirmedAlarmCountDTO::getBizProjId, ProjUnconfirmedAlarmCountDTO::getCount));
        }
        return Maps.newHashMap();
    }

    @Override
    public AlarmListResponse getUnconfirmedInfo(Long currentId) {
        TenantContext.setIgnore(true);
        Long tenantId = TenantContext.getTenantId();
        Long userId = LoginUserUtil.getLoginUserId();
        AlarmListResponse result = unconfirmedEventMapper.getUnconfirmedInfo(tenantId, currentId, userId);
        if (null != result) {
            result.setEventTypeName(EventTypeEnum.getName(result.getEventType()));
            result.setAlarmLevelName(AlarmLevelEnum.getName(result.getAlarmLevel()));
            result.setAlarmObjTypeName(AlarmObjTypeEnum.getName(result.getAlarmObjType()));
            result.setAlarmStatusName(AlarmStatusEnum.getName(result.getAlarmStatus()));
            result.setAlarmTypeName(AlarmTypeEnum.getName(result.getAlarmType()));
            AlarmListResponse nextResult = unconfirmedEventMapper.getUnconfirmedInfo(tenantId, result.getId(), userId);
            result.setHasNext(null != nextResult ? 1 : 0);
        }
        return result;
    }

    /**
     * 物理删除未确认事件
     *
     * @param eventId
     */
    @Override
    public void delEvent(String eventId) {
        unconfirmedEventMapper.delEvent(eventId);
    }
}
