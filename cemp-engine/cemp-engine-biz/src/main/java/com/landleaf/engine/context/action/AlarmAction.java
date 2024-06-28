package com.landleaf.engine.context.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.AlarmPushApi;
import com.landleaf.bms.api.GatewayApi;
import com.landleaf.bms.api.dto.AlarmPushRequest;
import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.bms.api.dto.ProductAlarmConfListResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.engine.domain.vo.RuleActionVO;
import com.landleaf.engine.domain.vo.RuleDetailVO;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.dto.AlarmAddRequest;
import com.landleaf.monitor.dto.CurrentAlarmResponse;
import com.landleaf.monitor.dto.ProductAlarmConf;
import com.landleaf.monitor.enums.*;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.dao.DeviceCacheDao;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.share.GatewayCache;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
public class AlarmAction extends BaseAction {

    private static AlarmApi alarmApi = SpringUtil.getBean(AlarmApi.class);

    private static GatewayCache gatewayCache = SpringUtil.getBean(GatewayCache.class);

    private static GatewayApi gatewayApi = SpringUtil.getBean(GatewayApi.class);

    private static AlarmPushApi alarmPushApi = SpringUtil.getBean(AlarmPushApi.class);

    private static DeviceCacheDao deviceCacheDao = SpringUtil.getBean(DeviceCacheDao.class);

    @Override
    public boolean hasExecuted(String bizDeviceId, RuleActionVO ruleActionVO) {
        /// 从redis查
        // 当设备为其它设备，或当前上报信息中并不包含对应的设备的属性时，需要从redis中获取对应的属性。
        // 此处，为了性能，自己执行redis的查询操作，不走dataApi。
        Map<String, Object> alarm = (Map<String, Object>) redisUtils.hget(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "alarm");
        // 按照原来的逻辑，这部分应该只取property
        if (null != alarm && alarm.containsKey(ruleActionVO.getAlarmCode())) {
            Map<String, Object> temp = (Map<String, Object>) alarm.get(ruleActionVO.getAlarmCode());
            return !"0".equals(String.valueOf(temp.get("val")));
        }
        return false;
    }

    @Override
    public void executeTrigger(String bizDeviceId, RuleDetailVO detail, JSONObject obj) {
        String gateId = obj.getString("gateId");
        Response<GatewayProjectResponse> resp = null;
        Response cacheResp = gatewayCache.getCache(gateId, Response.class);
        if (null != cacheResp) {
            resp = new Response<GatewayProjectResponse>();
            resp.setSuccess(cacheResp.isSuccess());
            resp.setResult(JSON.parseObject(String.valueOf(cacheResp.getResult()), GatewayProjectResponse.class));
        }
        if (null == resp) {
            resp = gatewayApi.getProjectInfoByBizId(gateId);
            if (resp.isSuccess()) {
                gatewayCache.setCache(gateId, resp);
            }
        }
        if (!resp.isSuccess() || null == resp.getResult()) {
            log.info("无法获取网关信息,触发告警返回。{}", gateId);
            return;
        }

        RuleActionVO actionVO = detail.getActionVO();
        ProductAlarmConfListResponse alarmInfo = new ProductAlarmConfListResponse();
        alarmInfo.setAlarmCode(actionVO.getAlarmCode());
        String alarmDesc = actionVO.getAlarmTriggerDesc();
        alarmDesc = alarmDesc.replace("{规则标题}", detail.getName());
        alarmDesc = alarmDesc.replace("{触发/复归}", "触发");
        alarmInfo.setAlarmDesc(alarmDesc);
        alarmInfo.setAlarmRelapseLevel(AlarmConfirmTypeEnum.AUTO.getCode());
        alarmInfo.setAlarmTriggerLevel(AlarmConfirmTypeEnum.AUTO.getCode());
        alarmInfo.setAlarmConfirmType(actionVO.getAlarmTriggerConfirmType());
        addCurrentAlarm(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(), alarmInfo, bizDeviceId, AlarmTypeEnum.RULE_ALARM.getCode(), AlarmStatusEnum.TRIGGER_RESET.getCode(), System.currentTimeMillis());
        Map<String, Object> alarm = (Map<String, Object>) redisUtils.hget(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "alarm");
        // 按照原来的逻辑，这部分应该只取property
        if (null == alarm) {
            alarm = Maps.newHashMap();
        }
        Map<String, Object> temp = Maps.newHashMap();
        temp.put("time", System.currentTimeMillis());
        temp.put("val", AlarmStatusEnum.TRIGGER_RESET.getCode());
        alarm.put(actionVO.getAlarmCode(), temp);
        redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "alarm", alarm);
    }

    @Override
    public void executeRelapse(String bizDeviceId, RuleDetailVO detail, JSONObject obj) {
        RuleActionVO actionVO = detail.getActionVO();
        Response<CurrentAlarmResponse> currentAlarmResp = alarmApi.delCurrentAlarmByCode(bizDeviceId, actionVO.getAlarmCode());
        if (!currentAlarmResp.isSuccess()) {
            return;
        }
        String alarmBizId = null;
        if (null != currentAlarmResp.getResult()) {
            alarmBizId = currentAlarmResp.getResult().getAlarmBizId();
        }

        // 新增history
        String gateId = obj.getString("gateId");
        Response<GatewayProjectResponse> resp = null;
        Response cacheResp = gatewayCache.getCache(gateId, Response.class);
        if (null != cacheResp) {
            resp = new Response<GatewayProjectResponse>();
            resp.setSuccess(cacheResp.isSuccess());
            resp.setResult(JSON.parseObject(String.valueOf(cacheResp.getResult()), GatewayProjectResponse.class));
        }
        if (null == resp) {
            resp = gatewayApi.getProjectInfoByBizId(gateId);
            if (resp.isSuccess()) {
                gatewayCache.setCache(gateId, resp);
            }
        }
        if (!resp.isSuccess() || null == resp.getResult()) {
            log.info("无法获取网关信息,触发告警返回。{}", gateId);
            return;
        }

        ProductAlarmConfListResponse alarmInfo = new ProductAlarmConfListResponse();
        alarmInfo.setAlarmCode(actionVO.getAlarmCode());
        String alarmDesc = actionVO.getAlarmRelapseDesc();
        alarmDesc = alarmDesc.replace("{规则标题}", detail.getName());
        alarmDesc = alarmDesc.replace("{触发/复归}", "复归");
        alarmInfo.setAlarmDesc(alarmDesc);
        alarmInfo.setAlarmRelapseLevel(AlarmConfirmTypeEnum.AUTO.getCode());
        alarmInfo.setAlarmTriggerLevel(AlarmConfirmTypeEnum.AUTO.getCode());
        alarmInfo.setAlarmRelapseConfirmType(actionVO.getAlarmRelapseConfirmType());
        addHisEvent(resp.getResult().getTenantId(), resp.getResult().getProjectBizId(), resp.getResult().getProjectName(),alarmInfo, bizDeviceId, AlarmTypeEnum.RULE_ALARM.getCode(), AlarmStatusEnum.RESET.getCode(), alarmBizId, null, System.currentTimeMillis(), EventTypeEnum.ALARM_EVENT.getCode());
        Map<String, Object> alarm = (Map<String, Object>) redisUtils.hget(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "alarm");
        // 按照原来的逻辑，这部分应该只取property
        if (null == alarm) {
            alarm = Maps.newHashMap();
        }
        Map<String, Object> temp = Maps.newHashMap();
        temp.put("time", System.currentTimeMillis());
        temp.put("val", AlarmStatusEnum.RESET.getCode());
        alarm.put(actionVO.getAlarmCode(), temp);
        redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "alarm", alarm);
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

        // add push
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
                .setAlarmLevel(alarmInfo.getAlarmRelapseLevel())
                .setEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()))
                .setBizProjectId(bizProjId)
                .setProjectName(projectName)
                .setObjName(deviceCache.getName())
                .setTenantId(tenantId);

        alarmPushApi.alarmPush(pushReq);
    }
}
