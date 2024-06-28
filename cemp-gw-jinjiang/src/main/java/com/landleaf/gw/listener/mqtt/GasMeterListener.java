package com.landleaf.gw.listener.mqtt;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.landleaf.gw.conf.JjConstance;
import com.landleaf.gw.domain.GasMeter;
import com.landleaf.influx.util.MeasurementFindUtil;
import com.landleaf.mqtt.annotation.MqttMessageListener;
import com.landleaf.mqtt.core.MqttListener;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static com.landleaf.kafka.conf.TopicDefineConst.DEVICE_STATUS_UPLOAD_TOPIC;
import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS;

/**
 * 气表的监听
 */
@Component
@Slf4j
@MqttMessageListener(topic = "/jj/PC0002/#")
public class GasMeterListener extends BaseListener implements MqttListener<String> {

    @Override
    public void onMessage(String topic, String msgStr) {
        String bizProdId = getBizProdId(topic);
        log.info("receive msg from mqtt, topic is :{}, content is : {}", topic, msgStr);
        GasMeter msg = JSONUtil.toBean(msgStr, GasMeter.class);
        String outerDeviceId = getOuterDeviceId(topic, msg.getDeviceId());
        String bizDeviceId = deviceRelationServiceImpl.getBizDeviceIdBySupplierAndOuterId(JjConstance.GAUGES_SUPPLIER_ID, outerDeviceId);
        Map<String, Object> valMap = msg.toMap();
        // 将信息入库
        if (MapUtil.isEmpty(valMap)) {
            // TODO 记录在线
            return;
        }
        Map<Object, Object> currentMap = redisUtils.hmget(DEVICE_CURRENT_STATUS + bizDeviceId);
        // 依次判断值是否相等，不相等则推送kafka，入influx

        Map<String, String> tags = Maps.newHashMap();
        tags.put("biz_device_id", bizDeviceId);
        tags.put("biz_tenant_id", JjConstance.BIZ_TENANT_ID);
        tags.put("biz_project_id", JjConstance.BIZ_PROJECT_ID);
        tags.put("biz_node_id", JjConstance.BIZ_NODE_ID);
        // 补上设备在线
        valMap.put("CST", JjConstance.CST_ONLINE);
        Map<String, Object> currentMapNew = valMap.entrySet().stream().collect(Collectors.toMap(k->k.getKey(), v->String.valueOf(v.getValue())));
        if (!MapUtil.isEmpty(currentMapNew)) {
            JSONObject kafkaSend = new JSONObject();
            JSONObject property = new JSONObject();
            kafkaSend.put("pkId","PK00000002");
            kafkaSend.put("sourceDevId",bizDeviceId);
            kafkaSend.put("gateId","GW00000003");
            kafkaSend.put("time",System.currentTimeMillis());
            BeanUtils.copyProperties(currentMapNew,property);
            for(Map.Entry<String,Object> entry : currentMapNew.entrySet()){
                property.put(entry.getKey(),entry.getValue());
            }
            sendKafka(JSON.toJSONString(kafkaSend));
            /*
            redisUtils.hmset(DEVICE_CURRENT_STATUS + bizDeviceId, currentMapNew);
            // 插入influx
            influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProdId)).tag(tags)
                    .fields(valMap)
                    .build());
             */
        }
    }

    private void sendKafka(String msg) {
        log.info("kafka send {}", msg);
        kafkaSender.send(DEVICE_STATUS_UPLOAD_TOPIC + "GW00000003", msg);
    }
}
