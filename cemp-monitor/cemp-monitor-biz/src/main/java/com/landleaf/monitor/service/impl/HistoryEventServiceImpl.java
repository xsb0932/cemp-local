package com.landleaf.monitor.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.monitor.dal.mapper.HistoryEventMapper;
import com.landleaf.monitor.domain.dto.AlarmTypeNumDTO;
import com.landleaf.monitor.domain.dto.ProjUnconfirmedAlarmCountDTO;
import com.landleaf.monitor.domain.entity.HistoryEventEntity;
import com.landleaf.monitor.domain.enums.*;
import com.landleaf.monitor.domain.request.HistoryEventListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.service.HistoryEventService;
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
public class HistoryEventServiceImpl implements HistoryEventService {
    private final HistoryEventMapper historyEventMapper;

    @Override
    public List<AlarmTypeNumResponse> getAlarmTypeNum(HistoryEventListRequest request) {
        List<AlarmTypeNumDTO> numList = historyEventMapper.selectCusCount(request);
        Map<String, List<AlarmTypeNumDTO>> alarmTypeMap = numList.stream().collect(Collectors.groupingBy(AlarmTypeNumDTO::getAlarmType));
        List<AlarmTypeNumResponse> alarmTypeNumResponseList = new ArrayList<>();
        alarmTypeMap.forEach((key, value) -> {
            AlarmTypeNumResponse alarmTypeNumResponse = new AlarmTypeNumResponse();
            alarmTypeNumResponse.setAlarmType(key);
            alarmTypeNumResponse.setAlarmTypeName(AlarmTypeEnum.getName(key));
            alarmTypeNumResponse.setNumber(value.stream().mapToInt(i -> i.getNumber()).sum());
            alarmTypeNumResponse.setReadNumber(value.stream().mapToInt(i -> i.getConfirmNum()).sum());
            alarmTypeNumResponse.setUnreadNumber(alarmTypeNumResponse.getNumber() - alarmTypeNumResponse.getReadNumber());
            alarmTypeNumResponseList.add(alarmTypeNumResponse);
        });
        return alarmTypeNumResponseList;
    }

    @Override
    public Page<AlarmListResponse> getAlarmResponse(HistoryEventListRequest request) {
        Page<AlarmListResponse> alarmListResponsePage = historyEventMapper.selectPageList(Page.of(request.getPageNo(), request.getPageSize()), request);
        List<AlarmListResponse> records = alarmListResponsePage.getRecords();
        for (AlarmListResponse alarmListResponse : records) {
            alarmListResponse.setEventTypeName(EventTypeEnum.getName(alarmListResponse.getEventType()));
            alarmListResponse.setAlarmLevelName(AlarmLevelEnum.getName(alarmListResponse.getAlarmLevel()));
            alarmListResponse.setAlarmObjTypeName(AlarmObjTypeEnum.getName(alarmListResponse.getAlarmObjType()));
            alarmListResponse.setAlarmStatusName(AlarmStatusEnum.getName(alarmListResponse.getAlarmStatus()));
            alarmListResponse.setAlarmTypeName(AlarmTypeEnum.getName(alarmListResponse.getAlarmType()));
        }
        return alarmListResponsePage;
    }

    @Override
    public List<AlarmListResponse> getAlarmExcelResponse(HistoryEventListRequest request) {
        List<AlarmListResponse> alarmListResponses = historyEventMapper.selectExcelList(request);
        for (AlarmListResponse alarmListResponse : alarmListResponses) {
            alarmListResponse.setEventTypeName(EventTypeEnum.getName(alarmListResponse.getEventType()));
            alarmListResponse.setAlarmLevelName(AlarmLevelEnum.getName(alarmListResponse.getAlarmLevel()));
            alarmListResponse.setAlarmObjTypeName(AlarmObjTypeEnum.getName(alarmListResponse.getAlarmObjType()));
            alarmListResponse.setAlarmStatusName(AlarmStatusEnum.getName(alarmListResponse.getAlarmStatus()));
            alarmListResponse.setAlarmTypeName(AlarmTypeEnum.getName(alarmListResponse.getAlarmType()));
        }
        return alarmListResponses;
    }

    @Override
    public List<AlarmResponse> getRJDAlarms(String tenantId) {
        return historyEventMapper.getRJDAlarms(tenantId);
    }
}
