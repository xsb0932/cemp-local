package com.landleaf.messaging.listener.kafka;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.landleaf.kafka.conf.TopicDefineConst;
import com.landleaf.kafka.receive.BaseKafkaListener;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.messaging.config.AlarmCodeConstance;
import com.landleaf.messaging.config.AlarmStatusEnum;
import com.landleaf.messaging.config.AlarmTypeEnum;
import com.landleaf.messaging.config.MsgContextTypeEnum;
import com.landleaf.messaging.service.DeviceAlarmService;
import com.landleaf.messaging.service.DeviceCtsService;
import com.landleaf.messaging.service.DeviceEventService;
import com.landleaf.messaging.service.DeviceInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class DeviceStatusUploadListener extends BaseKafkaListener {

    @Resource
    private DeviceInfoService deviceInfoServiceImpl;

    @Resource
    private DeviceCtsService deviceCtsServiceImpl;

    @Resource
    private KafkaSender kafkaSender;

    @Resource
    private DeviceAlarmService deviceAlarmServiceImpl;

    @Resource
    private DeviceEventService deviceEventServiceImpl;

    @KafkaListener(clientIdPrefix = "${spring.kafka.consumer-id}", idIsGroup = false, groupId = "${spring.kafka.consumer-group}", topicPattern = "${spring.kafka.topic-pattern}", concurrency = "8")
    public void listen(String in) {
        log.info("Receive device status update info from platform, content is : {}", in);
        try {

            JSONObject obj = JSONUtil.parseObj(in);
            // 解析消息，处理设备信息
            String gateId = obj.getStr("gateId");
            String pkId = obj.getStr("pkId");
            String sourceDevId = obj.getStr("sourceDevId");

            long time = obj.getLong("time");

            // 通过gateId, pkId和sourceDevId获取bizDeviceId;
            String bizDeviceId = deviceInfoServiceImpl.queryBizDeviceIdByOuterId(gateId, pkId, sourceDevId);
            if (!StringUtils.hasText(bizDeviceId)) {
                // 直接return；gg了
                sendBizDeviceIdResp(gateId, pkId, sourceDevId, null);
                return;
            }
            // 超时时间转为ms
            long timeout = deviceInfoServiceImpl.getTimeout(bizDeviceId) * 1000;
            // 判断通讯状态是否ok， 如果不ok，则执行登录注册
            Map<Object, Object> deviceStatusObj = deviceInfoServiceImpl.queryDeviceCurrentStatus(bizDeviceId);
            if (MapUtil.isEmpty(deviceStatusObj) || !deviceStatusObj.containsKey("CST")) {
                // 注册,将bizDeviceId返回网关
                sendBizDeviceIdResp(gateId, pkId, sourceDevId, bizDeviceId);
            } else {
                Map<String, Object> cstInfo = (Map<String, Object>) deviceStatusObj.get("CST");
                if (AlarmCodeConstance.CST_OFFLINE.equals(cstInfo.get("val"))) {
                    // 设备离线。
                    sendBizDeviceIdResp(gateId, pkId, sourceDevId, bizDeviceId);
                }
            }
            // 修改设备在线状态
            deviceCtsServiceImpl.refreshDeviceConnStatus(gateId, pkId, bizDeviceId, deviceStatusObj, time, timeout);

            // 如果property不为空，则更新对应的property
            if (obj.containsKey("propertys")) {
                JSONObject property = obj.getJSONObject("propertys");
                if (property.size() > 0) {
                    deviceInfoServiceImpl.refreshDeviceCurrentStatus(MsgContextTypeEnum.PROPERTYS.getType(), gateId, pkId, bizDeviceId, deviceStatusObj, property, time);
                }
            }

            // 如果parameters不为空，则更新对应的parameters
            if (obj.containsKey("parameters")) {
                JSONObject parameters = obj.getJSONObject("parameters");
                if (parameters.size() > 0) {
                    deviceInfoServiceImpl.refreshDeviceCurrentParameter(MsgContextTypeEnum.PARAMETERS.getType(), gateId, pkId, bizDeviceId, deviceStatusObj, parameters, time);
                }
            }

            // 如果又事件，则处理对应的事件
            if (obj.containsKey("events")) {
                JSONObject events = obj.getJSONObject("events");
                Set<String> eventKeys = events.keySet();
                eventKeys.forEach(eventKey -> {
                    if (eventKey.equals("devAlarm")) {
                        JSONObject devAlarm = events.getJSONObject("devAlarm");
                        // 按pd的要求，alarmCodeList与statusList一一对应
                        JSONArray alarmCodeList = devAlarm.getJSONArray("alarmCodeList");
                        JSONArray statusList = devAlarm.getJSONArray("statusList");
                        for (int i = 0; i < alarmCodeList.size(); i++) {
                            deviceAlarmServiceImpl.dealAlarmInfo(gateId, pkId, bizDeviceId, AlarmTypeEnum.DEVICE_ALARM.getCode(), deviceStatusObj, time, alarmCodeList.get(i).toString(), statusList.get(i).toString());
                        }
                    } else if (!eventKey.equals("conAlarm")) {
                        JSONObject otherEvent = events.getJSONObject(eventKey);
                        deviceEventServiceImpl.dealEventInfo(gateId, pkId, bizDeviceId, deviceStatusObj, time, eventKey, otherEvent);
                    }
                });
            }
        } catch (Exception e) {
            log.error("处理下消息失败。", e);
        }
    }

    private void sendBizDeviceIdResp(String gateId, String pkId, String sourceDevId, String bizDeviceId) {
        JSONObject returnObj = new JSONObject();
        returnObj.set("gateId", gateId);
        returnObj.set("pkId", pkId);
        returnObj.set("sourceDevId", sourceDevId);
        returnObj.set("time", System.currentTimeMillis());
        JSONObject register = new JSONObject();
        if (StringUtils.hasText(bizDeviceId)) {
            register.set("platDevId", bizDeviceId);
            register.set("respondCode", "0");
        } else {
            register.set("platDevId", "");
            register.set("respondCode", "200");
        }
        returnObj.set("events", register);
        kafkaSender.send(TopicDefineConst.DEVICE_STATUS_UPLOAD_TOPIC_ACK + gateId, returnObj.toString());
    }
}
