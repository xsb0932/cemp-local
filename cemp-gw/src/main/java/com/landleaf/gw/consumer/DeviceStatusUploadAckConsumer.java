//package com.landleaf.gw.consumer;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.landleaf.kafka.receive.BaseKafkaListener;
//import com.landleaf.mqtt.core.MqttTemplate;
//import com.landleaf.mqtt.enums.MqttQoS;
//import com.landleaf.script.CempScriptUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import javax.script.ScriptException;
//
//import static com.landleaf.gw.context.GwConstant.PAYLOAD;
//import static com.landleaf.gw.context.GwConstant.TOPIC;
//import static com.landleaf.kafka.conf.TopicDefineConst.DEVICE_STATUS_UPLOAD_TOPIC_ACK;
//
///**
// * @author Yang
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DeviceStatusUploadAckConsumer extends BaseKafkaListener {
//    private final CempScriptUtil scriptUtil;
//    private final MqttTemplate mqttTemplate;
//
//    @KafkaListener(id = "consumer-in-${cemp.gateway-biz-id}-0",
//            groupId = "consumer-in-0",
//            idIsGroup = false,
//            topics = DEVICE_STATUS_UPLOAD_TOPIC_ACK + "${cemp.gateway-biz-id}")
//    public void listen(String in) {
//        log.info("DeviceStatusUploadAck : {}", in);
//        try {
//            Object obj = scriptUtil.handleDown(in);
//            JSONObject jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(obj));
//            String topic = jsonObject.getStr(TOPIC);
//            String payload = jsonObject.getStr(PAYLOAD);
//            if (StrUtil.isNotBlank(topic) && StrUtil.isNotBlank(payload)) {
//                log.info("down topic {} payload {}", topic, payload);
//                mqttTemplate.publish(topic, payload, MqttQoS.AT_LEAST_ONCE);
//            }
//        } catch (ScriptException | NoSuchMethodException e) {
//            log.info("handle down js error", e);
//        }
//    }
//}
