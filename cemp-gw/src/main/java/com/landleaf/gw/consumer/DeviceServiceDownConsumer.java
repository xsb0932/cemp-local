package com.landleaf.gw.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.landleaf.kafka.receive.BaseKafkaListener;
import com.landleaf.mqtt.core.MqttTemplate;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.script.CempScriptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

import static com.landleaf.gw.context.GwConstant.PAYLOAD;
import static com.landleaf.gw.context.GwConstant.TOPIC;
import static com.landleaf.kafka.conf.TopicDefineConst.DEVICE_SERVICE_WRITE_GATEWAY_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceServiceDownConsumer extends BaseKafkaListener {
    private final CempScriptUtil scriptUtil;
    private final MqttTemplate mqttTemplate;

    @KafkaListener(id = "device-service-in-${cemp.gateway-biz-id}",
            groupId = "device-service-in-0",
            idIsGroup = false,
            topics = DEVICE_SERVICE_WRITE_GATEWAY_TOPIC + "${cemp.gateway-biz-id}")
    public void listen(String in) {
        log.info("DeviceServiceDown : {}", in);
        try {
            Object obj = scriptUtil.handleDown(in);
            JSONObject jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(obj));
            String topic = jsonObject.getStr(TOPIC);
            Object payload = jsonObject.getObj(PAYLOAD);

            if (StrUtil.isNotBlank(topic) && null != payload) {
                log.info("down topic {} payload {}", topic, payload);
                mqttTemplate.publish(topic, payload, MqttQoS.AT_LEAST_ONCE);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            log.info("handle down js error", e);
        }
    }
}
