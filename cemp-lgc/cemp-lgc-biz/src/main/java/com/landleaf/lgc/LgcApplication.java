package com.landleaf.lgc;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.energy.api.DeviceElectricityApi;
import com.landleaf.energy.api.ProjectCnfTimePeriodApi;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.monitor.api.AlarmApi;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * LGC 服务
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@EnableSpringUtil
@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan("com.landleaf.*")
@EnableDiscoveryClient
@MapperScan("com.landleaf.lgc.dal.mapper")
@EnableFeignClients(clients = {SubitemApi.class,
        DeviceHistoryApi.class,
        ProjectCnfTimePeriodApi.class,
        ProjectApi.class,
        DeviceElectricityApi.class,
        AlarmApi.class,
        ProjectWeatherApi.class,
        DeviceCurrentApi.class
})
public class LgcApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgcApplication.class, args);
    }
}
