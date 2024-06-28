package com.landleaf.messaging;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.messaging.api.ServiceControlApi;
import com.landleaf.messaging.api.dto.SendServiceRequest;
import com.landleaf.messaging.config.AlarmCodeConstance;
import com.landleaf.messaging.config.MsgContextTypeEnum;
import com.landleaf.messaging.service.DeviceCtsService;
import com.landleaf.messaging.service.DeviceInfoService;
import com.landleaf.monitor.api.MonitorApi;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
public class MessagingApplicationTest {


    @Autowired
    private DeviceInfoService deviceInfoServiceImpl;
    @Autowired
    private DeviceCtsService deviceCtsServiceImpl;

    @Resource
    private ServiceControlApi serviceControlApiImp;

    @Resource
    private MonitorApi monitorApi;

    @Test
    public void testApiImpl() {
        SendServiceRequest req = new SendServiceRequest();
        req.setBizDeviceId("D000000001281");
        req.setIdentifier("a11");
        req.setTime(System.currentTimeMillis());
        req.setBizProdId("PK00001254");
        req.setBizProjId("PJ00001021");
        req.setServiceParameter(new ArrayList<>());
        req.setSourceDeviceId("D000000001281");
        System.out.println(serviceControlApiImp.sendService(req));
    }

    @Test
    public void testSendNotify() {
        Response<Boolean> resp = monitorApi.changeStatusNotice("D00010010100", "PK00001216", "Ua", null, null);
        System.out.println(JSON.toJSONString(resp));
        System.out.println(resp.getErrorCode());
    }

    @Test
    public void test() {
        String in = new String("{\"pkId\":\"PK00001216\",\"sourceDevId\":\"PV-CONV-1\",\"propertys\":{\"CST\":1,\"UInput\":\"42.10\",\"UOutput\":\"42.20\",\"IInput\":\"0.00\",\"IOutput\":\"0.00\",\"TempBox\":\"29.00\",\"TempCooler\":\"29.00\",\"UOutputSet\":\"760.00\",\"IOutputSet\":\"10.00\"},\"events\":{\"devAlarm\":{\"alarmCodeList\":{\"0\":\"YxGW\",\"1\":\"YxSCGY\",\"2\":\"YxSRGY\"},\"statusList\":{\"0\":0,\"1\":0,\"2\":0}}},\"gateId\":\"GW00000004\",\"time\":1702396826003}");
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
            return;
        }
        // 超时时间转为ms
        long timeout = deviceInfoServiceImpl.getTimeout(bizDeviceId) * 1000;
        // 判断通讯状态是否ok， 如果不ok，则执行登录注册
        Map<Object, Object> deviceStatusObj = deviceInfoServiceImpl.queryDeviceCurrentStatus(bizDeviceId);
        if (MapUtil.isEmpty(deviceStatusObj) || !deviceStatusObj.containsKey("CST")) {
            // 注册,将bizDeviceId返回网关
        } else {
            Map<String, Object> cstInfo = (Map<String, Object>) deviceStatusObj.get("CST");
            if (AlarmCodeConstance.CST_OFFLINE.equals(cstInfo.get("val"))) {
                // 设备离线。
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
                deviceInfoServiceImpl.refreshDeviceCurrentStatus(MsgContextTypeEnum.PARAMETERS.getType(), gateId, pkId, bizDeviceId, deviceStatusObj, parameters, time);
            }
        }

        // 如果又事件，则处理对应的事件
        if (obj.containsKey("events")) {
            JSONObject events = obj.getJSONObject("events");

            // 判断有无告警事件
            if (events.containsKey("devAlarm")) {
                JSONObject devAlarm = events.getJSONObject("devAlarm");
                // 按pd的要求，alarmCodeList与statusList一一对应
                JSONArray alarmCodeList = devAlarm.getJSONArray("alarmCodeList");
                JSONArray statusList = devAlarm.getJSONArray("statusList");
                for (int i = 0; i < alarmCodeList.size(); i++) {
                }
            }
        }
    }
}
