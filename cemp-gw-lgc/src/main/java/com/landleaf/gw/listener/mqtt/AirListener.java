package com.landleaf.gw.listener.mqtt;


import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.gw.conf.LgcConstance;
import com.landleaf.gw.domain.Air;
import com.landleaf.gw.service.LgcDeviceService;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.mqtt.annotation.MqttMessageListener;
import com.landleaf.mqtt.core.MqttListener;
import com.landleaf.redis.constance.KeyConstance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 气表的监听
 */
@Component
@Slf4j
@AllArgsConstructor
@MqttMessageListener(topic = "/lgc/PC0026/#")
public class AirListener extends BaseListener implements MqttListener<String> {

    private final MonitorApi monitorApi;

    @Override
    public void onMessage(String topic, String msgStr) {
        String bizProdId = getBizProdId(topic);
        log.info("receive msg from mqtt, topic is :{}, content is : {}", topic, msgStr);
//        Air msg = JSONUtil.toBean(msgStr, Air.class);
//        String outerDeviceId = getOuterDeviceId(topic, msg.getDeviceId());
//        Response<String> response = monitorApi.getdevice(outerDeviceId);
//        String bizDeviceId =response.isSuccess() ? response.getResult() :"";
//        Map<String, Object> valMap = msg.toMap();
//        // 将信息入库
//        if (MapUtil.isEmpty(valMap)) {
//            // TODO 记录在线
//            return;
//        }
        // 依次判断值是否相等，不相等则推送kafka，入influx
//        Map<String, String> tags = Maps.newHashMap();
//        tags.put("biz_device_id", bizDeviceId);
//        tags.put("biz_tenant_id", LgcConstance.BIZ_TENANT_ID);
//        tags.put("biz_project_id", LgcConstance.BIZ_PROJECT_ID);
//        tags.put("biz_node_id", LgcConstance.BIZ_NODE_ID);
//
//        // 补上设备在线
//        valMap.put("CST", LgcConstance.CST_ONLINE);
//        Map<String, Object> currentMapNew = valMap.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> String.valueOf(v.getValue())));
//
//        if (!MapUtil.isEmpty(currentMapNew)) {
//            redisUtils.hmset(KeyConstance.DEVICE_CURRENT_STATUS + bizDeviceId, currentMapNew);
//            // 插入influx
//            influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
//                    .fields(valMap).time(System.currentTimeMillis() / 1000, TimeUnit.SECONDS)
//                    .build());
//        }
    }
}
