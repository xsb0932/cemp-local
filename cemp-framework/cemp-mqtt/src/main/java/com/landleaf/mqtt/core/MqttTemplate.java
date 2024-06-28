package com.landleaf.mqtt.core;

import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.mqtt.factory.MqttSendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt模版方法
 */
public class MqttTemplate {

    private static final Logger log = LoggerFactory.getLogger(MqttTemplate.class);


    private final MqttSendFactory mqttSendFactory;

    public MqttTemplate(MqttSendFactory mqttSendFactory) {
        this.mqttSendFactory = mqttSendFactory;
    }

    /**
     * 发送mqtt消息
     * @param topic 主题
     * @param msg 消息体
     * @param qoS qos
     */
    public void publish(String topic, Object msg, MqttQoS qoS){
        try {
            mqttSendFactory.client().publish(topic, msg, qoS);
        } catch (Exception e) {
            log.error("发送mqtt消息:{}",e.getMessage(), e);
        }
    }
}
