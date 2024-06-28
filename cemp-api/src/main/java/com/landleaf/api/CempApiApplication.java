package com.landleaf.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.landleaf.*")
@MapperScan("com.landleaf.api.mapper")
@EnableFeignClients
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class CempApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempApiApplication.class, args);
    }
}
