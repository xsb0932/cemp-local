package com.landleaf.file;

import com.landleaf.oauth.api.TenantApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 文件服务
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan("com.landleaf.*")
@EnableDiscoveryClient
@EnableFeignClients(clients = TenantApi.class)
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}
