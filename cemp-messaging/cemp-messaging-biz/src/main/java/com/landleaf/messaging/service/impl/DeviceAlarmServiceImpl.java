package com.landleaf.messaging.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.AlarmPushApi;
import com.landleaf.bms.api.GatewayApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.bms.api.dto.AlarmPushRequest;
import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.bms.api.dto.ProductAlarmConfListResponse;
import com.landleaf.bms.api.dto.ProductDeviceEventListResponse;
import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.messaging.config.*;
import com.landleaf.messaging.dao.AlarmConfirmMapper;
import com.landleaf.messaging.dao.CurrentAlarmMapper;
import com.landleaf.messaging.dao.HistoryEventMapper;
import com.landleaf.messaging.domain.CurrentAlarmEntity;
import com.landleaf.messaging.service.DeviceAlarmService;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.dto.AlarmAddRequest;
import com.landleaf.monitor.dto.ProductAlarmConf;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.dao.DeviceCacheDao;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.share.GatewayCache;
import com.landleaf.redis.share.ProductAlarmConfCache;
import com.landleaf.redis.share.ProductEventConfCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CurrentAlarmServiceImpl
 *
 * @author 张力方
 * @since 2023/8/15
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceAlarmServiceImpl implements DeviceAlarmService {
    private final CurrentAlarmMapper currentAlarmMapper;

    private final HistoryEventMapper historyEventMapper;

    private final AlarmConfirmMapper alarmConfirmMapper;

    private final BizSequenceService bizSequenceService;

    private final GatewayApi gatewayApi;

    private final ProductApi productApi;

    private final RedisUtils redisUtils;

    private final AlarmApi alarmApi;

    private final InfluxdbTemplate influxdbTemplate;

    private final GatewayCache gatewayCache;
    private final DeviceCacheDao deviceCacheDao;
    private final ProductAlarmConfCache productAlarmConfCache;
    private final ProductEventConfCache productEventConfCache;
    private final AlarmPushApi alarmPushApi;

    @Override
    public void dealAlarmInfo(String bizGateId, String pkId, String bizDeviceId, String alarmType, Map<Object, Object> deviceStatusObj, long time, String alarmCode, String alarmStatus) {
        Map<String, Object> info = (Map<String, Object>) deviceStatusObj.get(MsgContextTypeEnum.ALARM.getType());
        if (null == info) {
            info = new HashMap<>();
        }
        // 开始判断时间更新
        Map<String, Object> temp = null;
        boolean hasUpdate = false;
        // info不包含对应key，直接信息
        if (!info.containsKey(alarmCode)) {
            temp = Maps.newHashMap();
            temp.put("time", time);
            temp.put("val", alarmStatus);
            // add2history
            info.put(alarmCode, temp);
            hasUpdate = true;
        } else {
            temp = (Map<String, Object>) info.get(alarmCode);
            if (time >= (long) temp.get("time") && !alarmStatus.equals(temp.get("val"))) {
                // 更新
                temp.put("time", time);
                temp.put("val", alarmStatus);
                // add2history
                info.put(alarmCode, temp);
                hasUpdate = true;
            }
        }

        // 发生了变更，则处理
        if (hasUpdate) {
            Response<GatewayProjectResponse> resp = null;
            Response cacheResp = gatewayCache.getCache(bizGateId, Response.class);
            if (null != cacheResp) {
                resp = new Response<GatewayProjectResponse>();
                resp.setSuccess(cacheResp.isSuccess());
                resp.setResult(JSON.parseObject(String.valueOf(cacheResp.getResult()), GatewayProjectResponse.class));
            }
            if (null == resp) {
                resp = gatewayApi.getProjectInfoByBizId(bizGateId);
                if (resp.isSuccess()) {
                    gatewayCache.setCache(bizGateId, resp);
                }
            }
            if (!resp.isSuccess() || null == resp.getResult()) {
                log.info("无法获取网关信息,变更状态返回。{}", bizGateId);
                return;
            }
            TenantContext.setTenantId(resp.getResult().getTenantId());
            Response<List<ProductAlarmConfListResponse>> alarmResp = null;
            Response alarmCacheResp = productAlarmConfCache.getCache(pkId, Response.class);
            if (null != alarmCacheResp) {
                alarmResp = new Response<List<ProductAlarmConfListResponse>>();
                alarmResp.setSuccess(alarmCacheResp.isSuccess());
                alarmResp.setResult(JSONArray.parseArray(String.valueOf(alarmCacheResp.getResult()), ProductAlarmConfListResponse.class));
            }
            if (null == alarmResp) {
                alarmResp = productApi.getProductAlarm(pkId);
                if (alarmResp.isSuccess()) {
                    productAlarmConfCache.setCache(pkId, alarmResp);
                }
            }
            if (!alarmResp.isSuccess() || CollectionUtils.isEmpty(alarmResp.getResult())) {
                log.info("无法获取告警信息,变更状态返回。{}", pkId);
                return;
            }
            Map<String, ProductAlarmConfListResponse> alarmInfoMap = null;
            if (alarmType.equals(AlarmTypeEnum.DEVICE_ALARM.getCode()) || alarmType.equals(AlarmTypeEnum.CON_ALARM.getCode())) {
                alarmInfoMap = alarmResp.getResult().stream().collect(Collectors.toMap(ProductAlarmConfListResponse::getAlarmCode, i -> i, (v1, v2) -> v1));
                if (!alarmInfoMap.containsKey(alarmCode)) {
                    // 没这个故障，直接返回
                    log.info("未知事件。{}", alarmCode);
                    return;
                }
            }
            if (alarmType.equals(AlarmTypeEnum.DEVICE_ALARM.getCode())) {
                // 设备告警，
                if (AlarmStatusEnum.TRIGGER_NO_RESET.getCode().equals(alarmStatus)) {
                    // 触发-无复归直接记录历史故障
                    addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, null, null, time, EventTypeEnum.ALARM_EVENT.getCode());
                } else if (AlarmStatusEnum.TRIGGER_RESET.getCode().equals(alarmStatus)) {
                    // 判断，当前故障是否有对应值，无则新增，有则不处理
                    CurrentAlarmEntity alarm = getCurrentAlarm(bizDeviceId, alarmCode);
                    if (null == alarm) {
                        // 新增故障
                        addCurrentAlarm(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, time);
                    }
                } else if (AlarmStatusEnum.RESET.getCode().equals(alarmStatus)) {
                    // 判断，当前故障是否有对应值，有则删除，同时，插入复归的记录
                    CurrentAlarmEntity alarm = getCurrentAlarm(bizDeviceId, alarmCode);
                    if (null != alarm) {
                        alarmApi.delCurrentAlarm(alarm.getTenantId(), alarm.getProjectBizId(), alarm.getId());
                        addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, alarm.getAlarmBizId(), null, time, EventTypeEnum.ALARM_EVENT.getCode());
                    } else {
                        // addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, null, null, time);
                        // modify by hebin, 需求变更，要求reset的， 没有当前故障， 则直接不处理
                    }
                }
            } else if (alarmType.equals(AlarmTypeEnum.CON_ALARM.getCode())) {
                if (AlarmStatusEnum.CONN_ERROR.getCode().equals(alarmStatus)) {
                    // 判断，当前故障是否有通讯故障，无则新增，有则不处理
                    CurrentAlarmEntity alarm = getCurrentAlarm(bizDeviceId, alarmCode);
                    if (null == alarm) {
                        // 新增故障
                        addCurrentAlarm(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, time);
                    }
                    // 将设备置为离线
                    Map<String, Object> cstInfo = (Map<String, Object>) deviceStatusObj.get("CST");
                    cstInfo.put("time", time);
                    cstInfo.put("val", AlarmCodeConstance.CST_OFFLINE);
                    redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "CST", cstInfo);
                    // 插入一条CST=0的时序数据
                    Map<String, Object> valMap = Maps.newHashMap();
                    valMap.put("CST", Integer.valueOf(AlarmCodeConstance.CST_OFFLINE));
                    Map<String, String> tags = Maps.newHashMap();
                    tags.put("biz_device_id", bizDeviceId);
                    tags.put("biz_tenant_id", resp.getResult().getBizTenantId());
                    tags.put("biz_project_id", resp.getResult().getProjectBizId());
                    tags.put("biz_node_id", resp.getResult().getParentNodeBizId());
                    influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(pkId)).tag(tags)
                            .fields(valMap).time(time, TimeUnit.MILLISECONDS)
                            .build());
                } else if (AlarmStatusEnum.CONN_OK.getCode().equals(alarmStatus)) {
                    // 判断，当前故障是否有对应值，有则删除，同时，插入复归的记录
                    CurrentAlarmEntity alarm = getCurrentAlarm(bizDeviceId, alarmCode);
                    if (null != alarm) {
                        log.info("通讯复归，处理下。{}", JSON.toJSONString(alarm));
                        alarmApi.delCurrentAlarm(alarm.getTenantId(), alarm.getProjectBizId(), alarm.getId());
                        addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, alarm.getAlarmBizId(), null, time, EventTypeEnum.ALARM_EVENT.getCode());
                    } else {
                        // addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), alarmInfoMap.get(alarmCode), bizDeviceId, alarmType, alarmStatus, null, null, time);
                        // modify by hebin, 需求变更，要求reset的， 没有当前故障， 则直接不处理
                    }
                }
            } else {
                // other
                // 其他事件，当前直接记录his，根据需求，其alarm信息需单独构建
                Response<List<ProductDeviceEventListResponse>> eventResp = null;
                eventResp = productEventConfCache.getCache(pkId, Response.class);
                Response eventCacheResp = productEventConfCache.getCache(pkId, Response.class);
                if (null != eventCacheResp) {
                    eventResp = new Response<List<ProductDeviceEventListResponse>>();
                    eventResp.setSuccess(eventCacheResp.isSuccess());
                    eventResp.setResult(JSONArray.parseArray(String.valueOf(eventCacheResp.getResult()), ProductDeviceEventListResponse.class));
                }
                if (null == eventResp) {
                    eventResp = productApi.getEventByProdId(pkId);
                    if (eventResp.isSuccess()) {
                        productEventConfCache.setCache(pkId, eventResp);
                    }
                }
                if (!alarmResp.isSuccess() || CollectionUtils.isEmpty(alarmResp.getResult())) {
                    log.info("无法获取事件信息,变更状态返回。{}", pkId);
                    return;
                }
                ProductDeviceEventListResponse eventInfo = eventResp.getResult().stream().filter(i -> i.getIdentifier().equals(alarmType)).findFirst().orElse(null);
                if (null == eventInfo || CollectionUtils.isEmpty(eventInfo.getEventParameter())) {
                    log.info("otherEvent不存在,变更状态返回。{}", pkId);
                    return;
                }
                FunctionParameter eventParam = eventInfo.getEventParameter().stream().filter(i -> i.getIdentifier().equals(alarmCode)).findFirst().orElse(null);
                if (null == eventParam) {
                    log.info("otherEvent定义的code不存在,变更状态返回。{}", pkId);
                    return;
                }
                ProductAlarmConfListResponse alarmInfo = new ProductAlarmConfListResponse();
                alarmInfo.setAlarmCode(AlarmTypeEnum.OTHER_EVENT.getCode());
                JSONObject valObj = new JSONObject();
                valObj.put(alarmCode, alarmStatus);
                alarmInfo.setAlarmDesc("设备发生" + eventInfo.getFunctionName() + "事件，事件参数：" + valObj.toJSONString());
                alarmInfo.setAlarmRelapseLevel(AlarmConfirmTypeEnum.AUTO.getCode());
                alarmInfo.setAlarmTriggerLevel(AlarmConfirmTypeEnum.AUTO.getCode());
                addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfo, bizDeviceId, alarmType, AlarmStatusEnum.TRIGGER_NO_RESET.getCode(), null, null, time, EventTypeEnum.COMMON_EVENT.getCode());
            }
            redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, MsgContextTypeEnum.ALARM.getType(), info);
        }
    }

    private CurrentAlarmEntity getCurrentAlarm(String bizDeviceId, String alarmCode) {
        return currentAlarmMapper.selectOne(new QueryWrapper<CurrentAlarmEntity>().lambda().eq(CurrentAlarmEntity::getObjId, bizDeviceId)
                .eq(CurrentAlarmEntity::getAlarmCode, alarmCode));
    }

    private void addCurrentAlarm(Long tenantId, String bizProjId, String projectName, ProductAlarmConfListResponse alarmInfo, String bizDeviceId, String alarmType, String alarmStatus, long time) {
        AlarmAddRequest req = new AlarmAddRequest();
        req.setTenantId(tenantId);
        req.setAlarmInfo(BeanUtil.copyProperties(alarmInfo, ProductAlarmConf.class));
        req.setAlarmStatus(alarmStatus);
        req.setAlarmType(alarmType);
        req.setObjId(bizDeviceId);
        req.setTime(time);
        req.setBizProjId(bizProjId);
        req.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        alarmApi.addCurrentAlarm(req);
        DeviceInfoCacheDTO deviceCache = deviceCacheDao.getDeviceInfoCache(bizDeviceId);
        AlarmPushRequest pushReq = new AlarmPushRequest();
        pushReq.setAlarmStatus(alarmStatus)
                .setAlarmType(alarmType)
                .setAlarmDesc(alarmInfo.getAlarmDesc())
                .setAlarmLevel(alarmInfo.getAlarmTriggerLevel())
                .setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()))
                .setBizProjectId(bizProjId)
                .setProjectName(projectName)
                .setObjName(deviceCache.getName())
                .setTenantId(tenantId);

        alarmPushApi.alarmPush(pushReq);
    }

    private void addHisEvent(Long tenantId, String bizProjId, String projectName, ProductAlarmConfListResponse alarmInfo, String bizDeviceId, String alarmType, String alarmStatus, String alarmBizId, String eventId, long time, String eventType) {
        AlarmAddRequest req = new AlarmAddRequest();
        req.setTenantId(tenantId);
        req.setAlarmInfo(BeanUtil.copyProperties(alarmInfo, ProductAlarmConf.class));
        req.setAlarmStatus(alarmStatus);
        req.setAlarmType(alarmType);
        req.setObjId(bizDeviceId);
        req.setTime(time);
        req.setBizProjId(bizProjId);
        req.setEventId(eventId);
        req.setAlarmBizId(alarmBizId);
        req.setAlarmObjType(AlarmObjTypeEnum.DEVICE.getCode());
        req.setEventType(eventType);
        alarmApi.addHisEvent(req);

        DeviceInfoCacheDTO deviceCache = deviceCacheDao.getDeviceInfoCache(bizDeviceId);
        AlarmPushRequest pushReq = new AlarmPushRequest();
        pushReq.setAlarmStatus(alarmStatus)
                .setAlarmType(alarmType)
                .setAlarmDesc(alarmInfo.getAlarmDesc())
                .setAlarmLevel(AlarmStatusEnum.TRIGGER_NO_RESET.getCode().equals(alarmStatus) ? alarmInfo.getAlarmTriggerLevel() : alarmInfo.getAlarmRelapseLevel())
                .setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()))
                .setBizProjectId(bizProjId)
                .setProjectName(projectName)
                .setObjName(deviceCache.getName())
                .setTenantId(tenantId);

        alarmPushApi.alarmPush(pushReq);
    }
}
