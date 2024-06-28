package com.landleaf.jjgj;


import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.data.api.device.WeatherHistoryApi;
import com.landleaf.energy.api.DeviceElectricityApi;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.monitor.api.DeviceParameterApi;
import com.landleaf.monitor.api.MonitorApi;
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
@EnableFeignClients(clients = {WeatherHistoryApi.class, SubitemApi.class, DeviceElectricityApi.class, DeviceParameterApi.class, DeviceIotApi.class, ProjectApi.class, MonitorApi.class})

public class CempCusJjgjApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempCusJjgjApplication.class, args);
    }

}
