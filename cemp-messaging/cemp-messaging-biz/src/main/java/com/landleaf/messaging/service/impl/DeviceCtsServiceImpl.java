package com.landleaf.messaging.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.landleaf.messaging.config.AlarmCodeConstance;
import com.landleaf.messaging.config.AlarmStatusEnum;
import com.landleaf.messaging.config.AlarmTypeEnum;
import com.landleaf.messaging.config.MsgContextTypeEnum;
import com.landleaf.messaging.domain.DeviceLastCommunicationInfo;
import com.landleaf.messaging.queue.DelayQueueManager;
import com.landleaf.messaging.service.DeviceAlarmService;
import com.landleaf.messaging.service.DeviceCtsService;
import com.landleaf.messaging.service.DeviceInfoService;
import com.landleaf.redis.RedisUtils;
import com.landleaf.redis.constance.KeyConstance;
import com.landleaf.redis.dao.dto.DeviceInfoCacheDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeviceCtsServiceImpl implements DeviceCtsService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DelayQueueManager delayQueueManager;

    @Resource
    private DeviceAlarmService deviceAlarmServiceImpl;

    @Resource
    private DeviceInfoService deviceInfoServiceImpl;

    @Value("${cemp.refresh-flag:0}")
    private Integer refreshConn;

    /**
     * 重置信息
     */
    @PostConstruct
    private void init() {
        if (0 == refreshConn) {
            return;
        }
        List<String> list = redisUtils.scan(KeyConstance.DEVICE_CURRENT_STATUS_V1 + "*");
        for (String s : list) {
            String bizDeviceId = s.replace(KeyConstance.DEVICE_CURRENT_STATUS_V1, "");
            Map<Object, Object> deviceStatusObj = deviceInfoServiceImpl.queryDeviceCurrentStatus(bizDeviceId);
            // 如果当前离线，则不处理
            if (deviceStatusObj.containsKey("CST")) {
                Map<String, Object> cstInfo = (Map<String, Object>) deviceStatusObj.get("CST");
                if (AlarmCodeConstance.CST_ONLINE.equals(cstInfo.get("val"))) {
                    // 处理
                    DeviceLastCommunicationInfo info = new DeviceLastCommunicationInfo();
                    DeviceInfoCacheDTO deviceInfo = deviceInfoServiceImpl.getPkId(bizDeviceId);
                    if (null == deviceInfo) {
                        continue;
                    }
                    info.setPkId(deviceInfo.getBizProductId());
                    String gatewayId = deviceInfoServiceImpl.getGatewayId(deviceInfo);
                    if (!StringUtils.hasText(gatewayId)) {
                        continue;
                    }
                    info.setGateId(gatewayId);
                    info.setBizDeviceId(bizDeviceId);
                    info.setLastCommunicationTime((long) cstInfo.get("time"));
                    info.setRetainTime(deviceInfoServiceImpl.getTimeout(bizDeviceId));
                    log.info("加载判断设备离线：{}", JSON.toJSONString(info));
                    delayQueueManager.addQueueMsg(info);
                }
            }
        }
    }

    @Override
    public void refreshDeviceConnStatus(String gateId, String pkId, String bizDeviceId, Map<Object, Object> deviceStatusObj, long time, long timeout) {
        // 判断设备cst状态是否变更
        Map<String, Object> cstInfo = null;
        if (MapUtil.isEmpty(deviceStatusObj) || !deviceStatusObj.containsKey("CST")) {
            cstInfo = new HashMap<>();
        } else {
            cstInfo = (Map<String, Object>) deviceStatusObj.get("CST");
        }
        deviceStatusObj.put("CST", cstInfo);
        if (!cstInfo.containsKey("time")) {
            // 如果time+timeout小于当前时间，则可以直接认为设备离线了
            if (time + timeout < System.currentTimeMillis()) {
                cstInfo.put("time", time);
                cstInfo.put("val", AlarmCodeConstance.CST_OFFLINE);
                redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "CST", cstInfo);
                deviceAlarmServiceImpl.dealAlarmInfo(gateId, pkId, bizDeviceId, AlarmTypeEnum.CON_ALARM.getCode(), deviceStatusObj, time, AlarmCodeConstance.CONN_ALARM_CODE, AlarmStatusEnum.CONN_ERROR.getCode());
                return;
            }
        } else {
            if (time <= (long) cstInfo.get("time")) {
                // 设备的更新状态，比此条消息晚，所以，忽略次消息
                return;
            }
            // 时间已经超时，并且， 状态也是离线。更新下时间即可。（为了保护补数据的逻辑）
            if (time + timeout < System.currentTimeMillis()) {
                cstInfo.put("time", time);
                redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "CST", cstInfo);
                return;
            }
        }
        cstInfo.put("time", time);
        cstInfo.put("val", AlarmCodeConstance.CST_ONLINE);
        redisUtils.hset(KeyConstance.DEVICE_CURRENT_STATUS_V1 + bizDeviceId, "CST", cstInfo);
        // 修改bug,设备离线后，恢复通讯，不会更新alarm。 此处，需要判断CST的原始状态，和对应的event
        deviceAlarmServiceImpl.dealAlarmInfo(gateId, pkId, bizDeviceId, AlarmTypeEnum.CON_ALARM.getCode(), deviceStatusObj, time, AlarmCodeConstance.CONN_ALARM_CODE, AlarmStatusEnum.CONN_OK.getCode());
        // 更新设备的离线时间
        DeviceLastCommunicationInfo info = new DeviceLastCommunicationInfo();
        info.setGateId(gateId);
        info.setPkId(pkId);
        info.setBizDeviceId(bizDeviceId);
        info.setLastCommunicationTime(time);
        info.setRetainTime(timeout);
        delayQueueManager.addQueueMsg(info);
    }

    @Override
    public void dealDeviceConnStatus(DeviceLastCommunicationInfo msg) {
        log.info("判断设备离线：{}", JSON.toJSONString(msg));
        Map<Object, Object> deviceStatusObj = redisUtils.hmget(KeyConstance.DEVICE_CURRENT_STATUS_V1 + msg.getBizDeviceId());
        Map<String, Object> cstInfo = (Map<String, Object>) deviceStatusObj.get("CST");
        if (null != cstInfo && msg.getLastCommunicationTime() < (long) cstInfo.get("time")) {
            // 设备的更新状态，比此条消息晚，所以，忽略次消息
            return;
        }
        // 设备离线
        deviceAlarmServiceImpl.dealAlarmInfo(msg.getGateId(), msg.getPkId(), msg.getBizDeviceId(), AlarmTypeEnum.CON_ALARM.getCode(), deviceStatusObj, null != cstInfo ? (long) cstInfo.get("time") : msg.getLastCommunicationTime(), AlarmCodeConstance.CONN_ALARM_CODE, AlarmStatusEnum.CONN_ERROR.getCode());
    }
}
