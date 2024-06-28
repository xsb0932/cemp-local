package com.landleaf.mqtt.support;

import com.landleaf.mqtt.annotation.MqttMessageListener;
import com.landleaf.mqtt.client.MqttReceiveClient;
import com.landleaf.mqtt.constant.MqttConstant;
import com.landleaf.mqtt.core.MqttListener;
import com.landleaf.mqtt.enums.MqttMessageModel;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.mqtt.exception.MqttBusinessException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description 默认mqtt监听容器(参考rocketmq)
 */
public class DefaultMqttListenerContainer implements MqttListenerContainer, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DefaultMqttListenerContainer.class);

    /**
     * 创建对象时赋值
     */
    private String name;

    private String topic;

    private MessageConverter messageConverter;

    private MqttListener mqttListener;

    private MqttMessageListener mqttMessageListener;

    private MqttReceiveClient receiveClient;

    /**
     * 初始化后赋值
     */
    private Type messageType;

    private MethodParameter methodParameter;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.messageType = getMessageType();
        this.methodParameter = getMethodParameter();
        log.debug("mqtt messageType: {}", messageType);
    }

    private Type getMessageType() {
        if(mqttListener == null){
            throw new MqttBusinessException("mqtt 消费者没有被注入，请检查!");
        }
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(mqttListener);
        Type matchedGenericInterface = null;
        while (Objects.nonNull(targetClass)) {
            Type[] interfaces = targetClass.getGenericInterfaces();
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType && Objects.equals(((ParameterizedType) type).getRawType(), MqttListener.class) ) {
                    matchedGenericInterface = type;
                    break;
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        if (Objects.isNull(matchedGenericInterface)) {
            return Object.class;
        }
        Type[] actualTypeArguments = ((ParameterizedType) matchedGenericInterface).getActualTypeArguments();
        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
            return actualTypeArguments[0];
        }
        return Object.class;
    }

    private MethodParameter getMethodParameter() {
        if(mqttListener == null){
            throw new MqttBusinessException("mqtt 消费者没有被注入，请检查!");
        }
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(mqttListener);
        Type messageType = this.getMessageType();
        Class clazz;
        if (messageType instanceof ParameterizedType && messageConverter instanceof SmartMessageConverter) {
            clazz = (Class) ((ParameterizedType) messageType).getRawType();
        } else if (messageType instanceof Class) {
            clazz = (Class) messageType;
        } else {
            throw new RuntimeException("parameterType:" + messageType + " of onMessage method is not supported");
        }
        try {
            final Method method = targetClass.getMethod("onMessage", String.class, clazz);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("parameterType:" + messageType + " of onMessage method is not supported");
        }
    }

    @Override
    public void destroy() throws Exception {
        // 暂无逻辑
    }

    /**
     * 监听topic
     */
    @Override
    public void subscribe(){
        String topic = mqttMessageListener.topic();
        MqttQoS mqttQoS = mqttMessageListener.mqttQoS();
        MqttMessageModel mqttMessageModel = mqttMessageListener.mqttMessageModel();
        if(MqttMessageModel.SHARE.equals(mqttMessageModel)){
            int groupIndex = mqttMessageListener.groupIndex();
            topic = MqttConstant.SHARE_TOPIC_PREFIX.replace(MqttConstant.REPLACE_INDEX, String.valueOf(groupIndex)) + topic;
        }
        receiveClient.subscribe(topic, mqttQoS);
    }

    @Override
    public void handleMessage(String topic, MqttMessage mqttMessage){
        if(mqttListener == null){
            throw new MqttBusinessException("mqtt 消费者没有被注入，请检查!");
        }
        mqttListener.onMessage(topic, doConvertMessage(mqttMessage));
    }

    private Object doConvertMessage(MqttMessage mqttMessage) {
        if (Objects.equals(messageType, MqttMessage.class)) {
            return mqttMessage;
        }
        String str = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
        if (Objects.equals(messageType, String.class)) {
            return str;
        }
        if (messageType instanceof Class) {
            return this.getMessageConverter().fromMessage(MessageBuilder.withPayload(str).build(), (Class<?>) messageType);
        } else {
            return ((SmartMessageConverter) this.getMessageConverter()).fromMessage(MessageBuilder.withPayload(str).build(), (Class<?>) ((ParameterizedType) messageType).getRawType(), methodParameter);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public MqttListener getMqttListener() {
        return mqttListener;
    }

    public void setMqttListener(MqttListener mqttListener) {
        this.mqttListener = mqttListener;
    }

    public MqttMessageListener getMqttMessageListener() {
        return mqttMessageListener;
    }

    public void setMqttMessageListener(MqttMessageListener mqttMessageListener) {
        this.mqttMessageListener = mqttMessageListener;
    }

    public MqttReceiveClient getReceiveClient() {
        return receiveClient;
    }

    public void setReceiveClient(MqttReceiveClient receiveClient) {
        this.receiveClient = receiveClient;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public void setMethodParameter(MethodParameter methodParameter) {
        this.methodParameter = methodParameter;
    }
}
