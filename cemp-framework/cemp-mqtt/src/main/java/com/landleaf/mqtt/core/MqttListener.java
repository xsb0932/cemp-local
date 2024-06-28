package com.landleaf.mqtt.core;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description  MQTT 消息处理接口类
 */
public interface MqttListener<T> {
    /**
     * 消息处理接口
     * @param msg 接收到的消息
     */
    void onMessage(String topic, T msg);
}
