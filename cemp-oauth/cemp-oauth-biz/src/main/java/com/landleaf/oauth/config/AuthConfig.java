package com.landleaf.oauth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AuthConfig
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthConfig {
    @Bean
    SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

}
