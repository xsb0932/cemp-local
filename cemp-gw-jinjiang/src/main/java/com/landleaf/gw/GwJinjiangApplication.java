package com.landleaf.gw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan("com.landleaf.*")
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
public class GwJinjiangApplication {
    public static void main(String[] args) {
        SpringApplication.run(GwJinjiangApplication.class, args);
    }
}
