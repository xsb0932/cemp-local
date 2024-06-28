package com.landleaf.monitor.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.landleaf.bms.api.UserManagementNodeApi;
import com.landleaf.bms.api.dto.UserProjRelationResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.dal.mapper.CurrentAlarmMapper;
import com.landleaf.monitor.dal.mapper.UnconfirmedEventMapper;
import com.landleaf.monitor.domain.entity.CurrentAlarmEntity;
import com.landleaf.monitor.domain.entity.HistoryEventEntity;
import com.landleaf.monitor.domain.entity.UnconfirmedEventEntity;
import com.landleaf.monitor.dto.AlarmAddRequest;
import com.landleaf.monitor.dto.CurrentAlarmResponse;
import com.landleaf.monitor.enums.*;
import com.landleaf.monitor.dal.mapper.AlarmConfirmMapper;
import com.landleaf.monitor.dal.mapper.HistoryEventMapper;
import com.landleaf.monitor.domain.dto.ProjUnconfirmedAlarmCountDTO;
import com.landleaf.monitor.domain.entity.AlarmConfirmEntity;
import com.landleaf.monitor.domain.response.AlarmListResponse;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.monitor.service.impl.UnconfirmedEventServiceImpl;
import com.landleaf.mqtt.core.MqttTemplate;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AlarmApiImpl
 *
 * @author 张力方
 * @since 2023/8/22
 **/
@RestController
@RequiredArgsConstructor
public class AlarmApiImpl implements AlarmApi {
    private final HistoryEventMapper historyEventMapper;

    private final UnconfirmedEventMapper unconfirmedEventMapper;

    private final UserManagementNodeApi userManagementNodeApi;

    private final MqttTemplate mqttTemplate;

    private final AlarmConfirmMapper alarmConfirmMapper;

    private final BizSequenceService bizSequenceService;

    private final CurrentAlarmMapper currentAlarmMapper;

    @Override
    public Response<List<AlarmResponse>> query(List<String> deviceBizIds) {
        TenantContext.setIgnore(true);
        // 查询最新的五条数据
        List<AlarmListResponse> alarmListResponses = historyEventMapper.selectListByDeviceIds(deviceBizIds);
        List<AlarmResponse> alarmResponses = new ArrayList<>();
        for (AlarmListResponse alarmListResponse : alarmListResponses) {
            AlarmResponse alarmResponse = new AlarmResponse();
            alarmResponse.setTime(alarmListResponse.getEventTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")).substring(2));
            alarmResponse.setAlarmDevice(alarmListResponse.getObjName());
            alarmResponse.setAlarmDesc(alarmListResponse.getAlarmDesc());
            alarmResponse.setAlarmStatus(AlarmStatusEnum.getName(alarmListResponse.getAlarmStatus()));
            alarmResponses.add(alarmResponse);
        }
        return Response.success(alarmResponses);

    }

    @Override
    public Response<Boolean> changeAlarmCount(Long tenantId, String bizProjId) {
        TenantContext.setTenantId(tenantId);
        List<ProjUnconfirmedAlarmCountDTO> list = unconfirmedEventMapper.getUnconfirmedCount(tenantId, null);
        Map<String, Integer> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(list)) {
            map = list.stream().collect(Collectors.toMap(ProjUnconfirmedAlarmCountDTO::getBizProjId, ProjUnconfirmedAlarmCountDTO::getCount));
        }
        // 获取当前tenant下， 用户和proj的关系
        Response<List<UserProjRelationResponse>> resp = userManagementNodeApi.getUserProjRelation(tenantId);
        if (resp.isSuccess() && !CollectionUtils.isEmpty(resp.getResult())) {
            List<UserProjRelationResponse> userProjRelation = resp.getResult();
            // 推送
            Map<Long, Boolean> needPush = new HashMap<>();
            Map<Long, Integer> alarmCount = new HashMap<>();
            for (UserProjRelationResponse temp : userProjRelation) {
                if (temp.getBizProjId().equals(bizProjId)) {
                    needPush.put(temp.getUserId(), true);
                }
                if (!alarmCount.containsKey(temp.getUserId())) {
                    alarmCount.put(temp.getUserId(), 0);
                }
                alarmCount.put(temp.getUserId(), alarmCount.get(temp.getUserId()) + (map.containsKey(temp.getBizProjId()) ? map.get(temp.getBizProjId()) : 0));
            }
            needPush.forEach((k, v) -> {
                int count = alarmCount.get(k);
                // push
                JSONObject obj = new JSONObject();
                obj.set("count", count);
                mqttTemplate.publish("/notice/alarm/count/" + k, obj.toString(), MqttQoS.AT_MOST_ONCE);
            });
        }

        return Response.success(true);
    }

    @Override
    public Response<Boolean> addAlarmConfirm(Long tenantId, String eventId, String alarmAlarmType) {
        TenantContext.setIgnore(true);
        if (!AlarmConfirmTypeEnum.AUTO.getCode().equals(alarmAlarmType)) {
            return Response.success(true);
        }
        AlarmConfirmEntity alarmConfirmEntity = new AlarmConfirmEntity();
        alarmConfirmEntity.setTenantId(tenantId);
        alarmConfirmEntity.setEventId(eventId);
        alarmConfirmEntity.setAlarmConfirmType(alarmAlarmType);
        alarmConfirmEntity.setIsConfirm(true);
        alarmConfirmEntity.setConfirmUser(0L);
        alarmConfirmEntity.setConfirmTime(LocalDateTime.now());
        alarmConfirmMapper.insert(alarmConfirmEntity);
        return Response.success(true);
    }

    @Override
    public Response<Boolean> addCurrentAlarm(AlarmAddRequest request) {
        TenantContext.setIgnore(true);
        CurrentAlarmEntity currentAlarmEntity = new CurrentAlarmEntity();
        currentAlarmEntity.setTenantId(request.getTenantId());
        currentAlarmEntity.setProjectBizId(request.getBizProjId());
        currentAlarmEntity.setAlarmType(request.getAlarmType());
        currentAlarmEntity.setAlarmObjType(request.getAlarmObjType());
        if (null != request.getAlarmInfo()) {
            currentAlarmEntity.setAlarmCode(request.getAlarmInfo().getAlarmCode());
            currentAlarmEntity.setAlarmDesc(request.getAlarmInfo().getAlarmDesc());
            currentAlarmEntity.setAlarmLevel(request.getAlarmInfo().getAlarmTriggerLevel());
        }
        currentAlarmEntity.setAlarmStatus(request.getAlarmStatus());
        currentAlarmEntity.setAlarmObjType(request.getAlarmObjType());
        if (!StringUtils.hasText(currentAlarmEntity.getAlarmObjType())) {
            currentAlarmEntity.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        }
        currentAlarmEntity.setAlarmBizId(bizSequenceService.next(BizSequenceEnum.ALARM));
        currentAlarmEntity.setEventId(bizSequenceService.next(BizSequenceEnum.EVENT));
        currentAlarmEntity.setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTime()), ZoneId.systemDefault()));
        currentAlarmEntity.setEventType(EventTypeEnum.ALARM_EVENT.getCode());
        currentAlarmEntity.setObjId(request.getObjId());
        if (!AlarmStatusEnum.TRIGGER_NO_RESET.getCode().equals(request.getAlarmStatus())) {
            // 告警无复归，不计入当前告警
            currentAlarmMapper.insert(currentAlarmEntity);
        }
        request.setAlarmBizId(currentAlarmEntity.getAlarmBizId());
        request.setEventId(currentAlarmEntity.getEventId());
        addHisEvent(request);
        return Response.success(true);
    }

    @Override
    public Response<Boolean> addHisEvent(AlarmAddRequest request) {
        TenantContext.setIgnore(true);
        HistoryEventEntity historyEventEntity = new HistoryEventEntity();
        historyEventEntity.setTenantId(request.getTenantId());
        historyEventEntity.setProjectBizId(request.getBizProjId());
        historyEventEntity.setAlarmType(request.getAlarmType());
        historyEventEntity.setAlarmObjType(request.getAlarmObjType());

        String alarmConfirmType = AlarmConfirmTypeEnum.AUTO.getCode();
        if (null != request.getAlarmInfo()) {
            historyEventEntity.setAlarmCode(request.getAlarmInfo().getAlarmCode());
            historyEventEntity.setAlarmDesc(request.getAlarmInfo().getAlarmDesc());
            if (AlarmCodeConstance.CONN_ALARM_CODE.equals(request.getAlarmInfo().getAlarmCode())) {
                if (AlarmStatusEnum.CONN_OK.getCode().equals(request.getAlarmStatus())) {
                    historyEventEntity.setAlarmLevel(request.getAlarmInfo().getAlarmRelapseLevel());
                } else {
                    historyEventEntity.setAlarmLevel(request.getAlarmInfo().getAlarmTriggerLevel());
                    alarmConfirmType = request.getAlarmInfo().getAlarmConfirmType();
                }
            } else {
                if (AlarmStatusEnum.RESET.getCode().equals(request.getAlarmStatus())) {
                    historyEventEntity.setAlarmLevel(request.getAlarmInfo().getAlarmRelapseLevel());
                    // 规则告警，确认方式，复归的时候也可以选择需要人工确认，所以，需要改改
                    if (AlarmTypeEnum.RULE_ALARM.getCode().equals(request.getAlarmType())) {
                        alarmConfirmType = request.getAlarmInfo().getAlarmRelapseConfirmType();
                    }
                } else {
                    historyEventEntity.setAlarmLevel(request.getAlarmInfo().getAlarmTriggerLevel());
                    alarmConfirmType = request.getAlarmInfo().getAlarmConfirmType();
                }
            }
        }
        historyEventEntity.setAlarmStatus(request.getAlarmStatus());
        if (!StringUtils.hasText(request.getAlarmObjType())) {
            historyEventEntity.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        }
        if (StringUtils.hasText(request.getAlarmBizId())) {
            historyEventEntity.setAlarmBizId(request.getAlarmBizId());
        } else {
            historyEventEntity.setAlarmBizId(bizSequenceService.next(BizSequenceEnum.ALARM));
        }
        if (StringUtils.hasText(request.getEventId())) {
            historyEventEntity.setEventId(request.getEventId());
        } else {
            historyEventEntity.setEventId(bizSequenceService.next(BizSequenceEnum.EVENT));
        }
        historyEventEntity.setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTime()), ZoneId.systemDefault()));
        if (StringUtils.hasText(request.getEventType())) {
            historyEventEntity.setEventType(request.getEventType());
        } else {
            historyEventEntity.setEventType(EventTypeEnum.ALARM_EVENT.getCode());
        }
        historyEventEntity.setObjId(request.getObjId());
        historyEventMapper.insert(historyEventEntity);
        addAlarmConfirm(request.getTenantId(), historyEventEntity.getEventId(), alarmConfirmType);
        if (!AlarmConfirmTypeEnum.AUTO.getCode().equals(alarmConfirmType)) {
            // 非自动确认的，需要给unconfirmedEvent里面假如数据
            UnconfirmedEventEntity copyEntity = BeanUtil.copyProperties(historyEventEntity, UnconfirmedEventEntity.class);
            unconfirmedEventMapper.insert(copyEntity);
        }
        if (!AlarmConfirmTypeEnum.AUTO.getCode().equals(alarmConfirmType)) {
            changeAlarmCount(request.getTenantId(), request.getBizProjId());
        }
        return Response.success(true);
    }

    @Override
    public Response<Boolean> delCurrentAlarm(Long tenantId, String bizProjId, Long alarmId) {
        TenantContext.setIgnore(true);
        currentAlarmMapper.deleteAlarmById(alarmId);
        return Response.success(true);
    }

    @Override
    public Response<CurrentAlarmResponse> delCurrentAlarmByCode(String bizDeviceId, String alarmCode) {
        TenantContext.setIgnore(true);
        List<CurrentAlarmEntity> list = currentAlarmMapper.selectList(new QueryWrapper<CurrentAlarmEntity>().lambda().
                eq(CurrentAlarmEntity::getObjId, bizDeviceId).eq(CurrentAlarmEntity::getAlarmCode, alarmCode));
        CurrentAlarmResponse result = null;
        if (!CollectionUtils.isEmpty(list)) {
            result = BeanUtil.copyProperties(list.get(0), CurrentAlarmResponse.class);
            currentAlarmMapper.deleteAlarmById(result.getId());
        }
        return Response.success(result);
    }
}
