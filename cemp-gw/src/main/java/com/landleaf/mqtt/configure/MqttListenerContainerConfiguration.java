package com.landleaf.mqtt.configure;

import com.landleaf.gw.context.GwContext;
import com.landleaf.gw.listener.CustomMqttListener;
import com.landleaf.gw.service.JsConvertService;
import com.landleaf.mqtt.client.MqttReceiveClient;
import com.landleaf.mqtt.core.MqttListener;
import com.landleaf.mqtt.meta.MqttMessageMeta;
import com.landleaf.mqtt.support.CustomMqttListenerContainer;
import com.landleaf.mqtt.support.DefaultMqttListenerContainer;
import com.landleaf.mqtt.support.DefaultMqttMessageConverter;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 重写部分逻辑
 *
 * @author Yang
 */
@Configuration
public class MqttListenerContainerConfiguration implements ApplicationContextAware {

    private final AtomicLong counter = new AtomicLong(0);

    private ConfigurableApplicationContext applicationContext;

    private final DefaultMqttMessageConverter mqttMessageConverter;

    private final MqttReceiveClient receiveClient;

    public MqttListenerContainerConfiguration(DefaultMqttMessageConverter mqttMessageConverter, MqttReceiveClient receiveClient) {
        this.mqttMessageConverter = mqttMessageConverter;
        this.receiveClient = receiveClient;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    public void initContainer() {
        // 不同点，修改为根据topic配置去实例化listener以及创建listener的Container
        JsConvertService jsConvertService = this.applicationContext.getBean("jsConvertService", JsConvertService.class);
        GwContext gwContext = this.applicationContext.getBean("gwContext", GwContext.class);

        Map<String, String> topics = gwContext.getUpTopics();
        for (String topic : topics.keySet()) {
            CustomMqttListener customMqttListener = new CustomMqttListener(jsConvertService);
            MqttMessageMeta messageMeta = new MqttMessageMeta();
            messageMeta.setTopic(topic);
            this.registerContainer(customMqttListener, messageMeta);
        }
    }

    private void registerContainer(Object bean, MqttMessageMeta messageMeta) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + "is not instance of" + MqttListener.class.getName());
        }
        String containerBeanName = String.format("%s_%s", DefaultMqttListenerContainer.class.getName(), counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(containerBeanName, DefaultMqttListenerContainer.class,
                () -> createMqttListenerContainer(containerBeanName, bean, messageMeta));
        DefaultMqttListenerContainer container = genericApplicationContext.getBean(containerBeanName, DefaultMqttListenerContainer.class);
        String topicReg = getTopicReg(messageMeta.getTopic());
        receiveClient.putContainer(topicReg, container);
        container.subscribe();
    }

    /**
     * 获取topic正则
     *
     * @param originalTopic 原始toic
     * @return 转换后正则topic
     */
    private String getTopicReg(String originalTopic) {
        return originalTopic.replace("$", "\\$")
                .replaceAll("\\+", "\\.\\+")
                .replace("#", ".*");
    }


    private CustomMqttListenerContainer createMqttListenerContainer(String name, Object bean, MqttMessageMeta messageMeta) {
        CustomMqttListenerContainer container = new CustomMqttListenerContainer();

        container.setMqttMessageMeta(messageMeta);
        if (MqttListener.class.isAssignableFrom(bean.getClass())) {
            container.setMqttListener((MqttListener) bean);
        }
        container.setReceiveClient(receiveClient);
        container.setMessageConverter(mqttMessageConverter.getMessageConverter());
        container.setName(name);
        return container;
    }
}
