package com.landleaf.oauth;

import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.ManagementNodeApi;
import com.landleaf.bms.api.UserManagementNodeApi;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * CempOauthApplication
 *
 * @author 张力方
 * @since 2023/5/31
 **/
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.landleaf.*")
@MapperScan("com.landleaf.**.mapper")
@ConfigurationPropertiesScan
@EnableFeignClients(clients = {ManagementNodeApi.class, UserManagementNodeApi.class, DictApi.class})
public class CempOauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(CempOauthApplication.class, args);
    }
}
