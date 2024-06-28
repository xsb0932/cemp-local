package com.landleaf.gw.listener.mqtt;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.gw.conf.LgcConstance;
import com.landleaf.gw.domain.Pv;
import com.landleaf.gw.domain.screen.response.OverviewResponse;
import com.landleaf.gw.domain.screen.response.PvResponse;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.mqtt.annotation.MqttMessageListener;
import com.landleaf.mqtt.core.MqttListener;
import com.landleaf.mqtt.core.MqttTemplate;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.redis.constance.KeyConstance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.landleaf.gw.domain.screen.enums.LgcConstants.*;
import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS;


/**
 * 储能
 */
@Component
@Slf4j
@AllArgsConstructor
@MqttMessageListener(topic = "/lgc/PC0027/#")
public class PVListener extends BaseListener implements MqttListener<String> {

    private final MonitorApi monitorApi;

    private final MqttTemplate mqttTemplate;

    @Override
    public void onMessage(String topic, String msgStr) {

        String bizProdId = getBizProdId(topic);
        log.info("receive msg from mqtt, topic is :{}, content is : {}", topic, msgStr);
        Pv msg = JSONUtil.toBean(msgStr, Pv.class);
        String outerDeviceId = getOuterDeviceId(topic, msg.getDeviceId());
        Response<String> response = monitorApi.getdevice(outerDeviceId);
        String bizDeviceId = response.isSuccess() ? response.getResult() : "";
        Map<String, Object> valMap = msg.toMap();
        // 将信息入库
        if (MapUtil.isEmpty(valMap)) {
            // TODO 记录在线
            return;
        }
        // 依次判断值是否相等，不相等则推送kafka，入influx
//        Map<String, String> tags = Maps.newHashMap();
//        tags.put("biz_device_id", bizDeviceId);
//        tags.put("biz_tenant_id", LgcConstance.BIZ_TENANT_ID);
//        tags.put("biz_project_id", LgcConstance.BIZ_PROJECT_ID);
//        tags.put("biz_node_id", LgcConstance.BIZ_NODE_ID);

        // 补上设备在线
        valMap.put("CST", LgcConstance.CST_ONLINE);
        Map<String, Object> currentMapNew = valMap.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> String.valueOf(v.getValue())));

        if (ZNB1.equals(bizDeviceId) || ZNB2.equals(bizDeviceId)) {
            //大屏-光伏
            mqttTemplate.publish("/screen/lgc/device", ZNB1.equals(bizDeviceId) ? buildPv1FPv(currentMapNew, bizDeviceId) : buildPv2FPv(currentMapNew, bizDeviceId), MqttQoS.AT_LEAST_ONCE);
            //大屏-概览
            mqttTemplate.publish("/screen/lgc/device", ZNB1.equals(bizDeviceId) ? buildPv1FOverview(currentMapNew, bizDeviceId) : buildPv2FOverview(currentMapNew, bizDeviceId), MqttQoS.AT_LEAST_ONCE);
        }

//        if (!MapUtil.isEmpty(currentMapNew)) {
//            redisUtils.hmset(KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId, currentMapNew);
//            // 插入influx
//            influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
//                    .fields(valMap).time(System.currentTimeMillis() / 1000, TimeUnit.SECONDS)
//                    .build());
//
//        }
    }

    private String buildPv1FOverview(Map<String, Object> currentMapNew, String bizDeviceId) {
        OverviewResponse response = new OverviewResponse();
        Map<String, Object> currentPv2 = deviceCurrentApi.getDeviceCurrentById(ZNB2).getResult();
        OverviewResponse.Middle middle = new OverviewResponse.Middle();
        middle.setElectricityPvEnergyProductionCurrentDay(NumberUtil.add(new BigDecimal(String.valueOf(currentMapNew.get("EpexpDay"))), new BigDecimal(String.valueOf(currentPv2.get("EpexpDay")))));
        middle.setPvCurrentActiveP(NumberUtil.add(new BigDecimal(String.valueOf(currentMapNew.get("P"))), new BigDecimal(String.valueOf(currentPv2.get("P")))));
        response.setMiddle(middle);
        JSONObject jo = JSONObject.from(response);
        jo.put("pageCode", "/screen/lgc/overview");
        jo.put("bizDeviceId", bizDeviceId);
        return jo.toJSONString();
    }

    private String buildPv2FOverview(Map<String, Object> currentMapNew, String bizDeviceId) {
        OverviewResponse response = new OverviewResponse();
        Map<String, Object> currentPv1 = deviceCurrentApi.getDeviceCurrentById(ZNB1).getResult();
        OverviewResponse.Middle middle = new OverviewResponse.Middle();
        middle.setElectricityPvEnergyProductionCurrentDay(NumberUtil.add(new BigDecimal(String.valueOf(currentMapNew.getOrDefault("EpexpDay", "0"))), new BigDecimal(String.valueOf(currentPv1.getOrDefault("EpexpDay", "0")))));
        middle.setPvCurrentActiveP(NumberUtil.add(new BigDecimal(String.valueOf(currentMapNew.getOrDefault("P", "0"))), new BigDecimal(String.valueOf(currentPv1.getOrDefault("P", "0")))));
        response.setMiddle(middle);
        JSONObject jo = JSONObject.from(response);
        jo.put("pageCode", "/screen/lgc/overview");
        jo.put("bizDeviceId", bizDeviceId);
        return jo.toJSONString();
    }

    private String buildPv1FPv(Map<String, Object> currentMapNew, String bizDeviceId) {
        PvResponse response = new PvResponse();
        PvResponse.Znb01 znb01 = new PvResponse.Znb01();
        PvResponse.Middle middle = new PvResponse.Middle();
        znb01.setPv1(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV1U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV1I")))));
        znb01.setPv2(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV3U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV3I")))));
        znb01.setPv3(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV5U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV5I")))));
        znb01.setPv4(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV7U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV7I")))));
        znb01.setPv5(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV9U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV9I")))));
        znb01.setPv6(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV11U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV11I")))));
        znb01.setPv7(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV12U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV12I")))));
        znb01.setPv8(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV13U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV13I")))));
        znb01.setPv9(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV15U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV15I")))));
        znb01.setPv10(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV17U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV17I")))));
        znb01.setPv11(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV19U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV19I")))));
        znb01.setTemp(new BigDecimal(String.valueOf(currentMapNew.get("Temperature"))));
        znb01.setUa(new BigDecimal(String.valueOf(currentMapNew.get("Ua"))));
        znb01.setIa(new BigDecimal(String.valueOf(currentMapNew.get("Ia"))));
        znb01.setUb(new BigDecimal(String.valueOf(currentMapNew.get("Ub"))));
        znb01.setIb(new BigDecimal(String.valueOf(currentMapNew.get("Ib"))));
        znb01.setUc(new BigDecimal(String.valueOf(currentMapNew.get("Uc"))));
        znb01.setIc(new BigDecimal(String.valueOf(currentMapNew.get("Ic"))));
        znb01.setP(new BigDecimal(String.valueOf(currentMapNew.get("P"))));
        znb01.setQ(new BigDecimal(String.valueOf(currentMapNew.get("Q"))));
        znb01.setCurrentElectricity(new BigDecimal(String.valueOf(currentMapNew.get("EpexpDay"))));
        znb01.setEfficiency(new BigDecimal(String.valueOf(currentMapNew.get("E"))));
        znb01.setPInput(new BigDecimal(String.valueOf(currentMapNew.get("PInput"))));
        middle.setZnb01(znb01);
        middle.setZnb02(null);
        response.setMiddle(middle);
        JSONObject jo = JSONObject.from(response);
        jo.put("pageCode", "/screen/lgc/pv");
        jo.put("bizDeviceId", bizDeviceId);
        return jo.toJSONString();
    }

    private String buildPv2FPv(Map<String, Object> currentMapNew, String bizDeviceId) {
        PvResponse response = new PvResponse();
        PvResponse.Znb02 znb02 = new PvResponse.Znb02();
        PvResponse.Middle middle = new PvResponse.Middle();
        znb02.setPv1(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV1U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV1I")))));
        znb02.setPv2(new PvResponse.Pv(new BigDecimal(String.valueOf(currentMapNew.get("PV2U"))), new BigDecimal(String.valueOf(currentMapNew.get("PV2I")))));
        znb02.setEfficiency(new BigDecimal(String.valueOf(currentMapNew.get("E"))));
        znb02.setTemp(new BigDecimal(String.valueOf(currentMapNew.get("Temperature"))));
        znb02.setUa(new BigDecimal(String.valueOf(currentMapNew.get("Ua"))));
        znb02.setIa(new BigDecimal(String.valueOf(currentMapNew.get("Ia"))));
        znb02.setUb(new BigDecimal(String.valueOf(currentMapNew.get("Ub"))));
        znb02.setIb(new BigDecimal(String.valueOf(currentMapNew.get("Ib"))));
        znb02.setUc(new BigDecimal(String.valueOf(currentMapNew.get("Uc"))));
        znb02.setIc(new BigDecimal(String.valueOf(currentMapNew.get("Ic"))));
        znb02.setP(new BigDecimal(String.valueOf(currentMapNew.get("P"))));
        znb02.setQ(new BigDecimal(String.valueOf(currentMapNew.get("Q"))));
        znb02.setCurrentElectricity(new BigDecimal(String.valueOf(currentMapNew.get("EpexpDay"))));
        znb02.setEfficiency(new BigDecimal(String.valueOf(currentMapNew.get("E"))));
        znb02.setPInput(new BigDecimal(String.valueOf(currentMapNew.get("PInput"))));
        middle.setZnb02(znb02);
        middle.setZnb01(null);
        response.setMiddle(middle);
        JSONObject jo = JSONObject.from(response);
        jo.put("pageCode", "/screen/lgc/pv");
        jo.put("bizDeviceId", bizDeviceId);
        return jo.toJSONString();
    }

}
