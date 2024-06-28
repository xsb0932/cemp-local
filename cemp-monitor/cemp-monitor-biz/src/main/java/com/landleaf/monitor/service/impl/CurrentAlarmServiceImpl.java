package com.landleaf.monitor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.monitor.dal.mapper.CurrentAlarmMapper;
import com.landleaf.monitor.domain.entity.CurrentAlarmEntity;
import com.landleaf.monitor.domain.enums.*;
import com.landleaf.monitor.domain.request.CurrentAlarmListRequest;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.domain.response.AlarmTypeNumResponse;
import com.landleaf.monitor.service.CurrentAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CurrentAlarmServiceImpl
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Service
@RequiredArgsConstructor
public class CurrentAlarmServiceImpl implements CurrentAlarmService {
    private final CurrentAlarmMapper currentAlarmMapper;

    @Override
    public List<AlarmTypeNumResponse> getAlarmTypeNum(List<String> projectBizIds) {
        List<CurrentAlarmEntity> currentAlarmEntities = currentAlarmMapper.selectList(Wrappers
                .<CurrentAlarmEntity>lambdaQuery()
                .in(CurrentAlarmEntity::getProjectBizId, projectBizIds));
        Map<String, List<CurrentAlarmEntity>> alarmTypeMap = currentAlarmEntities.stream().collect(Collectors.groupingBy(CurrentAlarmEntity::getAlarmType));
        List<AlarmTypeNumResponse> alarmTypeNumResponseList = new ArrayList<>();
        alarmTypeMap.forEach((key, value) -> {
            AlarmTypeNumResponse alarmTypeNumResponse = new AlarmTypeNumResponse();
            alarmTypeNumResponse.setAlarmType(key);
            alarmTypeNumResponse.setAlarmTypeName(AlarmTypeEnum.getName(key));
            alarmTypeNumResponse.setNumber(value.size());
            alarmTypeNumResponseList.add(alarmTypeNumResponse);
        });
        return alarmTypeNumResponseList;
    }

    @Override
    public Page<AlarmListResponse> getAlarmResponse(CurrentAlarmListRequest request) {
        Page<AlarmListResponse> alarmListResponsePage = currentAlarmMapper.selectPageList(Page.of(request.getPageNo(), request.getPageSize()), Arrays.asList(request.getProjectBizIds().split(StrUtil.COMMA)), request);
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
}
