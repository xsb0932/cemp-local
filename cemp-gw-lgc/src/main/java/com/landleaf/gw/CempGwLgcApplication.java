package com.landleaf.gw;

import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.monitor.api.MonitorApi;
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
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(clients = {MonitorApi.class, DeviceCurrentApi.class})
public class CempGwLgcApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempGwLgcApplication.class, args);
    }

}
