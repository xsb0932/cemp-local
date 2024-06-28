package com.landleaf.sdl;

import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.energy.api.DeviceElectricityApi;
import com.landleaf.energy.api.ProjectCnfTimePeriodApi;
import com.landleaf.energy.api.SubitemApi;
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
@MapperScan("com.landleaf.**.mapper")
@ConfigurationPropertiesScan
@EnableFeignClients(clients = {SubitemApi.class,
        DeviceHistoryApi.class,
        ProjectCnfTimePeriodApi.class,
        ProjectApi.class,
        DeviceElectricityApi.class,
        AlarmApi.class,
        ProjectWeatherApi.class
})
public class CempCusSdlApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempCusSdlApplication.class, args);
    }

}
