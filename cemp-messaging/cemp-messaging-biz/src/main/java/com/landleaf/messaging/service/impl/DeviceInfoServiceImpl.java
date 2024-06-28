package com.landleaf.messaging.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.GatewayApi;
import com.landleaf.bms.api.dto.DeviceConnResponse;
import com.landleaf.bms.api.dto.GatewayProjectResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.messaging.config.AlarmCodeConstance;
import com.landleaf.messaging.service.DeviceInfoService;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.dao.DeviceCacheDao;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import com.landleaf.redis.share.GatewayCache;
import jakarta.annotation.Resource;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DeviceIotApi deviceIotApi;

    @Resource
    private GatewayApi gatewayApi;

    @Resource
    private InfluxdbTemplate influxdbTemplate;

    @Resource
    private MonitorApi monitorApi;

    @Resource
    private DeviceCacheDao deviceCacheDao;

    @Resource
    private GatewayCache gatewayCache;

    @Override
    public String queryBizDeviceIdByOuterId(String gateId, String pkId, String sourceDevId) {
        // 从redis获取对应的信息
        Object val = redisUtils.hget(KeyConstance.OUTER_DEVICE_RELATION, String.format(KeyConstance.OUTER_DEVICE_KEY, gateId, pkId, sourceDevId));
        String bizDeviceId = null;
        if (null != val) {
            bizDeviceId = String.valueOf(val);
        } else {
            // 从feign拿
            DeviceConnResponse deviceConnResp = deviceIotApi.queryBizDeviceIdByOuterId(gateId, pkId, sourceDevId).getResult();
            if (null != deviceConnResp) {
                bizDeviceId = deviceConnResp.getBizDeviceId();
                redisUtils.hset(KeyConstance.DEVICE_TIME_OUT_CACHE, bizDeviceId, deviceConnResp.getTimeout());
                redisUtils.hset(KeyConstance.DEVICE_GATEWAY_CACHE, bizDeviceId, gateId);
                // 放入redis
                redisUtils.hset(KeyConstance.OUTER_DEVICE_RELATION, String.format(KeyConstance.OUTER_DEVICE_KEY, gateId, pkId, sourceDevId), bizDeviceId);
                redisUtils.hset(KeyConstance.DEVICE_OUTER_RELATION, String.format(KeyConstance.DEVICE_OUTER_KEY, gateId, pkId, bizDeviceId), sourceDevId);
            }
        }
        return bizDeviceId;
    }

    @Override
    public Map<Object, Object> queryDeviceCurrentStatus(String bizDeviceId) {
        return redisUtils.hmget(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId);
    }

    @Override
    public void refreshDeviceCurrentStatus(String updateType, String bizGateId, String bizProdId, String bizDeviceId, Map<Object, Object> deviceStatusObj, JSONObject currentVal, long time) {
        Map<String, Object> info = (Map<String, Object>) deviceStatusObj.get(updateType);
        if (null == info) {
            info = new HashMap<>();
        }
        // 开始判断时间更新
        Map<String, Object> temp = null;
        boolean hasUpdate = false;

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
            return;
        }
        GatewayProjectResponse gatewayInfo = resp.getResult();
        Map<String, String> tags = Maps.newHashMap();
        tags.put("biz_device_id", bizDeviceId);
        tags.put("biz_tenant_id", gatewayInfo.getBizTenantId());
        tags.put("biz_project_id", gatewayInfo.getProjectBizId());
        tags.put("biz_node_id", gatewayInfo.getParentNodeBizId());
        Map<String, Object> valMap = new HashMap<>();
        for (String key : currentVal.keySet()) {
            Object tval = currentVal.get(key);
            int transVal = 0;
            boolean needTrans = false;
            if ("TRUE".equals(tval)) {
                needTrans = true;
                transVal = 1;
            } else if ("FALSE".equals(tval)) {
                needTrans = true;
                transVal = 0;
            }
            // info不包含对应key，直接信息
            if (!info.containsKey(key)) {
                temp = Maps.newHashMap();
                temp.put("time", time);
                temp.put("val", needTrans ? transVal : tval);
                // add2history
                info.put(key, temp);
                valMap.put(key, needTrans ? new BigDecimal(transVal) : currentVal.getBigDecimal(key));
                hasUpdate = true;
            } else {
                temp = (Map<String, Object>) info.get(key);
                if (time > (long) temp.get("time")) {
                    // 更新
                    temp.put("time", time);
                    temp.put("val", needTrans ? transVal : tval);
                    // add2history
                    info.put(key, temp);
                    valMap.put(key, needTrans ? new BigDecimal(transVal) : currentVal.getBigDecimal(key));
                    hasUpdate = true;
                } else {
                    // 历史消息了， 弃了
                    continue;
                }
            }
        }
        if (hasUpdate) {
            valMap.put("CST", Integer.valueOf(AlarmCodeConstance.CST_ONLINE));
            redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, updateType, info);
            // 需求新增：【ID1004253】设备存储和推送的数据自动补足
            Map<String, Object> newValMap = new HashMap<>();
            newValMap.putAll(valMap);
            info.forEach((k, v) -> {
                if (!newValMap.containsKey(k)) {
                    newValMap.put(k, new BigDecimal(String.valueOf((((Map<String, Object>) v).get("val")))));
                }
            });
            influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
                    .fields(newValMap).time(time, TimeUnit.MILLISECONDS)
                    .build());
            monitorApi.changeAllStatusNotice(bizDeviceId, bizProdId, time, valMap);
        }
    }

    @Override
    public void refreshDeviceCurrentParameter(String updateType, String bizGateId, String bizProdId, String bizDeviceId, Map<Object, Object> deviceStatusObj, JSONObject currentVal, long time) {
        Map<String, Object> info = (Map<String, Object>) deviceStatusObj.get(updateType);
        if (null == info) {
            info = new HashMap<>();
        }
        // 开始判断时间更新
        Map<String, Object> temp = null;
        boolean hasUpdate = false;

        Response<GatewayProjectResponse> resp = gatewayApi.getProjectInfoByBizId(bizGateId);
        if (!resp.isSuccess() || null == resp.getResult()) {
            return;
        }
        GatewayProjectResponse gatewayInfo = resp.getResult();
        Map<String, String> valMap = new HashMap<>();
        for (String key : currentVal.keySet()) {
            // info不包含对应key，直接信息
            if (!info.containsKey(key)) {
                temp = Maps.newHashMap();
                temp.put("time", time);
                temp.put("val", currentVal.get(key));
                // add2history
                info.put(key, temp);
                valMap.put(key, currentVal.getStr(key));
                hasUpdate = true;
            } else {
                temp = (Map<String, Object>) info.get(key);
                if (time > (long) temp.get("time")) {
                    // 更新
                    temp.put("time", time);
                    temp.put("val", currentVal.get(key));
                    // add2history
                    info.put(key, temp);
                    valMap.put(key, currentVal.getStr(key));
                    hasUpdate = true;
                } else {
                    // 历史消息了， 弃了
                    continue;
                }
            }
        }
        if (hasUpdate) {
            redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, updateType, info);
            // 将valMap新增或update置DB
            deviceIotApi.updateDeviceParameterVal(gatewayInfo.getTenantId(), gatewayInfo.getProjectBizId(), bizProdId, bizDeviceId, valMap);
        }
    }

    @Override
    public DeviceInfoCacheDTO getPkId(String bizDeviceId) {
        DeviceInfoCacheDTO deviceInfo = deviceCacheDao.getDeviceInfoCache(bizDeviceId);
        return deviceInfo;
    }

    @Override
    public String getGatewayId(DeviceInfoCacheDTO deviceInfo) {
        Object gatewayId = redisUtils.hget(KeyConstance.DEVICE_GATEWAY_CACHE, deviceInfo.getBizDeviceId());
        if (null == gatewayId) {
            // 查询并set
            Response<List<String>> resp = gatewayApi.findBizIdByProjAndProdId(deviceInfo.getBizProjectId(), deviceInfo.getBizProductId());
            if (!resp.isSuccess() || CollectionUtil.isEmpty(resp.getResult())) {
                return null;
            }
            redisUtils.hset(KeyConstance.DEVICE_GATEWAY_CACHE, deviceInfo.getBizDeviceId(), resp.getResult().get(0));
            return resp.getResult().get(0);
        } else {
            return String.valueOf(gatewayId);
        }
    }

    /**
     * 获取timeouot
     *
     * @param bizDeviceId
     * @return
     */
    @Override
    public long getTimeout(String bizDeviceId) {
        BigDecimal timeout = new BigDecimal(String.valueOf(redisUtils.hget(KeyConstance.DEVICE_TIME_OUT_CACHE, bizDeviceId)));
        return timeout.longValue();
    }
}
