package com.landleaf.mqtt.factory;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.mqtt.client.MqttSendClient;
import com.landleaf.mqtt.constant.MqttConstant;
import com.landleaf.mqtt.exception.MqttBusinessException;
import com.landleaf.mqtt.props.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt发送公共类
 */
public class MqttSendFactory {

    private static final Logger log = LoggerFactory.getLogger(MqttSendFactory.class);

    private final MqttProperties mqttProperties;

    public MqttSendFactory(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    private Integer maxSize = 16;

    private final List<MqttSendClient> clients =  new CopyOnWriteArrayList<>();

    private int index = 0;

    public void init() {
        int tempSize = maxSize;
        if(mqttProperties.getSendClientMaxSize() > 0){
            maxSize = mqttProperties.getSendClientMaxSize();
        }
        ExecutorService pool = new ThreadPoolExecutor(
                tempSize,
                tempSize * 2,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNamePrefix("mqtt-send-create-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i< maxSize; i++){
            pool.execute(() -> {
                String tempClientId = mqttProperties.getClientId() + StrUtil.UNDERLINE + MqttConstant.FIX_SEND + StrUtil.UNDERLINE + IdUtil.fastSimpleUUID();
                MqttSendClient client = new MqttSendClient(mqttConnectOptions(), mqttProperties, tempClientId);
                client.connect();
                log.info("初始化发送客户端:{}", tempClientId);
                clients.add(client);
            });
        }
    }

    /**
     * 获取factory对应的client
     * @return client
     */
    public MqttSendClient client(){
        int i = 0;
        //如果3次取到的链接都有问题，放弃吧
        while (i < MqttConstant.RETRY_TIME) {
            //暂时不考虑，index的线程安全问题
            int sub = Math.abs(++index % maxSize);
            MqttSendClient client = clients.get(sub);
            boolean connectFlag = client.checkConnect();
            if (connectFlag) {
                return client;
            }
            i++;
        }
        throw new MqttBusinessException("发布消息时获取MqttSendClient客户端异常!");
    }


    /**
     * MqttConnectOptions 相关配置
     * @return 相关配置对象
     */
    private MqttConnectOptions mqttConnectOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setConnectionTimeout(mqttProperties.getTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepAlive());
        options.setAutomaticReconnect(mqttProperties.isReconnect());
        options.setCleanSession(mqttProperties.isCleanSession());
        return options;
    }
}
