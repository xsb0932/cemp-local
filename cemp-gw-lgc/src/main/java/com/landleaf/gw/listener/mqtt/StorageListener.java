package com.landleaf.gw.listener.mqtt;


import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.gw.conf.LgcConstance;
import com.landleaf.gw.domain.Storage;
import com.landleaf.gw.domain.screen.enums.StoragePcsRSTEnum;
import com.landleaf.gw.domain.screen.response.OverviewResponse;
import com.landleaf.gw.domain.screen.response.StorageResponse;
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
import static com.landleaf.gw.domain.screen.constance.KeyConstance.DEVICE_CURRENT_STATUS;


/**
 * 储能
 */
@Component
@Slf4j
@AllArgsConstructor
@MqttMessageListener(topic = "/lgc/PC0028/#")
public class StorageListener extends BaseListener implements MqttListener<String> {

    private final MonitorApi monitorApi;

    private final MqttTemplate mqttTemplate;

    @Override
    public void onMessage(String topic, String msgStr) {
        String bizProdId = getBizProdId(topic);
        log.info("receive msg from mqtt, topic is :{}, content is : {}", topic, msgStr);
        Storage msg = JSONUtil.toBean(msgStr, Storage.class);
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

        if (!MapUtil.isEmpty(currentMapNew)) {
            if (STORAGE_DEVICE_BIZ_ID.equals(bizDeviceId)) {
                //大屏-储能
                mqttTemplate.publish("/screen/lgc/device", buildFStorage(currentMapNew, bizDeviceId), MqttQoS.AT_LEAST_ONCE);
                //大屏-概览
                mqttTemplate.publish("/screen/lgc/device", buildFOverview(currentMapNew, bizDeviceId), MqttQoS.AT_LEAST_ONCE);
            }

//            redisUtils.hmset(KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId, currentMapNew);
//            // 插入influx
//            influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
//                    .fields(valMap).time(System.currentTimeMillis() / 1000, TimeUnit.SECONDS)
//                    .build());
        }
    }

    private String buildFOverview(Map<String, Object> currentMapNew, String bizDeviceId) {
        OverviewResponse response = new OverviewResponse();
        OverviewResponse.Middle middle = new OverviewResponse.Middle();
        //运行状态
        middle.setStorageCurrentRunningStatus(StoragePcsRSTEnum.getName(String.valueOf(currentMapNew.get("pcsRST"))));
        // 储能系统实时SOC
        middle.setStorageCurrentSoc(new BigDecimal(String.valueOf(currentMapNew.get("SOC"))));
        // 储能系统实时有功功率
        middle.setStorageCurrentActiveP(new BigDecimal(String.valueOf(currentMapNew.get("P"))));
        response.setMiddle(middle);
        JSONObject jo = JSONObject.from(response);
        jo.put("pageCode", "/screen/lgc/overview");
        jo.put("bizDeviceId", bizDeviceId);
        return jo.toJSONString();
    }

    private String buildFStorage(Map<String, Object> currentMapNew, String bizDeviceId) {
        StorageResponse response = new StorageResponse();
        StorageResponse.Middle middle = new StorageResponse.Middle();
        // PCS状态
        middle.setPcsStatus(StoragePcsRSTEnum.getName(String.valueOf(currentMapNew.get("pcsRST"))));
        // 总压
        middle.setBatteryU(new BigDecimal(String.valueOf(currentMapNew.get("BatteryU"))));
        // 电流
        middle.setBatteryI(new BigDecimal(String.valueOf(currentMapNew.get("BatteryI"))));
        // 最高单体电压
        middle.setMaxVoltage(new BigDecimal(String.valueOf(currentMapNew.get("UnitMaxU"))));
        // 最低单体电压
        middle.setMinVoltage(new BigDecimal(String.valueOf(currentMapNew.get("UnitMinU"))));
        // 最高单体温度
        middle.setMaxTemp(new BigDecimal(String.valueOf(currentMapNew.get("UnitMaxT"))));
        // 最低单体温度
        middle.setMinTemp(new BigDecimal(String.valueOf(currentMapNew.get("UnitMinT"))));
        // Uab
        middle.setUab(new BigDecimal(String.valueOf(currentMapNew.get("Uab"))));
        // Ubc
        middle.setUbc(new BigDecimal(String.valueOf(currentMapNew.get("Ubc"))));
        // Uca
        middle.setUca(new BigDecimal(String.valueOf(currentMapNew.get("Uca"))));
        // Ia
        middle.setIa(new BigDecimal(String.valueOf(currentMapNew.get("Ia"))));
        // Ib
        middle.setIb(new BigDecimal(String.valueOf(currentMapNew.get("Ib"))));
        // Ic
        middle.setIc(new BigDecimal(String.valueOf(currentMapNew.get("Ic"))));
        // F
        middle.setF(new BigDecimal(String.valueOf(currentMapNew.get("F"))));
        // P
        middle.setP(new BigDecimal(String.valueOf(currentMapNew.get("P"))));
        // Q
        middle.setQ(new BigDecimal(String.valueOf(currentMapNew.get("Q"))));
        // PF
        middle.setPf(new BigDecimal(String.valueOf(currentMapNew.get("PF"))));
        //当日充电量
        middle.setUsageCurrentDay(new BigDecimal(String.valueOf(currentMapNew.get("dayChargeE"))));
        //当日放电量
        middle.setProductionCurrentDay(new BigDecimal(String.valueOf(currentMapNew.get("dayDischargeE"))));
        response.setMiddle(middle);
        response.setSoc(new BigDecimal(String.valueOf(currentMapNew.get("SOC"))));
        response.setSoh(new BigDecimal(String.valueOf(currentMapNew.get("SOH"))));
        response.setCurrentP(new BigDecimal(String.valueOf(currentMapNew.get("P"))));

        JSONObject jo = JSONObject.from(response);
        jo.put("pageCode", "/screen/lgc/storage");
        jo.put("bizDeviceId", bizDeviceId);
        return jo.toJSONString();
    }
}
