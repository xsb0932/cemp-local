package com.landleaf.messaging;

import com.landleaf.bms.api.AlarmPushApi;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.GatewayApi;
import com.landleaf.bms.api.ProductApi;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.api.MonitorApi;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("com.landleaf.messaging.dao")
@EnableFeignClients(clients = {ProductApi.class, GatewayApi.class, DeviceIotApi.class, AlarmApi.class, MonitorApi.class, AlarmPushApi.class})
@EnableDiscoveryClient
@EnableScheduling
public class MessagingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessagingApplication.class, args);
    }
}
