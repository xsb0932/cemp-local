package com.landleaf.mqtt.configure;

import com.landleaf.mqtt.support.DefaultMqttMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt消息转换配置类
 */
@Configuration
@ConditionalOnMissingBean(DefaultMqttMessageConverter.class)
public class MqttMessageConverterConfiguration {

    @Bean
    public DefaultMqttMessageConverter mqttMessageConverter(){
        return new DefaultMqttMessageConverter();
    }
}
