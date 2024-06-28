package com.landleaf.lh;

import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.ManagementNodeApi;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.WeatherHistoryApi;
import com.landleaf.energy.api.*;
import com.landleaf.file.api.FileApi;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.api.LhDeviceApi;
import com.landleaf.oauth.api.TenantApi;
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
@MapperScan("com.landleaf.**.mapper")
@ConfigurationPropertiesScan
@EnableFeignClients(clients = {SubitemApi.class,
        DeviceCurrentApi.class,
        DeviceHistoryApi.class,
        ProjectCnfTimePeriodApi.class,
        ProjectApi.class,
        DeviceElectricityApi.class,
        AlarmApi.class,
        ProjectWeatherApi.class,
        PlanedElectricityApi.class,
        PlannedWaterApi.class,
        WeatherHistoryApi.class,
        ProjectWeatherApi.class,
        UserProjectApi.class,
        LhDeviceApi.class,
        ManagementNodeApi.class,
        DictApi.class,
        FileApi.class,
        ManagementNodeApi.class,
        TenantApi.class
})
public class CempCusLhApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempCusLhApplication.class, args);
    }

}
