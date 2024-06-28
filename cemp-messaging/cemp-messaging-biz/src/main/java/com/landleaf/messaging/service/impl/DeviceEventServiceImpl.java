package com.landleaf.messaging.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.GatewayApi;
import com.landleaf.bms.api.ProductApi;
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
import com.landleaf.messaging.service.DeviceEventService;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.dto.AlarmAddRequest;
import com.landleaf.monitor.dto.ProductAlarmConf;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.share.GatewayCache;
import com.landleaf.redis.share.ProductAlarmConfCache;
import com.landleaf.redis.share.ProductEventConfCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class DeviceEventServiceImpl implements DeviceEventService {
    private final GatewayApi gatewayApi;

    private final ProductApi productApi;

    private final RedisUtils redisUtils;

    private final AlarmApi alarmApi;

    private final GatewayCache gatewayCache;
    private final ProductEventConfCache productEventConfCache;

    @Override
    public void dealEventInfo(String bizGateId, String pkId, String bizDeviceId, Map<Object, Object> deviceStatusObj, long time, String alarmCode, Map<String, Object> val) {
        Map<String, Object> info = (Map<String, Object>) deviceStatusObj.get(MsgContextTypeEnum.EVENT.getType());
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
            temp.put("val", JSON.toJSONString(val));
            // add2history
            info.put(alarmCode, temp);
            hasUpdate = true;
        } else {
            temp = (Map<String, Object>) info.get(alarmCode);
            if (time > (long) temp.get("time")) {
                // 更新
                temp.put("time", time);
                temp.put("val", JSON.toJSONString(val));
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
            // other
            // 其他事件，当前直接记录his，根据需求，其alarm信息需单独构建
            Response<List<ProductDeviceEventListResponse>> eventResp = null;
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
            if (!eventResp.isSuccess() || CollectionUtils.isEmpty(eventResp.getResult())) {
                log.info("无法获取事件信息,变更状态返回。{}", pkId);
                return;
            }
            ProductDeviceEventListResponse eventInfo = eventResp.getResult().stream().filter(i -> i.getIdentifier().equals(alarmCode)).findFirst().orElse(null);
            if (null == eventInfo || CollectionUtils.isEmpty(eventInfo.getEventParameter())) {
                log.info("otherEvent不存在,变更状态返回。{}", pkId);
                return;
            }
//            FunctionParameter eventParam = eventInfo.getEventParameter().stream().filter(i -> i.getIdentifier().equals(alarmCode)).findFirst().orElse(null);
//            if (null == eventParam) {
//                log.info("otherEvent定义的code不存在,变更状态返回。{}", pkId);
//                return;
//            }
            ProductAlarmConfListResponse alarmInfo = new ProductAlarmConfListResponse();
            alarmInfo.setAlarmCode(alarmCode);
            alarmInfo.setAlarmType(AlarmTypeEnum.OTHER_EVENT.getCode());
            alarmInfo.setAlarmDesc("设备发生" + eventInfo.getFunctionName() + "事件，事件参数：" + JSON.toJSONString(val));
            alarmInfo.setAlarmRelapseLevel(AlarmConfirmTypeEnum.AUTO.getCode());
            alarmInfo.setAlarmTriggerLevel(AlarmConfirmTypeEnum.AUTO.getCode());
            alarmInfo.setAlarmConfirmType(AlarmConfirmTypeEnum.AUTO.getCode());
            addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), alarmInfo, bizDeviceId, AlarmTypeEnum.OTHER_EVENT.getCode(), AlarmStatusEnum.TRIGGER_NO_RESET.getCode(), null, null, time, EventTypeEnum.COMMON_EVENT.getCode());
        }
        redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, MsgContextTypeEnum.EVENT.getType(), info);
    }

    private void addHisEvent(Long tenantId, String bizProjId, ProductAlarmConfListResponse alarmInfo, String bizDeviceId, String alarmType, String alarmStatus, String alarmBizId, String eventId, long time, String eventType) {
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
    }
}
