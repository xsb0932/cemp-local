package com.landleaf.monitor;

import com.landleaf.bms.api.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Yang
 */
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.landleaf.*")
@MapperScan("com.landleaf.monitor.dal.mapper")
@ConfigurationPropertiesScan
@EnableFeignClients(clients = {ProjectApi.class, CategoryApi.class, ProjectSpaceApi.class, ProductApi.class, IotApi.class})
public class CempMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CempMonitorApplication.class, args);
    }
}
