package com.landleaf.mqtt.configure;

import com.landleaf.mqtt.client.MqttReceiveClient;
import com.landleaf.mqtt.core.MqttTemplate;
import com.landleaf.mqtt.factory.MqttSendFactory;
import com.landleaf.mqtt.props.MqttProperties;
import com.landleaf.mqtt.support.MqttExecutor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executor;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt配置加载类
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
@ConditionalOnProperty(prefix = MqttProperties.PREFIX, value = "enable")
@Import({MqttMessageConverterConfiguration.class, MqttListenerContainerConfiguration.class, MqttExecutor.class})
@AutoConfigureAfter({MqttMessageConverterConfiguration.class})
@AutoConfigureBefore({MqttListenerContainerConfiguration.class})
public class MqttAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqttProperties mqttProperties(){
        return new MqttProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttReceiveClient receiveClient(MqttProperties mqttProperties, Executor mqttReceiveExecutor){
        MqttReceiveClient receiveClient = new MqttReceiveClient(mqttProperties, mqttReceiveExecutor);
        receiveClient.connect();
        receiveClient.check();
        return receiveClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttSendFactory mqttSendFactory(MqttProperties mqttProperties){
        MqttSendFactory factory = new MqttSendFactory(mqttProperties);
        factory.init();
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttTemplate mqttTemplate(MqttSendFactory mqttSendFactory){
        return new MqttTemplate(mqttSendFactory);
    }

}
