package com.landleaf.web.jackson.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.landleaf.web.jackson.core.databind.LocalDateTimeDeserializer;
import com.landleaf.web.jackson.core.databind.LocalDateTimeSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author eason
 */
@Configuration
public class CempJacksonAutoConfiguration {

    @Bean
    public BeanPostProcessor objectMapperBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof ObjectMapper)) {
                    return bean;
                }
                ObjectMapper objectMapper = (ObjectMapper) bean;
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE)
                        .addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);

                objectMapper.registerModules(simpleModule);

                return bean;
            }
        };
    }
}
