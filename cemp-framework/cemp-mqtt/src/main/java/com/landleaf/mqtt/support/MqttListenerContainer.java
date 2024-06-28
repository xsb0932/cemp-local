package com.landleaf.mqtt.support;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt 监听容器接口
 */
public interface MqttListenerContainer extends DisposableBean {

    /**
     * 处理mqtt消息
     * @param mqttMessage mqtt消息
     */
    void handleMessage(String topic, MqttMessage mqttMessage);

    /**
     * 生成对象后进行订阅
     */
    void subscribe();
}
