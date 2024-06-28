package com.landleaf.mqtt.client;

import cn.hutool.core.util.StrUtil;
import com.landleaf.mqtt.constant.MqttConstant;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.mqtt.exception.MqttBusinessException;
import com.landleaf.mqtt.props.MqttProperties;
import com.landleaf.mqtt.support.MqttListenerContainer;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt接收客户端
 */
public class MqttReceiveClient {

    private static final Logger log = LoggerFactory.getLogger(MqttReceiveClient.class);

    private final MqttProperties mqttProperties;

    private final Executor mqttReceiveExecutor;

    public MqttReceiveClient(MqttProperties mqttProperties, Executor mqttReceiveExecutor) {
        this.mqttProperties = mqttProperties;
        this.mqttReceiveExecutor = mqttReceiveExecutor;
    }

    private final Map<String, MqttListenerContainer> containers = new ConcurrentHashMap<>();

    /**
     * 客户端存储方式
     */
    private MqttClientPersistence persistence;

    /**
     * 单例客户端
     */
    private static MqttClient client = null;

    public static MqttClient getClient() {
        return client;
    }

    /**
     * 客户端连接
     */
    public void connect() {
        try {
            persistence = new MemoryPersistence();
            String tempClientId = mqttProperties.getClientId() + StrUtil.UNDERLINE + MqttConstant.FIX_RECEIVE;
            client = new MqttClient(mqttProperties.getServerUrl(), tempClientId, persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            options.setConnectionTimeout(mqttProperties.getTimeout());
            options.setKeepAliveInterval(mqttProperties.getKeepAlive());
            options.setAutomaticReconnect(mqttProperties.isReconnect());
            options.setCleanSession(mqttProperties.isCleanSession());
            // 设置回调
            client.setCallback(new MqttReceiveCallback());
            client.connect(options);
        } catch (Exception e) {
            log.error("mqtt连接时发生错误,{}", e.getMessage(), e);
        }
    }

    /**
     * 重新连接
     */
    public synchronized void reconnection() {
        if (client == null) {
            log.warn("MqttReceiveClient 客户端清空，开始重新连接......");
            this.connect();
        }
        if (!client.isConnected()) {
            log.warn("MqttReceiveClient 断开连接，开始重新连接......");
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
     * 关闭连接
     */
    public void close() {
        try {
            if (persistence != null) {
                persistence.close();
            }

            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
            }
        } catch (MqttException e) {
            log.error("mqtt关闭连接时发生错误,{}", e.getMessage(), e);
        }
    }

    /**
     * 放入container容器
     *
     * @param topicReg  主题正则-key
     * @param container 现为默认容器
     */
    public void putContainer(String topicReg, MqttListenerContainer container) {
        containers.put(topicReg, container);
    }

    /**
     * 订阅主题
     *
     * @param topic 主题topic
     * @param qoS   消息qos
     */
    public void subscribe(String topic, MqttQoS qoS) {
        log.debug("================开始订阅主题================ {}", topic);
        try {
            client.subscribe(topic, qoS.getValue());
        } catch (MqttException e) {
            log.error("mqtt订阅主题发生错误,topic:{},{}", topic, e.getMessage(), e);
        }
    }


    /**
     * 取消订阅主题
     *
     * @param topic 主题topic
     */
    public void unsubscribe(String topic) {
        log.debug("================开始取消订阅主题================ {}", topic);
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            log.error("mqtt取消订阅主题发生错误,topic:{},{}", topic, e.getMessage(), e);
        }
    }

    /**
     * 另起线程监听客户端连接状态
     */
    public void check() {
        ExecutorService checkExecutor = Executors.newFixedThreadPool(1);
        checkExecutor.submit(() -> {
            while (true) {
                boolean conFlag = this.checkConnect();
                if (!conFlag) {
                    MqttReceiveClient.this.containers.forEach((k, v) -> v.subscribe());
                }
                TimeUnit.SECONDS.sleep(this.mqttProperties.getKeepAlive());
            }
        });
    }

    /**
     * 监听内部类
     */
    class MqttReceiveCallback implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean b, String s) {
            log.debug("================clientId:{},客户端连接成功================", MqttReceiveClient.getClient().getClientId());
        }

        @Override
        public void connectionLost(Throwable throwable) {
            log.warn("mqtt断开连接，准备重新连接");
            if (MqttReceiveClient.getClient() == null || !MqttReceiveClient.getClient().isConnected()) {
                log.warn("mqtt断开连接，开始重新连接......");
                MqttReceiveClient.this.reconnection();
                MqttReceiveClient.this.containers.forEach((k, v) -> v.subscribe());
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            log.debug("接收消息主题 : {},接收消息QoS : {},接收消息内容 : {}", topic, mqttMessage.getQos(), new String(mqttMessage.getPayload()));
            Map<String, MqttListenerContainer> tempMap = MqttReceiveClient.this.containers;
            Optional<String> first = tempMap.keySet().stream().filter(topic::matches).findFirst();
            if (!first.isPresent()) {
                throw new MqttBusinessException("无匹配的mqtt topic对应:" + topic);
            }
            MqttListenerContainer mqttListenerContainer = tempMap.get(first.get());
            MqttReceiveClient.this.mqttReceiveExecutor.execute(() -> mqttListenerContainer.handleMessage(topic, mqttMessage));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //发布消息成功，暂时打印日志
            String[] topics = token.getTopics();
            for (String topic : topics) {
                log.debug("向主题:{},发送消息成功!", topic);
            }
            try {
                MqttMessage message = token.getMessage();
                byte[] payload = message.getPayload();
                String s = new String(payload, StandardCharsets.UTF_8);
                log.debug("消息的内容是：" + s);
            } catch (MqttException e) {
                log.error("获取消息内容失败:{}", e.getMessage(), e);
            }
        }
    }
}
