package com.landleaf.engine;

import com.landleaf.bms.api.*;
import com.landleaf.monitor.api.AlarmApi;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.landleaf.*")
@MapperScan("com.landleaf.engine.dal.mapper")
@ConfigurationPropertiesScan
@EnableFeignClients(clients = {ProductApi.class, DeviceIotApi.class, ProjectApi.class, AlarmApi.class, GatewayApi.class, AlarmPushApi.class})
public class CempEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempEngineApplication.class, args);
    }
}
