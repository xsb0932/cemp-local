package com.landleaf.mqtt.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.mqtt.props.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt 发送客户端
 */
public class MqttSendClient {

    private static final Logger log = LoggerFactory.getLogger(MqttSendClient.class);

    private MqttClient client;

    private final MqttConnectOptions options;

    private final MqttProperties mqttProperties;

    private final String clientId;

    public MqttSendClient(MqttConnectOptions options, MqttProperties mqttProperties, String clientId) {
        this.options = options;
        this.mqttProperties = mqttProperties;
        this.clientId = clientId;
    }


    /**
     * 连接发送客户端
     *
     * @return 发送客户端
     */
    public MqttClient connect() {
        try {
            client = new MqttClient(mqttProperties.getServerUrl(), clientId, new MemoryPersistence());
            client.setCallback(null);
            client.connect(options);
        } catch (Exception e) {
            log.error("MqttSendClient连接时发生错误,{}", e.getMessage(), e);
        }
        return client;
    }

    /**
     * 重新连接
     */
    public synchronized void reconnection() {
        if (client == null) {
            log.warn("MqttSendClient 客户端清空，开始重新连接......");
            this.connect();
        }
        if (!client.isConnected()) {
            log.warn("MqttSendClient 断开连接，开始重新连接......");
            this.connect();
        }
    }

    /**
     * 检查连接状态
     *
     * @return 连接状态
     */
    public boolean checkConnect() {
        if (client == null || !client.isConnected()) {
            this.reconnection();
            return false;
        }
        return true;
    }

    /**
     * 发布mqtt消息
     *
     * @param topic 主题
     * @param msg   消息
     * @param qoS   qos
     */
    public void publish(String topic, Object msg, MqttQoS qoS) {
        //校验
        if (StrUtil.isBlank(topic) || msg == null) {
            return;
        }
        //组装发送消息体
        MqttMessage message = new MqttMessage();
        if (qoS != null) {
            message.setQos(qoS.getValue());
        }
        //转换发送msg
        String pubMsg;
        if (msg instanceof String) {
            pubMsg = (String) msg;
        } else {
            pubMsg = JSONUtil.toJsonStr(msg);
        }
        message.setPayload(pubMsg.getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            log.error("MqttSendClient发送消息时发生错误,{}", e.getMessage(), e);
        }
    }
}
