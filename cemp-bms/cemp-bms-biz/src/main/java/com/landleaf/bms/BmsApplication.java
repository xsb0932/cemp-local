package com.landleaf.bms;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.messaging.api.ServiceControlApi;
import com.landleaf.monitor.api.HistoryEventApi;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.UserRoleApi;
import com.landleaf.oauth.api.UserRpcApi;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 后台管理服务
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@EnableAsync(proxyTargetClass = true)
@EnableSpringUtil
@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan("com.landleaf.*")
@EnableDiscoveryClient
@MapperScan("com.landleaf.**.mapper")
@EnableFeignClients(clients = {UserRoleApi.class, TenantApi.class, MonitorApi.class, UserRpcApi.class,
        DeviceCurrentApi.class, DeviceHistoryApi.class, HistoryEventApi.class, ServiceControlApi.class
})
public class BmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BmsApplication.class, args);
    }
}
