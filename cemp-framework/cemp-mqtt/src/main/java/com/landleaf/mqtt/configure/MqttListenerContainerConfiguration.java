package com.landleaf.mqtt.configure;

import com.landleaf.mqtt.annotation.MqttMessageListener;
import com.landleaf.mqtt.client.MqttReceiveClient;
import com.landleaf.mqtt.core.MqttListener;
import com.landleaf.mqtt.support.DefaultMqttListenerContainer;
import com.landleaf.mqtt.support.DefaultMqttMessageConverter;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt监听配置
 */
@Configuration
public class MqttListenerContainerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {

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

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(MqttMessageListener.class).entrySet().stream()
                .filter((entry) -> !ScopedProxyUtils.isScopedTarget(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        beans.forEach(this::registerContainer);

    }

    private void registerContainer(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + "is not instance of" + MqttListener.class.getName());
        }
        MqttMessageListener annotation = clazz.getAnnotation(MqttMessageListener.class);
        String containerBeanName = String.format("%s_%s", DefaultMqttListenerContainer.class.getName(), counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(containerBeanName, DefaultMqttListenerContainer.class,
                () -> createMqttListenerContainer(containerBeanName, bean, annotation));
        DefaultMqttListenerContainer container = genericApplicationContext.getBean(containerBeanName, DefaultMqttListenerContainer.class);
        String topicReg = getTopicReg(annotation.topic());
        receiveClient.putContainer(topicReg, container);
        container.subscribe();
    }

    public void registerDynamicContainer(String beanName, Object bean) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + "is not instance of" + MqttListener.class.getName());
        }
        MqttMessageListener annotation = clazz.getAnnotation(MqttMessageListener.class);
        String containerBeanName = String.format("%s_%s", DefaultMqttListenerContainer.class.getName(), counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(containerBeanName, DefaultMqttListenerContainer.class,
                () -> createMqttListenerContainer(containerBeanName, bean, annotation));
        DefaultMqttListenerContainer container = genericApplicationContext.getBean(containerBeanName, DefaultMqttListenerContainer.class);
        String topicReg = getTopicReg(annotation.topic());
        //String topicReg = "/lgc/PC0002/.*";
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


    private DefaultMqttListenerContainer createMqttListenerContainer(String name, Object bean, MqttMessageListener annotation) {
        DefaultMqttListenerContainer container = new DefaultMqttListenerContainer();

        container.setMqttMessageListener(annotation);
        if (MqttListener.class.isAssignableFrom(bean.getClass())) {
            container.setMqttListener((MqttListener) bean);
        }
        container.setReceiveClient(receiveClient);
        container.setMessageConverter(mqttMessageConverter.getMessageConverter());
        container.setName(name);
        return container;
    }
}
